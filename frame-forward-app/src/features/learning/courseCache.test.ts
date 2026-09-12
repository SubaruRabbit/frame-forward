import { cachedP0Lesson, saveP0Courses } from './courseCache';

test('opens a cached P0 lesson without requesting generation', async () => {
  const storage = new Map<string, string>();
  await saveP0Courses(
    [
      {
        id: 'p0-basics',
        title: '摄影基础',
        category: 'BASICS',
        contentVersion: 'p0-2026-01',
        lessons: [{ id: 'exposure', title: '曝光', objective: '控制曝光' }],
      },
    ],
    {
      getItem: async key => storage.get(key) ?? null,
      setItem: async (key, value) => {
        storage.set(key, value);
      },
    },
  );
  await expect(
    cachedP0Lesson('p0-basics', 'exposure', {
      getItem: async key => storage.get(key) ?? null,
      setItem: async () => {},
    }),
  ).resolves.toMatchObject({ objective: '控制曝光' });
});
