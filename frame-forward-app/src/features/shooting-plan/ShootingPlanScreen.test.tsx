import React from 'react';
import { act, create } from 'react-test-renderer';
import type { ShootingPlanUseCases } from './application/ShootingPlanUseCases';
import { ShootingPlanScreen } from './ShootingPlanScreen';

const plans = [
  {
    label: 'SAFE' as const,
    recommended: true,
    position: '人行道内侧',
    distance: '2 米',
    cameraHeight: '胸口高度',
    orientation: '竖构图',
    focalLengthMm: 35,
    exposure: { aperture: 'f/2.8', shutterSpeed: '1/250s', iso: 400, startingPoint: true },
    metering: '评价测光',
    focus: '单次自动对焦',
    driveMode: '单张',
    whiteBalance: '自动',
    composition: '引导线',
    pose: '自然站立',
    accessoryUse: '无需附件',
    steps: ['确认安全'],
  },
];

test('绑定生成中的状态，并在成功后显示拍摄方案', async () => {
  let resolve!: (value: typeof plans) => void;
  const useCases: ShootingPlanUseCases = {
    generate: jest.fn(() => new Promise<typeof plans>(complete => (resolve = complete))),
  };
  let tree!: ReturnType<typeof create>;
  await act(async () => {
    tree = create(<ShootingPlanScreen sceneAnalysisId="scene-1" useCases={useCases} />);
  });
  await act(async () => {
    tree.root.findByProps({ testID: 'generate-shooting-plan' }).props.onPress();
  });
  expect(tree.root.findByProps({ testID: 'shooting-plan-progress' })).toBeTruthy();
  expect(useCases.generate).toHaveBeenCalledWith(
    'scene-1',
    expect.any(Function),
    expect.any(AbortSignal),
  );

  await act(async () => {
    resolve(plans);
  });
  expect(tree.root.findByProps({ testID: 'shooting-plan-cards' })).toBeTruthy();
});

test('显示可重试的错误状态', async () => {
  const useCases: ShootingPlanUseCases = {
    generate: jest.fn().mockRejectedValue(new Error('服务暂不可用')),
  };
  let tree!: ReturnType<typeof create>;
  await act(async () => {
    tree = create(<ShootingPlanScreen sceneAnalysisId="scene-1" useCases={useCases} />);
  });
  await act(async () => {
    await tree.root.findByProps({ testID: 'generate-shooting-plan' }).props.onPress();
  });
  expect(tree.root.findByProps({ testID: 'shooting-plan-error' }).props.children).toBe(
    '服务暂不可用',
  );
  expect(JSON.stringify(tree.toJSON())).toContain('重试生成拍摄方案');
});
