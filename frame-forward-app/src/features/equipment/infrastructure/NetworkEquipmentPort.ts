import type { NetworkClient } from '@services/api';
import { equipmentApi } from '../equipmentApi';
import type { EquipmentPort } from '../application/EquipmentPort';

export const createNetworkEquipmentPort = (network: NetworkClient): EquipmentPort =>
  equipmentApi(network);
