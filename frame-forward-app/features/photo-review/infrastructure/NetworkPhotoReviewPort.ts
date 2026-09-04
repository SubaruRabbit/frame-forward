import type { NetworkClient } from '../../../shared/network/network';
import type { PhotoReviewPort } from '../application/PhotoReviewPort';

export function createNetworkPhotoReviewPort(network: NetworkClient): PhotoReviewPort {
  return {
    reanalyze: (request, idempotencyKey) =>
      network.request({
        path: '/photo-evaluations',
        method: 'POST',
        body: JSON.stringify(request),
        headers: { 'Idempotency-Key': idempotencyKey },
      }),
    getTask: taskId => network.request({ path: `/ai/tasks/${taskId}` }),
  };
}
