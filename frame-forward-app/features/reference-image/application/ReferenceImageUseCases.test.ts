import type { ReferenceImagePort } from './ReferenceImagePort';
import { createReferenceImageUseCases } from './ReferenceImageUseCases';

const request = {
  environmentMediaId: 'scene',
  shootingPlanId: 'plan',
  selectedPlan: {
    label: 'SAFE',
    position: '由文本方案提供',
    cameraHeight: '由文本方案提供',
    composition: '由文本方案提供',
    orientation: '由文本方案提供',
    focalLengthMm: 35,
    exposure: { startingPoint: true },
  },
};

test('cancels an in-progress polling operation', async () => {
  const controller = new AbortController();
  const port: ReferenceImagePort = {
    create: jest.fn().mockResolvedValue({ taskId: 'task-1', state: 'RUNNING' }),
    getTask: jest.fn(),
  };
  const useCases = createReferenceImageUseCases(port, {
    waitForNextPoll: async () => controller.abort(),
  });

  await expect(useCases.generate(request, controller.signal)).rejects.toThrow('已取消');
  expect(port.getTask).not.toHaveBeenCalled();
});

test('surfaces a failed generation and allows a new attempt to succeed', async () => {
  const port: ReferenceImagePort = {
    create: jest
      .fn()
      .mockResolvedValueOnce({ taskId: 'failed', state: 'FAILED' })
      .mockResolvedValueOnce({
        taskId: 'succeeded',
        state: 'SUCCEEDED',
        result: { imageUrl: 'generated/reference.jpg' },
      }),
    getTask: jest.fn(),
  };
  const useCases = createReferenceImageUseCases(port, { createIdempotencyKey: () => 'stable-key' });

  await expect(useCases.generate(request)).rejects.toThrow('保留文字方案后重试');
  await expect(useCases.generate(request)).resolves.toMatchObject({
    state: 'SUCCEEDED',
    result: { imageUrl: 'generated/reference.jpg' },
  });
  expect(port.create).toHaveBeenCalledTimes(2);
});
