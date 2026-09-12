import type { NetworkClient } from '@services/api';
import { pickJpegPhoto } from '@services/photoPicker';

import { uploadJpeg } from '../photoUpload';
import type { PhotoImportPort } from '../application/PhotoImportPort';

export const createDevicePhotoImportPort = (client: NetworkClient): PhotoImportPort => ({
  selectJpeg: pickJpegPhoto,
  upload(file, onProgress) {
    return uploadJpeg(file, client, onProgress);
  },
});
