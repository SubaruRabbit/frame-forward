import type { ShootingPlanPort } from './ShootingPlanPort';
import { createShootingPlanUseCases } from './ShootingPlanUseCases';

const plans = [
  {
    label: 'SAFE' as const,
    recommended: true,
    position: '人行道内侧',
    distance: '2 米',
    cameraHeight: '胸口高度',
    orientation: '竖构图',
    focalLengthMm: 35,
    exposure: { aperture: 'f/2.8', shutterSpeed: '1/250s', iso: 400, startingPoint: true },
    metering: '评价测光',
    focus: '单次自动对焦',
    driveMode: '单张',
    whiteBalance: '自动',
    composition: '引导线',
    pose: '自然站立',
    accessoryUse: '无需附件',
    steps: ['确认安全', '完成试拍'],
  },
];

test('轮询拍摄方案任务并返回结构化方案', async () => {
  const port: ShootingPlanPort = {
    create: jest.fn().mockResolvedValue({ taskId: 'task-1', state: 'QUEUED' }),
    getTask: jest
      .fn()
      .mockResolvedValueOnce({ taskId: 'task-1', state: 'RUNNING' })
      .mockResolvedValueOnce({ taskId: 'task-1', state: 'SUCCEEDED', result: { plans } }),
  };
  const progress: string[] = [];
  const useCases = createShootingPlanUseCases(port, { waitForNextPoll: async () => undefined });

  await expect(useCases.generate('scene-1', state => progress.push(state))).resolves.toEqual(plans);
  expect(port.create).toHaveBeenCalledWith(
    { sceneAnalysisId: 'scene-1' },
    expect.stringMatching(/^shooting-plan-/),
    undefined,
  );
  expect(progress).toEqual(['queued', 'running']);
});

test('失败、取消与超时均停止轮询并提供可恢复错误', async () => {
  const failedPort: ShootingPlanPort = {
    create: jest.fn().mockResolvedValue({ taskId: 'failed', state: 'FAILED' }),
    getTask: jest.fn(),
  };
  await expect(
    createShootingPlanUseCases(failedPort).generate('scene-1', jest.fn()),
  ).rejects.toThrow('未能生成');

  const controller = new AbortController();
  const cancelledPort: ShootingPlanPort = {
    create: jest.fn().mockResolvedValue({ taskId: 'running', state: 'RUNNING' }),
    getTask: jest.fn(),
  };
  await expect(
    createShootingPlanUseCases(cancelledPort, {
      waitForNextPoll: async () => controller.abort(),
    }).generate('scene-1', jest.fn(), controller.signal),
  ).rejects.toThrow('已取消');
  expect(cancelledPort.getTask).not.toHaveBeenCalled();

  const waitingPort: ShootingPlanPort = {
    create: jest.fn().mockResolvedValue({ taskId: 'waiting', state: 'QUEUED' }),
    getTask: jest.fn().mockResolvedValue({ taskId: 'waiting', state: 'QUEUED' }),
  };
  await expect(
    createShootingPlanUseCases(waitingPort, {
      maxPollAttempts: 1,
      waitForNextPoll: async () => undefined,
    }).generate('scene-1', jest.fn()),
  ).rejects.toThrow('等待超时');
});
