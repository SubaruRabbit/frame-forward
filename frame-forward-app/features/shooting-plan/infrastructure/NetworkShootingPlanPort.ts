import type { NetworkClient } from '../../../shared/network/network';
import type { ShootingPlanPort } from '../application/ShootingPlanPort';

export function createNetworkShootingPlanPort(network: NetworkClient): ShootingPlanPort {
  return {
    create: (request, idempotencyKey, signal) =>
      network.request({
        path: '/shooting-plans',
        method: 'POST',
        body: JSON.stringify(request),
        headers: { 'Idempotency-Key': idempotencyKey },
        signal,
      }),
    getTask: (taskId, signal) => network.request({ path: `/ai/tasks/${taskId}`, signal }),
  };
}
