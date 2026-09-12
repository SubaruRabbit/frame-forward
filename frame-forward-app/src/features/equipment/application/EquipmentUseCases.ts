import { EquipmentModel, type EquipmentKind } from '../equipmentModel';
import type { EquipmentPort } from './EquipmentPort';

export function createEquipmentUseCases(port: EquipmentPort) {
  const model = new EquipmentModel(port);
  return {
    model,
    restore: () => model.restore(),
    add: (kind: EquipmentKind, catalogItemId: string) => model.add(kind, catalogItemId),
    remove: (id: string) => model.remove(id),
    setPrimary: (id: string) => model.setPrimary(id),
  };
}

export type EquipmentUseCases = ReturnType<typeof createEquipmentUseCases>;
