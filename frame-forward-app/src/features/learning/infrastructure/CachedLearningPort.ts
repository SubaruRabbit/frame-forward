import { cachedP0Courses } from '../courseCache';
import type { LearningPort } from '../application/LearningPort';

export const cachedLearningPort: LearningPort = {
  listCourses: async () =>
    (await cachedP0Courses()).map(course => ({
      id: course.id,
      title: course.title,
      category: course.category,
      contentVersion: course.contentVersion,
      lessonCount: course.chapters.flatMap(chapter => chapter.lessons).length,
    })),
  loadCourse: async courseId => {
    const course = (await cachedP0Courses()).find(item => item.id === courseId);

    if (course == null) {
      throw new Error('未找到缓存课程。');
    }
    return course;
  },
};
