import React from 'react';
import { act, create } from 'react-test-renderer';
import {
  ShootingSessionPanel,
  type RetakeComparison,
  type SessionPlan,
} from './ShootingSessionPanel';

const plans: SessionPlan[] = [
  { id: 'plan-safe', label: '稳妥方案', summary: '胸口高度 · 35mm' },
  { id: 'plan-atmosphere', label: '氛围方案', summary: '眼睛高度 · 50mm' },
];

const comparison: RetakeComparison = {
  original: {
    mediaId: 'original-media',
    score: 62,
    imageUrl: 'https://example.com/original.jpg',
    exif: { aperture: 'f/2.8', shutterSpeed: '1/125s', iso: '800' },
  },
  retake: { mediaId: 'retake-media', score: 79, imageUrl: 'https://example.com/retake.jpg' },
  scoreDelta: 17,
  dimensionChanges: { 构图: 12, 光线: 5 },
  compositionChanges: ['主体移至三分线交点'],
  parameterChanges: { 快门: { before: '1/125s', after: '1/250s' } },
  improvedProblems: ['背景杂乱'],
  remainingProblems: ['主体欠曝'],
  nextPracticeAdvice: '保持背景距离，并继续检查主体曝光。',
};

test('选择方案并创建其绑定的拍摄任务', () => {
  const createSession = jest.fn();
  let tree: ReturnType<typeof create>;
  act(() => {
    tree = create(<ShootingSessionPanel plans={plans} onCreateSession={createSession} />);
  });
  act(() => {
    tree!.root.findByProps({ testID: 'session-plan-plan-atmosphere' }).props.onPress();
  });
  act(() => {
    tree!.root.findByProps({ testID: 'create-shooting-session' }).props.onPress();
  });
  expect(createSession).toHaveBeenCalledWith('plan-atmosphere');
  expect(
    JSON.stringify(tree!.root.findByProps({ testID: 'selected-session-plan' }).props.children),
  ).toContain('氛围方案');
});

test('可继续选择既有的方案绑定拍摄任务', () => {
  const selectSession = jest.fn();
  let tree: ReturnType<typeof create>;
  act(() => {
    tree = create(
      <ShootingSessionPanel
        plans={plans}
        sessions={[{ id: 'session-1', planId: 'plan-safe', label: '公园人像练习' }]}
        onSelectSession={selectSession}
      />,
    );
  });
  act(() => {
    tree!.root.findByProps({ testID: 'shooting-session-session-1' }).props.onPress();
  });
  expect(selectSession).toHaveBeenCalledWith('session-1');
});

test('完整渲染前后媒体和每个结构化比较类别', () => {
  let tree: ReturnType<typeof create>;
  act(() => {
    tree = create(<ShootingSessionPanel plans={plans} comparison={comparison} />);
  });
  expect(tree!.root.findByProps({ testID: 'original-media' })).toBeTruthy();
  expect(tree!.root.findByProps({ testID: 'retake-media' })).toBeTruthy();
  for (const id of [
    'score-delta',
    'dimension-changes',
    'composition-changes',
    'parameter-changes',
    'improved-problems',
    'remaining-problems',
    'next-practice-advice',
  ]) {
    expect(tree!.root.findByProps({ testID: id })).toBeTruthy();
  }
});

test('缺少可选 EXIF 时显示未提供而不隐藏参数对比', () => {
  let tree: ReturnType<typeof create>;
  act(() => {
    tree = create(<ShootingSessionPanel plans={plans} comparison={comparison} />);
  });
  const text = JSON.stringify(tree!.toJSON());
  expect(tree!.root.findByProps({ testID: 'retake-exif-unavailable' })).toBeTruthy();
  expect(tree!.root.findByProps({ testID: 'parameter-changes' })).toBeTruthy();
  expect(text).toContain('未提供');
});
