import type { ShootingPlanRequest, ShootingPlanTask } from '../domain/shootingPlan';

export interface ShootingPlanPort {
  create(
    request: ShootingPlanRequest,
    idempotencyKey: string,
    signal?: AbortSignal,
  ): Promise<ShootingPlanTask>;
  getTask(taskId: string, signal?: AbortSignal): Promise<ShootingPlanTask>;
}
