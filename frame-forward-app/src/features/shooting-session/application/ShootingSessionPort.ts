import type { RetakeComparison, ShootingSession } from '../ShootingSessionPanel';

export interface ShootingSessionPort {
  createSession(request: { shootingPlanId: string; planContext: string }): Promise<ShootingSession>;
  getComparison(retakeEvaluationId: string): Promise<RetakeComparison>;
}
