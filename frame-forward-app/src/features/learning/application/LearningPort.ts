import type { Course } from '../courseCache';

export interface LearningPort {
  loadCourses(): Promise<Course[]>;
}
