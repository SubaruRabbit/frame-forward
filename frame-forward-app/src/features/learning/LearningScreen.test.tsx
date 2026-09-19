import React from 'react';
import { act, create } from 'react-test-renderer';
import type { LearningUseCases } from './application/LearningUseCases';
import { LearningScreen } from './LearningScreen';

test('renders a server-delivered course catalog and its structured detail', async () => {
  const useCases: LearningUseCases = {
    loadCourses: jest.fn().mockResolvedValue([
      {
        id: 'p0-basics',
        title: 'Photography basics',
        category: 'BASICS',
        contentVersion: 'p0-2026-09',
        lessonCount: 1,
      },
    ]),
    loadCourse: jest.fn().mockResolvedValue({
      id: 'p0-basics',
      title: 'Photography basics',
      category: 'BASICS',
      contentVersion: 'p0-2026-09',
      chapters: [
        {
          id: 'chapter-1',
          title: 'Core',
          sequence: 1,
          lessons: [
            {
              id: 'lesson-1',
              title: 'Exposure',
              objective: 'Control light',
              content: 'Use aperture, shutter speed, and ISO intentionally.',
              correctExample: 'Use a faster shutter speed for motion.',
              incorrectExample: 'Use a slow shutter speed for fast action.',
              exercise: {
                question: 'Can higher ISO enable a faster shutter speed?',
                answer: true,
                explanation: 'It can trade image noise for exposure flexibility.',
              },
              assignment: 'Photograph the same scene at three exposure settings.',
            },
          ],
        },
      ],
    }),
  };
  let tree!: ReturnType<typeof create>;

  await act(async () => {
    tree = create(<LearningScreen useCases={useCases} />);
  });
  expect(JSON.stringify(tree.toJSON())).toContain('Photography basics');
  expect(JSON.stringify(tree.toJSON())).toContain('BASICS');
  expect(JSON.stringify(tree.toJSON())).toContain('节课');
  await act(async () => {
    tree.root.findByProps({ testID: 'course-p0-basics' }).props.onPress();
  });

  expect(tree.root.findByProps({ testID: 'course-detail-screen' })).toBeTruthy();
  expect(JSON.stringify(tree.toJSON())).toContain(
    'Use aperture, shutter speed, and ISO intentionally.',
  );
  expect(JSON.stringify(tree.toJSON())).toContain(
    'Photograph the same scene at three exposure settings.',
  );
});

test('retries failed catalog loading and returns to the catalog after detail failure', async () => {
  const useCases: LearningUseCases = {
    loadCourses: jest
      .fn()
      .mockRejectedValueOnce(new Error('网络不可用'))
      .mockResolvedValue([
        { id: 'p0', title: '摄影基础', category: 'BASICS', contentVersion: 'v1', lessonCount: 1 },
      ]),
    loadCourse: jest.fn().mockRejectedValue(new Error('课程已下架')),
  };
  let tree!: ReturnType<typeof create>;

  await act(async () => {
    tree = create(<LearningScreen useCases={useCases} />);
  });
  expect(tree.root.findByProps({ testID: 'failure-state' })).toBeTruthy();
  await act(async () => {
    tree.root
      .find(node => node.props.accessibilityLabel === '重试：课程目录加载失败')
      .props.onPress();
  });
  await act(async () => {
    tree.root.findByProps({ testID: 'course-p0' }).props.onPress();
  });
  expect(tree.root.findByProps({ testID: 'failure-state' })).toBeTruthy();
  await act(async () => {
    tree.root.findByProps({ testID: 'back-to-catalog' }).props.onPress();
  });
  expect(tree.root.findByProps({ testID: 'learning-screen' })).toBeTruthy();
});
