import type { Filter } from '../domain/portfolio';
import type { PortfolioPort } from './PortfolioPort';

export function createPortfolioUseCases(port: PortfolioPort) {
  return {
    loadWorks: (filter?: Filter) => port.list(filter),
    loadWork: (id: string) => port.detail(id),
    changeFavorite: (id: string, favorite: boolean) => port.favorite(id, favorite),
    deleteWork: (id: string) => port.deleteWork(id),
  };
}

export type PortfolioUseCases = ReturnType<typeof createPortfolioUseCases>;
