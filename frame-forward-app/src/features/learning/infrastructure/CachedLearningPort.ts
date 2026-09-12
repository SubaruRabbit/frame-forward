import { cachedP0Courses } from '../courseCache';
import type { LearningPort } from '../application/LearningPort';

export const cachedLearningPort: LearningPort = { loadCourses: cachedP0Courses };
