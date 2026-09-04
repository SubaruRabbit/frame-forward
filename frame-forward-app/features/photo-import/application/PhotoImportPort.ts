export type SelectedPhoto = { name?: string | null; type?: string | null; uri: string };
export interface PhotoImportPort {
  selectJpeg(): Promise<SelectedPhoto | null>;
  upload(file: SelectedPhoto, onProgress: (value: number) => void): Promise<string>;
}
