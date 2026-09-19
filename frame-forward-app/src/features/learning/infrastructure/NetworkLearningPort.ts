import type { NetworkClient } from '@services/api';
import type { LearningPort } from '../application/LearningPort';
import type { CourseDetail, CourseSummary } from '../course';

export const createNetworkLearningPort = (network: NetworkClient): LearningPort => ({
  listCourses: () => network.request<CourseSummary[]>({ path: '/courses' }),
  loadCourse: courseId => network.request<CourseDetail>({ path: `/courses/${courseId}` }),
});
