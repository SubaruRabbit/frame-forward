import type { NetworkClient } from '../../../shared/network/network';
import type { ReferenceImagePort } from '../application/ReferenceImagePort';

export function createNetworkReferenceImagePort(network: NetworkClient): ReferenceImagePort {
  return {
    create: (request, idempotencyKey, signal) =>
      network.request({
        path: '/reference-images',
        method: 'POST',
        headers: { 'Idempotency-Key': idempotencyKey },
        body: JSON.stringify(request),
        signal,
      }),
    getTask: (taskId, signal) => network.request({ path: `/ai/tasks/${taskId}`, signal }),
  };
}
