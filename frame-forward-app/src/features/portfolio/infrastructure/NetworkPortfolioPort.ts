import type { NetworkClient } from '@services/api';
import type { PortfolioPort } from '../application/PortfolioPort';
import type { Work } from '../domain/portfolio';

type WorkDto = Omit<Work, 'workflowContext'> & { workflowContext?: Work['workflowContext'] };

const mapWork = (dto: WorkDto): Work => ({
  ...dto,
  workflowContext: dto.workflowContext
    ? {
        mediaId: dto.workflowContext.mediaId,
        evaluation: dto.workflowContext.evaluation,
        sourcePlan: dto.workflowContext.sourcePlan,
        session: dto.workflowContext.session,
        comparisonCandidates: dto.workflowContext.comparisonCandidates,
      }
    : undefined,
});

export function createNetworkPortfolioPort(network: NetworkClient): PortfolioPort {
  return {
    async list(filter = {}) {
      const query = Object.entries(filter)
        .filter(([, value]) => value)
        .map(([key, value]) => `${key}=${encodeURIComponent(String(value))}`)
        .join('&');
      const response = await network.request<{ items: WorkDto[]; nextCursor: string | null }>({
        path: `/portfolio/works${query ? `?${query}` : ''}`,
      });
      return { ...response, items: response.items.map(mapWork) };
    },
    detail: async id => mapWork(await network.request<WorkDto>({ path: `/portfolio/works/${id}` })),
    favorite: (id, value) =>
      network.request({
        path: `/portfolio/works/${id}`,
        method: 'PUT',
        body: JSON.stringify({ favorite: value }),
      }),
    deleteWork: id => network.request({ path: `/portfolio/works/${id}`, method: 'DELETE' }),
  };
}
