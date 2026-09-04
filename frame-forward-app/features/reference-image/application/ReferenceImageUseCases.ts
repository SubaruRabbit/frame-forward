import type { ReferenceImageRequest, ReferenceImageTask } from '../domain/referenceImage';
import type { ReferenceImagePort } from './ReferenceImagePort';

type PollingOptions = {
  createIdempotencyKey?: () => string;
  waitForNextPoll?: (delayMs: number, signal?: AbortSignal) => Promise<void>;
};

const waitFor = (delayMs: number, signal?: AbortSignal) =>
  new Promise<void>((resolve, reject) => {
    if (signal?.aborted) {
      reject(new Error('参考图生成已取消'));
      return;
    }
    const timeout = setTimeout(resolve, delayMs);
    signal?.addEventListener(
      'abort',
      () => {
        clearTimeout(timeout);
        reject(new Error('参考图生成已取消'));
      },
      { once: true },
    );
  });

const assertNotCancelled = (signal?: AbortSignal) => {
  if (signal?.aborted) throw new Error('参考图生成已取消');
};

export function createReferenceImageUseCases(
  port: ReferenceImagePort,
  options: PollingOptions = {},
) {
  const createIdempotencyKey = options.createIdempotencyKey ?? (() => `reference-${Date.now()}`);
  const waitForNextPoll = options.waitForNextPoll ?? waitFor;

  const waitForCompletion = async (
    initialTask: ReferenceImageTask,
    signal?: AbortSignal,
  ): Promise<ReferenceImageTask> => {
    let task = initialTask;
    while (task.state === 'QUEUED' || task.state === 'RUNNING') {
      assertNotCancelled(signal);
      await waitForNextPoll(800, signal);
      assertNotCancelled(signal);
      task = await port.getTask(task.taskId, signal);
    }
    if (task.state === 'FAILED') throw new Error('参考图生成失败，请保留文字方案后重试');
    return task;
  };

  return {
    async generate(request: ReferenceImageRequest, signal?: AbortSignal) {
      assertNotCancelled(signal);
      const task = await port.create(request, createIdempotencyKey(), signal);
      return waitForCompletion(task, signal);
    },
  };
}

export type ReferenceImageUseCases = ReturnType<typeof createReferenceImageUseCases>;
