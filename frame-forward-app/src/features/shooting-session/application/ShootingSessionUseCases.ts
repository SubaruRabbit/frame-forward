import type { ShootingSessionPort } from './ShootingSessionPort';

export function createShootingSessionUseCases(port: ShootingSessionPort) {
  let creating = false;
  return {
    async createSession(shootingPlanId: string, planContext: string) {
      if (creating) throw new Error('拍摄任务正在创建中。');
      creating = true;
      try {
        return await port.createSession({ shootingPlanId, planContext });
      } finally {
        creating = false;
      }
    },
    getComparison: (retakeEvaluationId: string) => port.getComparison(retakeEvaluationId),
  };
}

export type ShootingSessionUseCases = ReturnType<typeof createShootingSessionUseCases>;
