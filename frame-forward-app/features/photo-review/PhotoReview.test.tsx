import React from 'react';
import { act, create } from 'react-test-renderer';
import { PhotoReview } from './PhotoReview';
test('无 EXIF 时按固定顺序保留视觉反馈和重新分析动作', () => {
  let tree: ReturnType<typeof create>;
  act(() => {
    tree = create(
      <PhotoReview
        onReanalyze={() => {}}
        result={{
          total: 75,
          dimensions: { 构图: 80, 光线: 70 },
          strengths: ['构图清晰'],
          primaryProblems: ['背景略杂'],
          priorityImprovement: '先整理背景',
          technicalDiagnosis: { certainty: 'INFERENCE', text: '可能由运动或手持导致' },
          exifLimit: '缺少可用 EXIF',
          retakeSteps: ['固定机位', '提高快门'],
        }}
      />,
    );
  });
  const text = JSON.stringify(tree!.toJSON());
  expect(tree!.root.findByProps({ testID: 'exif-limit' })).toBeTruthy();
  expect(tree!.root.findByProps({ testID: 'reanalyze-photo' })).toBeTruthy();
  expect(text.indexOf('画面优点')).toBeLessThan(text.indexOf('主要问题'));
  expect(text).toContain('构图清晰');
});
