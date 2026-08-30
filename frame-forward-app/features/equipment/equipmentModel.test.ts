import {EquipmentModel} from './equipmentModel';

const cameras = [{id: 'sony-a6700', brand: 'Sony', model: 'α6700'}];

test('catalog search, add/remove, primary restore, and incompatible feedback remain coherent', async () => {
  const api = {
    catalog: async () => cameras,
    owned: async () => [{id: 'camera-a', kind: 'CAMERA' as const, catalogItemId: 'sony-a6700', primary: true}, {id: 'camera-b', kind: 'CAMERA' as const, catalogItemId: 'nikon-z8', primary: false}],
    add: async () => ({id: 'lens-a', kind: 'LENS' as const, catalogItemId: 'canon-rf-24-70-l', primary: false}),
    remove: async () => undefined,
    setPrimary: async () => ({id: 'camera-b', kind: 'CAMERA' as const, catalogItemId: 'nikon-z8', primary: true}),
    combinations: async () => [{cameraEquipmentId: 'camera-a', lensEquipmentId: 'lens-a', compatible: false, cropModeRequired: false, mode: 'CROSS_MOUNT', defaultEligible: false}],
  };
  const model = new EquipmentModel(api);

  await model.restore();
  expect(model.search('sony')).toEqual(cameras);
  await model.add('LENS', 'canon-rf-24-70-l');
  expect(model.owned.map(item => item.id)).toContain('lens-a');
  expect(model.incompatibilityFor('lens-a')).toContain('不兼容');
  await model.remove('lens-a');
  expect(model.owned.map(item => item.id)).not.toContain('lens-a');
  await model.setPrimary('camera-b');
  expect(model.primaryCameraId).toBe('camera-b');
});
