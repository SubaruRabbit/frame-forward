import type { ShootingPlan, ShootingPlanProgress, ShootingPlanTask } from '../domain/shootingPlan';
import type { ShootingPlanPort } from './ShootingPlanPort';

type PollingOptions = {
  createIdempotencyKey?: () => string;
  maxPollAttempts?: number;
  waitForNextPoll?: (delayMs: number, signal?: AbortSignal) => Promise<void>;
};

const waitFor = (delayMs: number, signal?: AbortSignal) =>
  new Promise<void>((resolve, reject) => {
    if (signal?.aborted) {
      reject(new Error('拍摄方案已取消'));
      return;
    }
    const timeout = setTimeout(resolve, delayMs);
    signal?.addEventListener(
      'abort',
      () => {
        clearTimeout(timeout);
        reject(new Error('拍摄方案已取消'));
      },
      { once: true },
    );
  });

const assertNotCancelled = (signal?: AbortSignal) => {
  if (signal?.aborted) throw new Error('拍摄方案已取消');
};

const progressFor = (task: ShootingPlanTask): ShootingPlanProgress =>
  task.state === 'RUNNING' ? 'running' : 'queued';

export function createShootingPlanUseCases(port: ShootingPlanPort, options: PollingOptions = {}) {
  const createIdempotencyKey =
    options.createIdempotencyKey ?? (() => `shooting-plan-${Date.now()}`);
  const maxPollAttempts = options.maxPollAttempts ?? 10;
  const waitForNextPoll = options.waitForNextPoll ?? waitFor;

  return {
    async generate(
      sceneAnalysisId: string,
      onProgress: (progress: ShootingPlanProgress) => void,
      signal?: AbortSignal,
    ): Promise<ShootingPlan[]> {
      assertNotCancelled(signal);
      let task = await port.create({ sceneAnalysisId }, createIdempotencyKey(), signal);
      for (let attempt = 0; attempt <= maxPollAttempts; attempt += 1) {
        assertNotCancelled(signal);
        if (task.state === 'SUCCEEDED' && task.result) return task.result.plans;
        if (task.state === 'FAILED') throw new Error('拍摄方案未能生成，请重试。');
        if (attempt === maxPollAttempts) throw new Error('拍摄方案等待超时，请重试。');
        onProgress(progressFor(task));
        await waitForNextPoll(800, signal);
        assertNotCancelled(signal);
        task = await port.getTask(task.taskId, signal);
      }
      throw new Error('拍摄方案等待超时，请重试。');
    },
  };
}

export type ShootingPlanUseCases = ReturnType<typeof createShootingPlanUseCases>;
