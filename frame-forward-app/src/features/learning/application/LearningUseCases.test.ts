import { createLearningUseCases } from './LearningUseCases';

test('加载缓存课程，并将空与失败状态交给页面处理', async () => {
  await expect(
    createLearningUseCases({ loadCourses: async () => [] }).loadCourses(),
  ).resolves.toEqual([]);
  await expect(
    createLearningUseCases({
      loadCourses: async () => Promise.reject(new Error('缓存不可用')),
    }).loadCourses(),
  ).rejects.toThrow('缓存不可用');
});
