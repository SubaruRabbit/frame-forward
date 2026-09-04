import { createPhotoReviewUseCases } from './PhotoReviewUseCases';

const result = {
  total: 80,
  dimensions: {},
  strengths: [],
  primaryProblems: [],
  priorityImprovement: '继续练习',
  technicalDiagnosis: { certainty: 'OBSERVATION' as const, text: '正常' },
  retakeSteps: [],
};

test('重分析轮询成功结果并传递会话', async () => {
  const port = {
    reanalyze: jest.fn().mockResolvedValue({ taskId: 'task-1', state: 'RUNNING' }),
    getTask: jest.fn().mockResolvedValue({ taskId: 'task-1', state: 'SUCCEEDED', result }),
  };
  await expect(createPhotoReviewUseCases(port).reanalyze('media-1', 'session-1')).resolves.toEqual(
    result,
  );
  expect(port.reanalyze).toHaveBeenCalledWith(
    { mediaId: 'media-1', sessionId: 'session-1', reanalyze: true },
    expect.any(String),
  );
});

test('失败后允许再次重试', async () => {
  const port = {
    reanalyze: jest
      .fn()
      .mockResolvedValueOnce({ taskId: 'bad', state: 'FAILED' })
      .mockResolvedValueOnce({ taskId: 'good', state: 'SUCCEEDED', result }),
    getTask: jest.fn(),
  };
  const useCases = createPhotoReviewUseCases(port);
  await expect(useCases.reanalyze('media-1')).rejects.toThrow('失败');
  await expect(useCases.reanalyze('media-1')).resolves.toEqual(result);
});
