import type { SceneAnalysisPort } from './SceneAnalysisPort';
import { createSceneAnalysisUseCases } from './SceneAnalysisUseCases';

const request = {
  environmentMediaId: 'media-1',
  subjectType: 'CUSTOM' as const,
  subject: '人像',
  targetStyle: '自然',
  timeConstraintMinutes: 30,
  equipmentIds: [],
};

const result = {
  sceneAnalysisId: 'scene-1',
  sceneType: '街景',
  subjectCandidates: ['人像'],
  light: { quality: '柔和' },
  backgroundComplexity: '低',
  compositionalStructures: ['引导线'],
  usablePositions: [{ description: '人行道内侧' }],
  accessoryOpportunities: [],
  safetyWarnings: [],
};

test('reports queued and running progress before recovering with a result', async () => {
  const port: SceneAnalysisPort = {
    create: jest.fn().mockResolvedValue({ taskId: 'task-1', state: 'QUEUED' }),
    getTask: jest
      .fn()
      .mockResolvedValueOnce({ taskId: 'task-1', state: 'RUNNING' })
      .mockResolvedValueOnce({ taskId: 'task-1', state: 'SUCCEEDED', result }),
  };
  const progress: string[] = [];
  const useCases = createSceneAnalysisUseCases(port, { waitForNextPoll: async () => undefined });

  await expect(useCases.analyze(request, state => progress.push(state))).resolves.toEqual(result);
  expect(progress).toEqual(['queued', 'running']);
});

test('reports failures and permits a retry', async () => {
  const port: SceneAnalysisPort = {
    create: jest
      .fn()
      .mockResolvedValueOnce({ taskId: 'failed', state: 'FAILED' })
      .mockResolvedValueOnce({ taskId: 'succeeded', state: 'SUCCEEDED', result }),
    getTask: jest.fn(),
  };
  const useCases = createSceneAnalysisUseCases(port);

  await expect(useCases.analyze(request, jest.fn())).rejects.toThrow('结构化结果');
  await expect(useCases.analyze(request, jest.fn())).resolves.toEqual(result);
  expect(port.create).toHaveBeenCalledTimes(2);
});

test('cancels before another polling request is made', async () => {
  const controller = new AbortController();
  const port: SceneAnalysisPort = {
    create: jest.fn().mockResolvedValue({ taskId: 'task-1', state: 'RUNNING' }),
    getTask: jest.fn(),
  };
  const useCases = createSceneAnalysisUseCases(port, {
    waitForNextPoll: async () => controller.abort(),
  });

  await expect(useCases.analyze(request, jest.fn(), controller.signal)).rejects.toThrow('已取消');
  expect(port.getTask).not.toHaveBeenCalled();
});

test('stops polling after the configured retry limit', async () => {
  const port: SceneAnalysisPort = {
    create: jest.fn().mockResolvedValue({ taskId: 'task-1', state: 'QUEUED' }),
    getTask: jest.fn().mockResolvedValue({ taskId: 'task-1', state: 'QUEUED' }),
  };
  const useCases = createSceneAnalysisUseCases(port, {
    maxPollAttempts: 1,
    waitForNextPoll: async () => undefined,
  });

  await expect(useCases.analyze(request, jest.fn())).rejects.toThrow('等待超时');
  expect(port.getTask).toHaveBeenCalledTimes(1);
});
