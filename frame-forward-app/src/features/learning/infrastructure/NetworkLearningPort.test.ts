import type { NetworkClient } from '@services/api';
import { createNetworkLearningPort } from './NetworkLearningPort';

test('loads course summaries and detail through the authenticated network client', async () => {
  const request = jest
    .fn()
    .mockResolvedValueOnce([
      { id: 'p0', title: '摄影基础', category: 'BASICS', contentVersion: 'v1', lessonCount: 1 },
    ])
    .mockResolvedValueOnce({
      id: 'p0',
      title: '摄影基础',
      category: 'BASICS',
      contentVersion: 'v1',
      chapters: [],
    });
  const port = createNetworkLearningPort({ request } as NetworkClient);

  await expect(port.listCourses()).resolves.toMatchObject([{ lessonCount: 1 }]);
  await expect(port.loadCourse('p0')).resolves.toMatchObject({ id: 'p0', chapters: [] });
  expect(request).toHaveBeenNthCalledWith(1, { path: '/courses' });
  expect(request).toHaveBeenNthCalledWith(2, { path: '/courses/p0' });
});
