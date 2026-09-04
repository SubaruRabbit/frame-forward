import AsyncStorage from '@react-native-async-storage/async-storage';

export type Lesson = { id: string; title: string; objective: string };
export type Course = {
  id: string;
  title: string;
  category: 'BASICS' | 'MIRRORLESS' | 'EQUIPMENT' | 'MODEL';
  contentVersion: string;
  lessons: Lesson[];
};
type Storage = Pick<typeof AsyncStorage, 'getItem' | 'setItem'>;
const KEY = 'learning:p0-courses';
export async function saveP0Courses(courses: Course[], storage: Storage = AsyncStorage) {
  await storage.setItem(KEY, JSON.stringify(courses));
}
export async function cachedP0Lesson(
  courseId: string,
  lessonId: string,
  storage: Storage = AsyncStorage,
): Promise<Lesson | null> {
  const raw = await storage.getItem(KEY);
  const course: Course | undefined = raw
    ? JSON.parse(raw).find((item: Course) => item.id === courseId)
    : undefined;
  return course?.lessons.find(item => item.id === lessonId) ?? null;
}
export async function cachedP0Courses(storage: Storage = AsyncStorage): Promise<Course[]> {
  const raw = await storage.getItem(KEY);
  return raw ? JSON.parse(raw) : [];
}
