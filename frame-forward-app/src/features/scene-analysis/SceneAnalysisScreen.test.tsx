import React from 'react';
import { act, create } from 'react-test-renderer';
import type { SceneAnalysisUseCases } from './application/SceneAnalysisUseCases';
import { SceneAnalysisScreen } from './SceneAnalysisScreen';

test('renders loading state while the analysis use case is in progress', async () => {
  const useCases: SceneAnalysisUseCases = {
    analyze: jest.fn(() => new Promise(() => {})),
  };
  let tree!: ReturnType<typeof create>;
  await act(async () => {
    tree = create(<SceneAnalysisScreen useCases={useCases} />);
  });
  await act(async () => {
    tree.root.findByProps({ testID: 'scene-media-id' }).props.onChangeText('media-1');
    tree.root.findByProps({ testID: 'scene-subject' }).props.onChangeText('人像');
    tree.root.findByProps({ testID: 'scene-style' }).props.onChangeText('自然');
  });
  await act(async () => {
    tree.root.findByProps({ testID: 'start-scene-analysis' }).props.onPress();
  });
  expect(tree.root.findByProps({ testID: 'scene-progress' })).toBeTruthy();
  expect(useCases.analyze).toHaveBeenCalledWith(
    expect.objectContaining({
      environmentMediaId: 'media-1',
      subject: '人像',
      targetStyle: '自然',
    }),
    expect.any(Function),
    expect.any(AbortSignal),
  );
});
