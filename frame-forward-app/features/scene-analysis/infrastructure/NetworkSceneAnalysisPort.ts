import type { NetworkClient } from '../../../shared/network/network';
import type { SceneAnalysisPort } from '../application/SceneAnalysisPort';

export function createNetworkSceneAnalysisPort(network: NetworkClient): SceneAnalysisPort {
  return {
    create: (request, idempotencyKey, signal) =>
      network.request({
        path: '/scene-analyses',
        method: 'POST',
        body: JSON.stringify(request),
        headers: { 'Idempotency-Key': idempotencyKey },
        signal,
      }),
    getTask: (taskId, signal) => network.request({ path: `/ai/tasks/${taskId}`, signal }),
  };
}
