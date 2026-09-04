import assert from 'node:assert/strict';
import test from 'node:test';

import { findBreakingChanges } from '../scripts/check-compatibility.mjs';

test('reports a removed published path as a breaking change', () => {
  const baseline = { paths: { '/courses': { get: { responses: { '200': {} } } } } };
  const candidate = { paths: {} };

  assert.deepEqual(findBreakingChanges(baseline, candidate), ['Removed path: /courses']);
});
