import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import test from 'node:test';

const contract = JSON.parse(readFileSync(new URL('../openapi-baseline.json', import.meta.url), 'utf8'));

function resolveReference(reference) {
  return reference
    .slice(2)
    .split('/')
    .reduce((value, segment) => value?.[segment.replaceAll('~1', '/').replaceAll('~0', '~')], contract);
}

function collectReferences(value, references = []) {
  if (Array.isArray(value)) value.forEach(item => collectReferences(item, references));
  else if (value && typeof value === 'object') {
    if (typeof value.$ref === 'string') references.push(value.$ref);
    Object.entries(value)
      .filter(([key]) => key !== '$ref')
      .forEach(([, item]) => collectReferences(item, references));
  }
  return references;
}

test('course detail declares observed success and missing-resource responses', () => {
  const operation = contract.paths['/courses/{courseId}'].get;

  assert.ok(operation.responses['200']);
  assert.ok(operation.responses['404']);
  assert.equal(operation.responses['404'].content, undefined);
  assert.deepEqual(operation.security, [{ bearerAuth: [] }]);
});

test('course detail exposes structured chapters while the catalog remains a summary', () => {
  const courseSummary = contract.components.schemas.CourseSummary;
  const courseDetail = contract.components.schemas.CourseDetail;
  const lesson = contract.components.schemas.Lesson;

  assert.deepEqual(courseSummary.required, [
    'id',
    'title',
    'category',
    'contentVersion',
    'lessonCount',
  ]);
  assert.deepEqual(courseSummary.properties.lessonCount, { type: 'integer', minimum: 0 });
  assert.deepEqual(courseDetail.required, ['id', 'title', 'category', 'contentVersion', 'chapters']);
  assert.deepEqual(lesson.required, [
    'id',
    'title',
    'objective',
    'content',
    'correctExample',
    'incorrectExample',
    'exercise',
    'assignment',
  ]);
  assert.deepEqual(courseDetail.properties.chapters.items.$ref, '#/components/schemas/Chapter');
  assert.ok(contract.components.schemas.Chapter);
});

test('authenticated course operations declare unauthorized responses', () => {
  for (const operation of [
    contract.paths['/courses'].get,
    contract.paths['/courses/{courseId}/progress'].get,
    contract.paths['/courses/{courseId}/lessons/{lessonId}/assignments'].post,
  ]) {
    assert.deepEqual(operation.security, [{ bearerAuth: [] }]);
    assert.ok(operation.responses['401']);
  }
});

test('every local schema reference resolves', () => {
  for (const reference of collectReferences(contract)) {
    if (reference.startsWith('#/components/')) assert.notEqual(resolveReference(reference), undefined, reference);
  }
});
