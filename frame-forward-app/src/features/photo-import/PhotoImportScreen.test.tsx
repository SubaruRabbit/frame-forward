import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { PhotoImportScreen } from './PhotoImportScreen';

const useCases = { select: jest.fn(), upload: jest.fn() };

const picker = jest.requireMock('@react-native-documents/picker') as {
  pick: jest.Mock;
  types: { images: string };
};

beforeEach(() => {
  picker.pick.mockReset();
});

test('选择 JPEG 时调用新的文件选择器并保留所选文件', async () => {
  useCases.select.mockResolvedValueOnce({
    name: 'frame.jpeg',
    type: 'image/jpeg',
    uri: 'file:///frame.jpeg',
  });
  let renderer!: ReactTestRenderer.ReactTestRenderer;
  await ReactTestRenderer.act(async () => {
    renderer = ReactTestRenderer.create(<PhotoImportScreen useCases={useCases} />);
  });

  await ReactTestRenderer.act(async () => {
    await renderer.root.findByProps({ testID: 'pick-jpeg' }).props.onPress();
  });

  expect(useCases.select).toHaveBeenCalled();
  expect(renderer.root.findByProps({ testID: 'upload-jpeg' })).toBeTruthy();
});
