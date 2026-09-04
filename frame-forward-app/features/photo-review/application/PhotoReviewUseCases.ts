import type { PhotoEvaluation } from '../PhotoReview';
import type { PhotoReviewPort } from './PhotoReviewPort';

export type ReanalysisState =
  | { kind: 'idle' }
  | { kind: 'loading' }
  | { kind: 'success'; result: PhotoEvaluation }
  | { kind: 'error'; message: string };

export function createPhotoReviewUseCases(port: PhotoReviewPort) {
  let running = false;
  const run = async (mediaId: string, sessionId?: string): Promise<PhotoEvaluation> => {
    if (running) throw new Error('重新分析正在进行中。');
    running = true;
    try {
      let task = await port.reanalyze(
        { mediaId, ...(sessionId ? { sessionId } : {}), reanalyze: true },
        `photo-${Date.now()}`,
      );
      for (let attempts = 0; attempts < 10; attempts += 1) {
        if (task.state === 'SUCCEEDED' && task.result) return task.result;
        if (task.state === 'FAILED') throw new Error('照片重新分析失败，请重试。');
        task = await port.getTask(task.taskId);
      }
      throw new Error('照片重新分析等待超时，请重试。');
    } finally {
      running = false;
    }
  };
  return { reanalyze: run };
}

export type PhotoReviewUseCases = ReturnType<typeof createPhotoReviewUseCases>;
