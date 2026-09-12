import type { PhotoEvaluation } from '../PhotoReview';

export type PhotoEvaluationTask = {
  taskId: string;
  state: 'QUEUED' | 'RUNNING' | 'SUCCEEDED' | 'FAILED';
  result?: PhotoEvaluation;
};

export interface PhotoReviewPort {
  reanalyze(
    request: { mediaId: string; sessionId?: string; reanalyze: true },
    idempotencyKey: string,
  ): Promise<PhotoEvaluationTask>;
  getTask(taskId: string): Promise<PhotoEvaluationTask>;
}
