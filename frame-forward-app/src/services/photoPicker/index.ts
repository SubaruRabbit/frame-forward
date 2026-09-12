import {
  errorCodes,
  isErrorWithCode,
  pick,
  types,
  type DocumentPickerResponse,
} from '@react-native-documents/picker';

export type PickedDocument = DocumentPickerResponse;

export async function pickJpegPhoto(): Promise<PickedDocument | null> {
  try {
    const [document] = await pick({ type: [types.images], allowMultiSelection: false });
    if (!document) return null;
    const isJpeg =
      document.type === 'image/jpeg' ||
      document.name?.toLowerCase().endsWith('.jpg') ||
      document.name?.toLowerCase().endsWith('.jpeg');
    if (!isJpeg) throw new Error('仅支持 JPEG 图片');
    return document;
  } catch (error) {
    if (isErrorWithCode(error) && error.code === errorCodes.OPERATION_CANCELED) return null;
    throw error;
  }
}
