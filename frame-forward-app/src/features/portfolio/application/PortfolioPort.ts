import type { DeletionJob, Filter, Work } from '../domain/portfolio';

export interface PortfolioPort {
  list(filter?: Filter): Promise<{ items: Work[]; nextCursor: string | null }>;
  detail(id: string): Promise<Work>;
  favorite(id: string, value: boolean): Promise<{ mediaId: string; favorite: boolean }>;
  deleteWork(id: string): Promise<DeletionJob>;
}
