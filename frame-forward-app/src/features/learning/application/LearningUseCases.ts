import type { LearningPort } from './LearningPort';

export const createLearningUseCases = (port: LearningPort) => ({
  loadCourses: () => port.listCourses(),
  loadCourse: (courseId: string) => port.loadCourse(courseId),
});
export type LearningUseCases = ReturnType<typeof createLearningUseCases>;
