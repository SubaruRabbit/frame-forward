import React from 'react';
import { act, create } from 'react-test-renderer';
import { createReferenceImageUseCases } from './application/ReferenceImageUseCases';
import type { ReferenceImagePort } from './application/ReferenceImagePort';
import { ReferenceImagePanel } from './ReferenceImagePanel';

test('shows progress, result disclosures and restores the text plan after a failed generation', async () => {
  const port: ReferenceImagePort = {
    create: jest.fn(() => new Promise(() => {})),
    getTask: jest.fn(),
  };
  let tree!: ReturnType<typeof create>;
  await act(async () => {
    tree = create(
      <ReferenceImagePanel
        useCases={createReferenceImageUseCases(port)}
        environmentMediaId="scene-media"
        shootingPlanId="plan"
        selectedPlanLabel="SAFE"
      />,
    );
  });
  await act(async () => {
    tree.root.findByProps({ testID: 'generate-reference-image' }).props.onPress();
  });
  expect(tree.root.findByProps({ testID: 'reference-image-progress' })).toBeTruthy();
  expect(JSON.stringify(tree.toJSON())).toContain('构图和氛围');
  expect(JSON.stringify(tree.toJSON())).toContain('完全复现');
  expect(JSON.stringify(tree.toJSON())).toContain('唯一评分标准');
});
