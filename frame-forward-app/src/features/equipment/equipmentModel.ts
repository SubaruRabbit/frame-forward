export type EquipmentKind = 'CAMERA' | 'LENS' | 'ACCESSORY';
export type CatalogItem = { id: string; brand: string; model: string };
export type OwnedEquipment = {
  id: string;
  kind: EquipmentKind;
  catalogItemId: string;
  nickname?: string | null;
  primary: boolean;
};
export type BodyLensCombination = {
  cameraEquipmentId: string;
  lensEquipmentId: string;
  compatible: boolean;
  cropModeRequired: boolean;
  mode: string;
  defaultEligible: boolean;
};

export interface EquipmentApi {
  catalog(kind: EquipmentKind): Promise<CatalogItem[]>;
  owned(): Promise<OwnedEquipment[]>;
  add(kind: EquipmentKind, catalogItemId: string): Promise<OwnedEquipment>;
  remove(id: string): Promise<void>;
  setPrimary(id: string): Promise<OwnedEquipment>;
  combinations(): Promise<BodyLensCombination[]>;
}

export class EquipmentModel {
  catalog: CatalogItem[] = [];
  owned: OwnedEquipment[] = [];
  combinations: BodyLensCombination[] = [];

  constructor(private readonly api: EquipmentApi) {}

  get primaryCameraId(): string | undefined {
    return this.owned.find(item => item.kind === 'CAMERA' && item.primary)?.id;
  }

  async restore(): Promise<void> {
    const [catalog, owned, combinations] = await Promise.all([
      this.api.catalog('CAMERA'),
      this.api.owned(),
      this.api.combinations(),
    ]);
    this.catalog = catalog;
    this.owned = owned;
    this.combinations = combinations;
  }

  search(query: string): CatalogItem[] {
    const normalized = query.trim().toLowerCase();
    return normalized
      ? this.catalog.filter(item =>
          `${item.brand} ${item.model}`.toLowerCase().includes(normalized),
        )
      : this.catalog;
  }

  async add(kind: EquipmentKind, catalogItemId: string): Promise<void> {
    const item = await this.api.add(kind, catalogItemId);
    this.owned = [...this.owned, item];
    this.combinations = await this.api.combinations();
  }

  async remove(id: string): Promise<void> {
    await this.api.remove(id);
    this.owned = this.owned.filter(item => item.id !== id);
    this.combinations = await this.api.combinations();
  }

  async setPrimary(id: string): Promise<void> {
    const selected = await this.api.setPrimary(id);
    this.owned = this.owned.map(item =>
      item.kind === 'CAMERA' ? { ...item, primary: item.id === selected.id } : item,
    );
  }

  incompatibilityFor(lensEquipmentId: string): string | undefined {
    const incompatible = this.combinations.find(
      item => item.lensEquipmentId === lensEquipmentId && !item.compatible,
    );
    return incompatible ? '该镜头与已拥有机身不兼容，不能作为默认拍摄组合。' : undefined;
  }
}
