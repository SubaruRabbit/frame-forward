import type { PhotoImportPort, SelectedPhoto } from './PhotoImportPort';
export const createPhotoImportUseCases = (port: PhotoImportPort) => ({
  select: () => port.selectJpeg(),
  upload: (file: SelectedPhoto, onProgress: (value: number) => void) =>
    port.upload(file, onProgress),
});
export type PhotoImportUseCases = ReturnType<typeof createPhotoImportUseCases>;
