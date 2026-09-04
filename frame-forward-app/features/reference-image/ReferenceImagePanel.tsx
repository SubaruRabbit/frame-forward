import React, { useState } from 'react';
import { Pressable, StyleSheet, Text, View } from 'react-native';
import type { NetworkClient } from '../../shared/network/network';

type Task = {
  taskId: string;
  state: 'QUEUED' | 'RUNNING' | 'SUCCEEDED' | 'FAILED';
  result?: { imageUrl: string };
  errorCode?: string;
};
export function ReferenceImagePanel({
  network,
  environmentMediaId,
  shootingPlanId,
  selectedPlanLabel,
}: {
  network: NetworkClient;
  environmentMediaId: string;
  shootingPlanId: string;
  selectedPlanLabel: string;
}) {
  const [state, setState] = useState<'text-plan' | 'loading' | 'result'>('text-plan');
  const [error, setError] = useState<string | null>(null);
  const [imageUrl, setImageUrl] = useState<string | null>(null);
  const generate = async () => {
    setState('loading');
    setError(null);
    try {
      const task = await network.request<Task>({
        path: '/reference-images',
        method: 'POST',
        headers: { 'Idempotency-Key': `reference-${Date.now()}` },
        body: JSON.stringify({
          environmentMediaId,
          shootingPlanId,
          selectedPlan: {
            label: selectedPlanLabel,
            position: '由文本方案提供',
            cameraHeight: '由文本方案提供',
            composition: '由文本方案提供',
            orientation: '由文本方案提供',
            focalLengthMm: 35,
            exposure: { startingPoint: true },
          },
        }),
      });
      await poll(task.taskId);
    } catch (reason) {
      setError(reason instanceof Error ? reason.message : '参考图生成失败');
      setState('text-plan');
    }
  };
  const poll = async (taskId: string) => {
    const task = await network.request<Task>({ path: `/ai/tasks/${taskId}` });
    if (task.state === 'SUCCEEDED' && task.result) {
      setImageUrl(task.result.imageUrl);
      setState('result');
      return;
    }
    if (task.state === 'FAILED') throw new Error('参考图生成失败，请保留文字方案后重试');
    setTimeout(() => {
      poll(taskId).catch(reason => {
        setError(reason instanceof Error ? reason.message : '参考图生成失败');
        setState('text-plan');
      });
    }, 800);
  };
  return (
    <View testID="reference-image-panel" style={styles.box}>
      <Text style={styles.title}>参考图</Text>
      {state === 'text-plan' && (
        <Pressable
          testID="generate-reference-image"
          accessibilityRole="button"
          onPress={generate}
          style={styles.button}
        >
          <Text style={styles.buttonText}>生成参考图</Text>
        </Pressable>
      )}
      {state === 'loading' && (
        <Text testID="reference-image-progress">正在生成构图与氛围参考图…</Text>
      )}
      {state === 'result' && (
        <View testID="reference-image-result">
          <Text>{imageUrl}</Text>
        </View>
      )}
      <View testID="reference-image-disclosures">
        <Text>参考图用于表达构图和氛围。</Text>
        <Text>不代表实际拍摄可以完全复现。</Text>
        <Text>不能作为唯一评分标准。</Text>
      </View>
      {error && <Text testID="reference-image-error">{error}</Text>}
    </View>
  );
}
const styles = StyleSheet.create({
  box: { borderColor: '#C8D8D4', borderRadius: 12, borderWidth: 1, marginTop: 12, padding: 12 },
  title: { color: '#102A43', fontWeight: '800' },
  button: { backgroundColor: '#18A999', borderRadius: 10, marginTop: 10, padding: 10 },
  buttonText: { color: '#FFF', fontWeight: '800', textAlign: 'center' },
});
