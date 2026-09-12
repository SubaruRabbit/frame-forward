import type { LearningPort } from './LearningPort';

export const createLearningUseCases = (port: LearningPort) => ({
  loadCourses: () => port.loadCourses(),
});
export type LearningUseCases = ReturnType<typeof createLearningUseCases>;
