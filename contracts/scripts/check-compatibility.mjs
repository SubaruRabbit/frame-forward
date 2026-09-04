import { readFileSync } from 'node:fs';

const operationNames = new Set(['delete', 'get', 'head', 'options', 'patch', 'post', 'put', 'trace']);

export function findBreakingChanges(baseline, candidate) {
  const changes = [];
  for (const [path, baselinePath] of Object.entries(baseline.paths ?? {})) {
    const candidatePath = candidate.paths?.[path];
    if (!candidatePath) {
      changes.push(`Removed path: ${path}`);
      continue;
    }
    for (const [method, baselineOperation] of Object.entries(baselinePath)) {
      if (!operationNames.has(method)) continue;
      const candidateOperation = candidatePath[method];
      if (!candidateOperation) {
        changes.push(`Removed operation: ${method.toUpperCase()} ${path}`);
        continue;
      }
      for (const status of Object.keys(baselineOperation.responses ?? {})) {
        if (!candidateOperation.responses?.[status])
          changes.push(`Removed response: ${method.toUpperCase()} ${path} ${status}`);
      }
    }
  }
  return changes;
}

if (process.argv[1] === new URL(import.meta.url).pathname) {
  const [baselinePath, candidatePath] = process.argv.slice(2);
  if (!baselinePath || !candidatePath) throw new Error('Usage: check-compatibility <baseline> <candidate>');
  const changes = findBreakingChanges(JSON.parse(readFileSync(baselinePath, 'utf8')), JSON.parse(readFileSync(candidatePath, 'utf8')));
  if (changes.length) {
    console.error(changes.join('\n'));
    process.exitCode = 1;
  }
}
