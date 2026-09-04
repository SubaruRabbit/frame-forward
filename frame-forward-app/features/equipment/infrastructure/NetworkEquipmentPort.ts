import type { NetworkClient } from '../../../shared/network/network';
import { equipmentApi } from '../equipmentApi';
import type { EquipmentPort } from '../application/EquipmentPort';

export const createNetworkEquipmentPort = (network: NetworkClient): EquipmentPort =>
  equipmentApi(network);
