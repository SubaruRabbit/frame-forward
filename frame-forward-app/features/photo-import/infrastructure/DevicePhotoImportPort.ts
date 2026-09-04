import { errorCodes, isErrorWithCode, pick, types } from '@react-native-documents/picker';
import type { NetworkClient } from '../../../shared/network/network';
import { uploadJpeg } from '../photoUpload';
import type { DocumentPickerResponse } from '@react-native-documents/picker';
import type { PhotoImportPort } from '../application/PhotoImportPort';
export const createDevicePhotoImportPort = (network: NetworkClient): PhotoImportPort => ({
  async selectJpeg() {
    try {
      const [file] = await pick({ type: [types.images] });
      if (file.hasRequestedType && (/jpe?g$/i.test(file.name ?? '') || file.type === 'image/jpeg'))
        return file;
      throw new Error('请选择 JPEG 文件。');
    } catch (error) {
      if (isErrorWithCode(error) && error.code === errorCodes.OPERATION_CANCELED) return null;
      throw error instanceof Error ? error : new Error('无法打开文件选择器。');
    }
  },
  upload: (file, onProgress) => uploadJpeg(file as DocumentPickerResponse, network, onProgress),
});
