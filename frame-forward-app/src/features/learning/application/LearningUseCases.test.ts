import { createLearningUseCases } from './LearningUseCases';

test('加载缓存课程，并将空与失败状态交给页面处理', async () => {
  await expect(
    createLearningUseCases({
      listCourses: async () => [],
      loadCourse: async () => Promise.reject(),
    }).loadCourses(),
  ).resolves.toEqual([]);
  await expect(
    createLearningUseCases({
      listCourses: async () => Promise.reject(new Error('课程目录不可用')),
      loadCourse: async () => Promise.reject(),
    }).loadCourses(),
  ).rejects.toThrow('课程目录不可用');
});
