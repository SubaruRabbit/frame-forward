import React, { useEffect, useRef, useState } from 'react';
import { Pressable, StyleSheet, Text, TextInput, View } from 'react-native';
import type { SceneAnalysisUseCases } from './application/SceneAnalysisUseCases';
import type { SceneAnalysisProgress, SceneResult } from './domain/sceneAnalysis';

const progressCopy: Record<SceneAnalysisProgress, string> = {
  queued: '正在排队分析现场…',
  running: '正在理解光线、空间和构图…',
};

export function SceneAnalysisScreen({
  useCases,
  onCompleted,
}: {
  useCases: SceneAnalysisUseCases;
  onCompleted?: (result: SceneResult) => void;
}) {
  const [mediaId, setMediaId] = useState('');
  const [subject, setSubject] = useState('');
  const [style, setStyle] = useState('');
  const [progress, setProgress] = useState<string | null>(null);
  const [result, setResult] = useState<SceneResult | null>(null);
  const [error, setError] = useState<string | null>(null);
  const analysis = useRef<AbortController | null>(null);
  useEffect(() => () => analysis.current?.abort(), []);
  const submit = async () => {
    if (!mediaId.trim() || !subject.trim() || !style.trim()) {
      setError('请填写现场照片、拍摄对象和目标风格。');
      return;
    }
    analysis.current?.abort();
    const controller = new AbortController();
    analysis.current = controller;
    setError(null);
    setResult(null);
    setProgress(progressCopy.queued);
    try {
      const scene = await useCases.analyze(
        {
          environmentMediaId: mediaId.trim(),
          subjectType: 'CUSTOM',
          subject: subject.trim(),
          targetStyle: style.trim(),
          timeConstraintMinutes: 30,
          equipmentIds: [],
        },
        nextProgress => setProgress(progressCopy[nextProgress]),
        controller.signal,
      );
      if (!controller.signal.aborted) {
        setResult(scene);
        onCompleted?.(scene);
        setProgress(null);
      }
    } catch (reason) {
      if (!controller.signal.aborted) {
        setProgress(null);
        setError(reason instanceof Error ? reason.message : '现场分析失败，请重试。');
      }
    }
  };
  return (
    <View style={styles.box} testID="scene-analysis">
      <Text style={styles.title}>分析拍摄现场</Text>
      <Text style={styles.copy}>填写已上传的现场 JPEG 编号，获得可验证的环境事实与安全提示。</Text>
      <TextInput
        testID="scene-media-id"
        value={mediaId}
        onChangeText={setMediaId}
        placeholder="现场 JPEG 编号"
        style={styles.input}
      />
      <TextInput
        testID="scene-subject"
        value={subject}
        onChangeText={setSubject}
        placeholder="拍摄对象"
        style={styles.input}
      />
      <TextInput
        testID="scene-style"
        value={style}
        onChangeText={setStyle}
        placeholder="目标风格"
        style={styles.input}
      />
      <Pressable
        accessibilityRole="button"
        testID="start-scene-analysis"
        onPress={submit}
        style={styles.button}
      >
        <Text style={styles.buttonText}>开始现场分析</Text>
      </Pressable>
      {progress && <Text testID="scene-progress">{progress}</Text>}
      {error && <Text testID="scene-error">{error}</Text>}
      {result && <SceneResultCards result={result} />}
    </View>
  );
}
function SceneResultCards({ result }: { result: SceneResult }) {
  return (
    <View testID="scene-result">
      <Text style={styles.cardTitle}>现场类型：{result.sceneType}</Text>
      <Text>
        光线：{result.light.quality ?? '未识别'} · {result.light.direction ?? '未识别'}
      </Text>
      <Text>背景复杂度：{result.backgroundComplexity}</Text>
      <Text>构图结构：{result.compositionalStructures.join('、') || '未识别'}</Text>
      <Text>
        可用机位：
        {result.usablePositions.map(item => item.description ?? '可用机位').join('、') ||
          '请现场复核'}
      </Text>
      <Text>可用附件：{result.accessoryOpportunities.join('、') || '无'}</Text>
      {result.safetyWarnings.map(warning => (
        <Text key={warning} style={styles.warning}>
          安全提示：{warning}
        </Text>
      ))}
    </View>
  );
}
const styles = StyleSheet.create({
  box: { borderColor: '#C8D8D4', borderRadius: 16, borderWidth: 1, marginTop: 24, padding: 18 },
  title: { color: '#102A43', fontSize: 18, fontWeight: '800' },
  copy: { color: '#52616B', lineHeight: 20, marginTop: 6 },
  input: {
    backgroundColor: '#FFF',
    borderColor: '#C8D8D4',
    borderRadius: 10,
    borderWidth: 1,
    marginTop: 10,
    padding: 10,
  },
  button: { backgroundColor: '#18A999', borderRadius: 10, marginTop: 12, padding: 12 },
  buttonText: { color: '#FFF', fontWeight: '800', textAlign: 'center' },
  cardTitle: { color: '#102A43', fontWeight: '800' },
  warning: { color: '#B54708', marginTop: 6 },
});
