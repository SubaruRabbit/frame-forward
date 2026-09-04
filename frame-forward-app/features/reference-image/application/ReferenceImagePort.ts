import type { ReferenceImageRequest, ReferenceImageTask } from '../domain/referenceImage';

export interface ReferenceImagePort {
  create(
    request: ReferenceImageRequest,
    idempotencyKey: string,
    signal?: AbortSignal,
  ): Promise<ReferenceImageTask>;
  getTask(taskId: string, signal?: AbortSignal): Promise<ReferenceImageTask>;
}
