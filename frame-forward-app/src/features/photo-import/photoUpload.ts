import type { NetworkClient } from '@services/api';

import type { SelectedPhoto } from './application/PhotoImportPort';

export async function uploadJpeg(
  file: SelectedPhoto,
  network: NetworkClient,
  onProgress: (progress: number) => void,
): Promise<string> {
  const form = new FormData();
  form.append('file', {
    uri: file.uri,
    name: file.name ?? 'photo.jpg',
    type: file.type ?? 'image/jpeg',
  } as unknown as Blob);
  const result = await network.request<{ id: string }>({
    path: '/media/jpeg',
    method: 'POST',
    body: form,
  });
  onProgress(100);
  return result.id;
}
