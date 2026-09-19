export type CourseCategory = 'BASICS' | 'MIRRORLESS' | 'EQUIPMENT' | 'MODEL';

export type JudgmentExercise = { question: string; answer: boolean; explanation: string };

export type Lesson = {
  id: string;
  title: string;
  objective: string;
  content: string;
  correctExample: string;
  incorrectExample: string;
  exercise: JudgmentExercise;
  assignment: string;
};

export type Chapter = { id: string; title: string; sequence: number; lessons: Lesson[] };

export type CourseSummary = {
  id: string;
  title: string;
  category: CourseCategory;
  contentVersion: string;
  lessonCount: number;
};

export type CourseDetail = {
  id: string;
  title: string;
  category: CourseCategory;
  contentVersion: string;
  chapters: Chapter[];
};
