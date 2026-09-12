import { createPhotoReviewUseCases, createNetworkPhotoReviewPort } from '@features/photo-review';
import type { NetworkClient } from '@services/api';
import {
  createNetworkShootingSessionPort,
  createShootingSessionUseCases,
} from '@features/shooting-session';

export function createWorkflowUseCases(network: NetworkClient) {
  return {
    photoReview: createPhotoReviewUseCases(createNetworkPhotoReviewPort(network)),
    shootingSession: createShootingSessionUseCases(createNetworkShootingSessionPort(network)),
  };
}
