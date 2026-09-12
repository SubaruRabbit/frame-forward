import { createPhotoImportUseCases } from './PhotoImportUseCases';

test('透传取消、上传进度、失败和重试所需的上传调用', async () => {
  const port = {
    selectJpeg: jest.fn().mockResolvedValue(null),
    upload: jest.fn().mockImplementation(async (_file, progress) => {
      progress(100);
      return 'media-1';
    }),
  };
  const useCases = createPhotoImportUseCases(port);
  await expect(useCases.select()).resolves.toBeNull();
  const progress: number[] = [];
  await expect(
    useCases.upload({ uri: 'file:///a.jpg' }, value => progress.push(value)),
  ).resolves.toBe('media-1');
  expect(progress).toEqual([100]);
  await expect(
    createPhotoImportUseCases({
      ...port,
      upload: jest.fn().mockRejectedValue(new Error('offline')),
    }).upload({ uri: 'file:///a.jpg' }, jest.fn()),
  ).rejects.toThrow('offline');
});
