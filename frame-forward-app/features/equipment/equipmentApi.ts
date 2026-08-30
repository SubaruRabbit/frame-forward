import type {BodyLensCombination, CatalogItem, EquipmentApi, OwnedEquipment} from './equipmentModel';

const endpoint = 'http://10.0.2.2:8080';

export function equipmentApi(accessToken: string): EquipmentApi {
  const request = async <T>(path: string, init?: RequestInit): Promise<T> => {
    const response = await fetch(`${endpoint}${path}`, {headers: {Authorization: `Bearer ${accessToken}`, 'Content-Type': 'application/json', ...(init?.headers ?? {})}, ...init});
    if (!response.ok) throw new Error('器材操作未完成，请重试。');
    return response.status === 204 ? undefined as T : response.json() as Promise<T>;
  };
  return {
    catalog: async kind => request<{items: CatalogItem[]}>(kind === 'CAMERA' ? '/catalog/cameras' : kind === 'LENS' ? '/catalog/lenses' : '/catalog/accessories').then(result => result.items),
    owned: async () => request<{items: OwnedEquipment[]}>('/equipment').then(result => result.items),
    add: (kind, catalogItemId) => request<OwnedEquipment>('/equipment', {method: 'POST', body: JSON.stringify({kind, catalogItemId})}),
    remove: id => request<void>(`/equipment/${id}`, {method: 'DELETE'}),
    setPrimary: id => request<OwnedEquipment>(`/equipment/cameras/${id}/primary`, {method: 'PUT'}),
    combinations: async () => request<{items: BodyLensCombination[]}>('/equipment/body-lens-combinations').then(result => result.items),
  };
}
