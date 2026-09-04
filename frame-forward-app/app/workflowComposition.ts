import type { NetworkClient } from '../shared/network/network';
import { createPhotoReviewUseCases } from '../features/photo-review/application/PhotoReviewUseCases';
import { createNetworkPhotoReviewPort } from '../features/photo-review/infrastructure/NetworkPhotoReviewPort';
import { createShootingSessionUseCases } from '../features/shooting-session/application/ShootingSessionUseCases';
import { createNetworkShootingSessionPort } from '../features/shooting-session/infrastructure/NetworkShootingSessionPort';

export function createWorkflowUseCases(network: NetworkClient) {
  return {
    photoReview: createPhotoReviewUseCases(createNetworkPhotoReviewPort(network)),
    shootingSession: createShootingSessionUseCases(createNetworkShootingSessionPort(network)),
  };
}
