import fs from 'node:fs';
import path from 'node:path';
import ts from 'typescript';

const root = process.cwd();
const readJson = file => JSON.parse(fs.readFileSync(path.join(root, file), 'utf8'));
const manifestPath = path.join(root, 'architecture/legacy-migration.json');
const manifest = fs.existsSync(manifestPath)
  ? JSON.parse(fs.readFileSync(manifestPath, 'utf8'))
  : null;
const legacy = new Set(manifest?.files ?? []);
const sourceExtensions = ['.ts', '.tsx', '.js', '.jsx'];
const restrictedPackages = new Set([
  '@react-native-async-storage/async-storage',
  '@react-native-documents/picker',
  'react-native-keychain',
]);

const matrix = {
  app: new Set([
    'app',
    'feature',
    'domain',
    'component',
    'hook',
    'util',
    'theme',
    'service',
    'contract',
    'config',
    'i18n',
  ]),
  feature: new Set([
    'domain',
    'component',
    'hook',
    'util',
    'theme',
    'service',
    'contract',
    'config',
    'i18n',
  ]),
  domain: new Set(['domain', 'contract', 'util']),
  component: new Set(['component', 'hook', 'util', 'theme', 'contract', 'i18n']),
  hook: new Set(['hook', 'util', 'service', 'contract']),
  util: new Set(['util', 'contract']),
  theme: new Set(['theme', 'contract']),
  service: new Set(['service', 'contract', 'config', 'util']),
  contract: new Set(['contract']),
  config: new Set(['config', 'contract']),
  i18n: new Set(['i18n', 'contract']),
};

function walk(directory) {
  if (!fs.existsSync(directory)) return [];
  return fs.readdirSync(directory, { withFileTypes: true }).flatMap(entry => {
    const file = path.join(directory, entry.name);
    if (entry.isDirectory()) return walk(file);
    return sourceExtensions.includes(path.extname(entry.name)) ? [file] : [];
  });
}

function relative(file) {
  return path.relative(root, file).split(path.sep).join('/');
}

function describe(file) {
  const rel = relative(file);
  const test = /(?:^|\/)(?:__tests__\/|[^/]+\.(?:test|spec)\.[jt]sx?$)/.test(rel);
  if (rel === 'App.tsx' || rel === 'index.js' || rel.startsWith('__tests__/'))
    return { region: 'app', module: 'app', test, rel };
  if (rel.startsWith('features/'))
    return { region: 'feature', module: rel.split('/')[1], test, rel, legacy: true };
  const match = rel.match(
    /^src\/(app|features|domains|components|hooks|utils|theme|services|contracts|config|i18n)(?:\/([^/]+))?/,
  );
  if (!match) return { region: null, module: null, test, rel };
  const region =
    {
      features: 'feature',
      domains: 'domain',
      components: 'component',
      hooks: 'hook',
      utils: 'util',
      services: 'service',
      contracts: 'contract',
    }[match[1]] ?? match[1];
  const modularRegion = ['feature', 'domain', 'service', 'component'].includes(region);
  return { region, module: modularRegion ? match[2] ?? region : region, test, rel };
}

function suffixes(platform) {
  return platform === 'ios' ? ['.ios', '.native', ''] : ['.android', '.native', ''];
}

const aliases = {
  '@app': 'src/app',
  '@features': 'src/features',
  '@domains': 'src/domains',
  '@components': 'src/components',
  '@hooks': 'src/hooks',
  '@utils': 'src/utils',
  '@theme': 'src/theme',
  '@services': 'src/services',
  '@contracts': 'src/contracts',
  '@config': 'src/config',
  '@i18n': 'src/i18n',
};

function resolveImport(from, specifier, platform) {
  let base;
  if (specifier.startsWith('.')) base = path.resolve(path.dirname(from), specifier);
  else {
    const alias = Object.keys(aliases).find(
      key => specifier === key || specifier.startsWith(`${key}/`),
    );
    if (!alias) return null;
    base = path.join(
      root,
      aliases[alias],
      specifier.slice(alias.length + (specifier === alias ? 0 : 1)),
    );
  }
  const candidates = [];
  if (path.extname(base)) candidates.push(base);
  else
    for (const suffix of suffixes(platform))
      for (const extension of sourceExtensions) candidates.push(`${base}${suffix}${extension}`);
  for (const suffix of suffixes(platform))
    for (const extension of sourceExtensions)
      candidates.push(path.join(base, `index${suffix}${extension}`));
  return candidates.find(fs.existsSync) ?? false;
}

function importsOf(file) {
  const text = fs.readFileSync(file, 'utf8');
  const source = ts.createSourceFile(file, text, ts.ScriptTarget.Latest, true);
  const imports = [];
  const errors = [];
  function visit(node) {
    if ((ts.isImportDeclaration(node) || ts.isExportDeclaration(node)) && node.moduleSpecifier) {
      imports.push({
        specifier: node.moduleSpecifier.text,
        typeOnly: Boolean(node.importClause?.isTypeOnly),
      });
    }
    if (
      ts.isCallExpression(node) &&
      (node.expression.kind === ts.SyntaxKind.ImportKeyword ||
        (ts.isIdentifier(node.expression) && node.expression.text === 'require'))
    ) {
      if (node.arguments.length !== 1 || !ts.isStringLiteralLike(node.arguments[0]))
        errors.push('dynamic import/require must use one static string literal');
      else imports.push({ specifier: node.arguments[0].text, typeOnly: false });
    }
    if (
      ts.isCallExpression(node) &&
      ts.isIdentifier(node.expression) &&
      node.expression.text === 'fetch'
    )
      errors.push('direct fetch is restricted to src/services/api');
    ts.forEachChild(node, visit);
  }
  visit(source);
  return { imports, errors };
}

function publicEntry(target) {
  const info = describe(target);
  if (!info.region) return true;
  if (['app', 'domain', 'service', 'feature'].includes(info.region))
    return /\/index\.[jt]sx?$/.test(info.rel) || info.rel === 'src/app/testing.ts';
  return info.rel.split('/').length <= 3 || /\/index\.[jt]sx?$/.test(info.rel);
}

function hasCycle(edges) {
  const graph = new Map();
  for (const [from, to] of edges) graph.set(from, [...(graph.get(from) ?? []), to]);
  const active = new Set(),
    done = new Set();
  function visit(node) {
    if (active.has(node)) return true;
    if (done.has(node)) return false;
    active.add(node);
    if ((graph.get(node) ?? []).some(visit)) return true;
    active.delete(node);
    done.add(node);
    return false;
  }
  return [...graph.keys()].some(visit);
}

function legacyMigrationErrors(actualLegacy, migrationManifest) {
  if (!migrationManifest)
    return actualLegacy.length
      ? ['root features/** source files are forbidden in strict mode']
      : [];
  const errors = [];
  const declaredLegacy = [...migrationManifest.files].sort();
  if (JSON.stringify(actualLegacy) !== JSON.stringify(declaredLegacy))
    errors.push('legacy migration manifest must exactly match features/** source files');
  if (
    !migrationManifest.approvedBy ||
    !migrationManifest.cleanupChange ||
    !migrationManifest.mergeAndReleaseBlocked
  )
    errors.push('legacy migration approval metadata is incomplete');
  if (new Date(`${migrationManifest.expiresOn}T23:59:59Z`) < new Date())
    errors.push(`legacy migration exception expired on ${migrationManifest.expiresOn}`);
  return errors;
}

function runFixtures() {
  const fixtures = readJson('architecture/fixtures.json');
  const failures = [];
  for (const item of fixtures.matrix)
    if (matrix[item.from]?.has(item.to) !== item.allowed) failures.push(item.name);
  for (const item of fixtures.restrictedSdk)
    if ((item.test || item.region === 'service') !== item.allowed) failures.push(item.name);
  for (const item of fixtures.cycles)
    if (!hasCycle(item.edges) !== item.allowed) failures.push(item.name);
  for (const item of fixtures.legacyMigration)
    if ((legacyMigrationErrors(item.actual, item.manifest).length === 0) !== item.allowed)
      failures.push(item.name);
  if (failures.length) throw new Error(`Architecture fixture failures: ${failures.join(', ')}`);
  console.log(
    `Architecture fixtures passed (${
      fixtures.matrix.length +
      fixtures.restrictedSdk.length +
      fixtures.cycles.length +
      fixtures.legacyMigration.length
    }).`,
  );
}

function runProject() {
  const files = [
    path.join(root, 'App.tsx'),
    path.join(root, 'index.js'),
    ...walk(path.join(root, '__tests__')),
    ...walk(path.join(root, 'src')),
    ...walk(path.join(root, 'features')),
  ].filter(fs.existsSync);
  const actualLegacy = files
    .map(relative)
    .filter(file => file.startsWith('features/'))
    .sort();
  const errors = [],
    warnings = [];
  errors.push(...legacyMigrationErrors(actualLegacy, manifest));
  for (const platform of ['android', 'ios']) {
    const runtimeEdges = [];
    for (const file of files) {
      const from = describe(file);
      const parsed = importsOf(file);
      for (const message of parsed.errors)
        if (!(message.startsWith('direct fetch') && from.rel.startsWith('src/services/api/')))
          errors.push(`${platform}: ${from.rel}: ${message}`);
      for (const imported of parsed.imports) {
        if (restrictedPackages.has(imported.specifier) && !from.test && from.region !== 'service')
          errors.push(
            `${platform}: ${from.rel}: restricted SDK ${imported.specifier} must be behind src/services`,
          );
        const target = resolveImport(file, imported.specifier, platform);
        if (target === false) {
          errors.push(`${platform}: ${from.rel}: cannot resolve ${imported.specifier}`);
          continue;
        }
        if (!target) continue;
        const to = describe(target);
        if (!from.region || !to.region) continue;
        if (!from.test && to.test)
          errors.push(`${platform}: ${from.rel}: production code imports test code ${to.rel}`);
        const legacyEdge = from.legacy || to.legacy;
        const sameModule = from.region === to.region && from.module === to.module;
        if (!sameModule && !matrix[from.region]?.has(to.region)) {
          if ((legacyEdge && legacy.has(from.rel)) || (legacyEdge && legacy.has(to.rel)))
            warnings.push(`${platform}: approved legacy edge ${from.rel} -> ${to.rel}`);
          else
            errors.push(
              `${platform}: forbidden dependency ${from.region} -> ${to.region}: ${from.rel} -> ${to.rel}`,
            );
        }
        if (!sameModule && !legacyEdge && !publicEntry(target))
          errors.push(
            `${platform}: ${from.rel}: cross-boundary import must target a public entry: ${to.rel}`,
          );
        if (!imported.typeOnly) runtimeEdges.push([from.rel, to.rel]);
      }
    }
    if (hasCycle(runtimeEdges)) errors.push(`${platform}: runtime dependency cycle detected`);
  }
  [...new Set(warnings)].forEach(message => console.warn(`WARNING ${message}`));
  if (errors.length) {
    errors.forEach(message => console.error(`ERROR ${message}`));
    process.exitCode = 1;
    return;
  }
  console.log(`Architecture check passed for ${files.length} files on Android and iOS.`);
}

if (process.argv.includes('--fixtures')) runFixtures();
if (process.argv.includes('--project')) runProject();
