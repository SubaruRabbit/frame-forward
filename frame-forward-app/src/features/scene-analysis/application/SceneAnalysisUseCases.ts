import type {
  SceneAnalysisProgress,
  SceneAnalysisRequest,
  SceneAnalysisTask,
  SceneResult,
} from '../domain/sceneAnalysis';
import type { SceneAnalysisPort } from './SceneAnalysisPort';

type PollingOptions = {
  createIdempotencyKey?: () => string;
  maxPollAttempts?: number;
  waitForNextPoll?: (delayMs: number, signal?: AbortSignal) => Promise<void>;
};

const waitFor = (delayMs: number, signal?: AbortSignal) =>
  new Promise<void>((resolve, reject) => {
    if (signal?.aborted) {
      reject(new Error('现场分析已取消'));
      return;
    }
    const timeout = setTimeout(resolve, delayMs);
    signal?.addEventListener(
      'abort',
      () => {
        clearTimeout(timeout);
        reject(new Error('现场分析已取消'));
      },
      { once: true },
    );
  });

const assertNotCancelled = (signal?: AbortSignal) => {
  if (signal?.aborted) throw new Error('现场分析已取消');
};

const progressFor = (task: SceneAnalysisTask): SceneAnalysisProgress =>
  task.state === 'RUNNING' ? 'running' : 'queued';

export function createSceneAnalysisUseCases(port: SceneAnalysisPort, options: PollingOptions = {}) {
  const createIdempotencyKey = options.createIdempotencyKey ?? (() => `scene-${Date.now()}`);
  const maxPollAttempts = options.maxPollAttempts ?? 10;
  const waitForNextPoll = options.waitForNextPoll ?? waitFor;

  return {
    async analyze(
      request: SceneAnalysisRequest,
      onProgress: (progress: SceneAnalysisProgress) => void,
      signal?: AbortSignal,
    ): Promise<SceneResult> {
      assertNotCancelled(signal);
      let task = await port.create(request, createIdempotencyKey(), signal);
      for (let attempt = 0; attempt <= maxPollAttempts; attempt += 1) {
        assertNotCancelled(signal);
        if (task.state === 'SUCCEEDED' && task.result) return task.result;
        if (task.state === 'FAILED') throw new Error('现场分析未能生成结构化结果，请重试。');
        if (attempt === maxPollAttempts) throw new Error('现场分析等待超时，请重试。');
        onProgress(progressFor(task));
        await waitForNextPoll(800, signal);
        assertNotCancelled(signal);
        task = await port.getTask(task.taskId, signal);
      }
      throw new Error('现场分析等待超时，请重试。');
    },
  };
}

export type SceneAnalysisUseCases = ReturnType<typeof createSceneAnalysisUseCases>;
