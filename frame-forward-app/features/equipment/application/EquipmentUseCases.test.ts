import { createEquipmentUseCases } from './EquipmentUseCases';

const port = (overrides = {}) => ({
  catalog: jest.fn().mockResolvedValue([]),
  owned: jest.fn().mockResolvedValue([]),
  combinations: jest.fn().mockResolvedValue([]),
  add: jest
    .fn()
    .mockResolvedValue({ id: 'camera-1', kind: 'CAMERA', catalogItemId: 'a', primary: true }),
  remove: jest.fn().mockResolvedValue(undefined),
  setPrimary: jest.fn(),
  ...overrides,
});

test('保留空器材库、失败与重复添加的可恢复行为', async () => {
  const api = port();
  const useCases = createEquipmentUseCases(api);
  await useCases.restore();
  expect(useCases.model.owned).toEqual([]);
  await useCases.add('CAMERA', 'a');
  await useCases.add('CAMERA', 'a');
  expect(api.add).toHaveBeenCalledTimes(2);

  await expect(
    createEquipmentUseCases(
      port({ owned: jest.fn().mockRejectedValue(new Error('离线')) }),
    ).restore(),
  ).rejects.toThrow('离线');
});
