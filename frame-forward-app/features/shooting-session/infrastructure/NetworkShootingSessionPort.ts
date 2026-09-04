import type { NetworkClient } from '../../../shared/network/network';
import type { ShootingSessionPort } from '../application/ShootingSessionPort';

export function createNetworkShootingSessionPort(network: NetworkClient): ShootingSessionPort {
  return {
    createSession: request =>
      network.request({
        path: '/shooting-sessions',
        method: 'POST',
        body: JSON.stringify(request),
      }),
    getComparison: retakeEvaluationId =>
      network.request({ path: `/retake-comparisons/${retakeEvaluationId}` }),
  };
}
