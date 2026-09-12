import type { SceneAnalysisRequest, SceneAnalysisTask } from '../domain/sceneAnalysis';

export interface SceneAnalysisPort {
  create(
    request: SceneAnalysisRequest,
    idempotencyKey: string,
    signal?: AbortSignal,
  ): Promise<SceneAnalysisTask>;
  getTask(taskId: string, signal?: AbortSignal): Promise<SceneAnalysisTask>;
}
