import type { CourseDetail, CourseSummary } from '../course';

export interface LearningPort {
  listCourses(): Promise<CourseSummary[]>;
  loadCourse(courseId: string): Promise<CourseDetail>;
}
