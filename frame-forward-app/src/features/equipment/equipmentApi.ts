import type {
  BodyLensCombination,
  CatalogItem,
  EquipmentApi,
  OwnedEquipment,
} from './equipmentModel';
import type { NetworkClient } from '@services/api';

export function equipmentApi(network: NetworkClient): EquipmentApi {
  const request = <T>(path: string, method?: string, body?: string) =>
    network.request<T>({ path, method, body });
  return {
    catalog: async kind =>
      request<{ items: CatalogItem[] }>(
        kind === 'CAMERA'
          ? '/catalog/cameras'
          : kind === 'LENS'
          ? '/catalog/lenses'
          : '/catalog/accessories',
      ).then(result => result.items),
    owned: async () =>
      request<{ items: OwnedEquipment[] }>('/equipment').then(result => result.items),
    add: (kind, catalogItemId) =>
      request<OwnedEquipment>('/equipment', 'POST', JSON.stringify({ kind, catalogItemId })),
    remove: id => request<void>(`/equipment/${id}`, 'DELETE'),
    setPrimary: id => request<OwnedEquipment>(`/equipment/cameras/${id}/primary`, 'PUT'),
    combinations: async () =>
      request<{ items: BodyLensCombination[] }>('/equipment/body-lens-combinations').then(
        result => result.items,
      ),
  };
}
