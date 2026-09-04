import type { DocumentPickerResponse } from '@react-native-documents/picker';
import type { NetworkClient } from '../../shared/network/network';

export async function uploadJpeg(
  file: DocumentPickerResponse,
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
