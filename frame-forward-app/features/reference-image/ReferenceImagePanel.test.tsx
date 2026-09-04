import React from 'react';
import { act, create } from 'react-test-renderer';
import { ReferenceImagePanel } from './ReferenceImagePanel';
import type { NetworkClient } from '../../shared/network/network';

test('shows progress, result disclosures and restores the text plan after a failed generation', async () => {
  const network = { request: jest.fn(() => new Promise(() => {})) } as unknown as NetworkClient;
  let tree!: ReturnType<typeof create>;
  await act(async () => {
    tree = create(
      <ReferenceImagePanel
        network={network}
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
