import React, { useState } from 'react';
import { Pressable, StyleSheet, Text, View } from 'react-native';
import type { PhotoImportUseCases } from './application/PhotoImportUseCases';
import type { SelectedPhoto } from './application/PhotoImportPort';
export function PhotoImportScreen({ useCases }: { useCases: PhotoImportUseCases }) {
  const [selected, setSelected] = useState<SelectedPhoto | null>(null);
  const [progress, setProgress] = useState<number | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [complete, setComplete] = useState(false);
  const select = async () => {
    try {
      const file = await useCases.select();
      if (!file) return;
      setSelected(file);
      setError(null);
      setComplete(false);
    } catch (e) {
      setError(e instanceof Error ? e.message : '无法打开文件选择器。');
    }
  };
  const send = async () => {
    if (!selected) return;
    setError(null);
    setProgress(0);
    try {
      await useCases.upload(selected, setProgress);
      setComplete(true);
    } catch (e) {
      setProgress(null);
      setError(e instanceof Error ? e.message : '上传失败，请重试。');
    }
  };
  return (
    <View style={styles.box} testID="photo-import">
      <Text style={styles.title}>导入 JPEG 照片</Text>
      <Text>{selected?.name ?? '从设备选择不超过 50 MB 的 JPEG'}</Text>
      <Pressable accessibilityRole="button" onPress={select} testID="pick-jpeg">
        <Text>选择 JPEG</Text>
      </Pressable>
      {selected && (
        <Pressable accessibilityRole="button" onPress={send} testID="upload-jpeg">
          <Text>上传照片</Text>
        </Pressable>
      )}
      {progress !== null && <Text testID="upload-progress">上传进度 {progress}%</Text>}
      {error && (
        <>
          <Text testID="upload-error">{error}</Text>
          <Pressable accessibilityRole="button" onPress={send} testID="retry-upload">
            <Text>重试上传</Text>
          </Pressable>
        </>
      )}
      {complete && <Text testID="upload-complete">上传完成</Text>}
    </View>
  );
}
const styles = StyleSheet.create({
  box: { borderColor: '#C8D8D4', borderRadius: 16, borderWidth: 1, marginTop: 24, padding: 18 },
  title: { color: '#102A43', fontSize: 18, fontWeight: '800' },
});
