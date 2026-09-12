import React from 'react';
import ReactTestRenderer from 'react-test-renderer';

import { FailureState } from './ScreenState';

test('announces the state and exposes a named, scalable retry action', async () => {
  let view!: ReactTestRenderer.ReactTestRenderer;
  await ReactTestRenderer.act(async () => {
    view = ReactTestRenderer.create(
      <FailureState message="请检查网络后再试" onRetry={jest.fn()} title="加载失败" />,
    );
  });

  const state = view.root.findByProps({ testID: 'failure-state' });
  expect(state.props).toMatchObject({
    accessible: true,
    accessibilityLabel: '加载失败：请检查网络后再试',
    accessibilityLiveRegion: 'polite',
    accessibilityRole: 'alert',
  });
  const retry = view.root.findByProps({ accessibilityRole: 'button' });
  expect(retry.props.accessibilityLabel).toBe('重试：加载失败');
  expect(view.root.findAllByProps({ allowFontScaling: true }).length).toBeGreaterThanOrEqual(3);
});
