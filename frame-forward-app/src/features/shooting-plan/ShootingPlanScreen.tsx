import React, { useEffect, useRef, useState } from 'react';
import { Pressable, StyleSheet, Text, View } from 'react-native';
import type { ShootingPlanUseCases } from './application/ShootingPlanUseCases';
import type { ShootingPlan, ShootingPlanProgress } from './domain/shootingPlan';
import { ShootingPlanCards } from './ShootingPlanCards';

const progressCopy: Record<ShootingPlanProgress, string> = {
  queued: '正在排队生成拍摄方案…',
  running: '正在匹配机位、参数与构图…',
};

export function ShootingPlanScreen({
  sceneAnalysisId,
  useCases,
}: {
  sceneAnalysisId: string;
  useCases: ShootingPlanUseCases;
}) {
  const [progress, setProgress] = useState<string | null>(null);
  const [plans, setPlans] = useState<ShootingPlan[] | null>(null);
  const [error, setError] = useState<string | null>(null);
  const generation = useRef<AbortController | null>(null);
  useEffect(() => () => generation.current?.abort(), []);
  const generate = async () => {
    generation.current?.abort();
    const controller = new AbortController();
    generation.current = controller;
    setError(null);
    setPlans(null);
    setProgress(progressCopy.queued);
    try {
      const nextPlans = await useCases.generate(
        sceneAnalysisId,
        nextProgress => setProgress(progressCopy[nextProgress]),
        controller.signal,
      );
      if (!controller.signal.aborted) {
        setPlans(nextPlans);
        setProgress(null);
      }
    } catch (reason) {
      if (!controller.signal.aborted) {
        setProgress(null);
        setError(reason instanceof Error ? reason.message : '拍摄方案生成失败，请重试。');
      }
    }
  };
  return (
    <View style={styles.box} testID="shooting-plan-screen">
      <Text style={styles.title}>生成拍摄方案</Text>
      <Text style={styles.copy}>基于刚完成的现场分析，生成可执行的机位、参数与构图建议。</Text>
      <Pressable
        accessibilityRole="button"
        onPress={generate}
        style={styles.button}
        testID="generate-shooting-plan"
      >
        <Text style={styles.buttonText}>{error ? '重试生成拍摄方案' : '生成拍摄方案'}</Text>
      </Pressable>
      {progress && <Text testID="shooting-plan-progress">{progress}</Text>}
      {error && <Text testID="shooting-plan-error">{error}</Text>}
      {plans && <ShootingPlanCards plans={plans} />}
    </View>
  );
}

const styles = StyleSheet.create({
  box: { borderColor: '#C8D8D4', borderRadius: 16, borderWidth: 1, marginTop: 16, padding: 18 },
  title: { color: '#102A43', fontSize: 18, fontWeight: '800' },
  copy: { color: '#52616B', lineHeight: 20, marginTop: 6 },
  button: { backgroundColor: '#18A999', borderRadius: 10, marginTop: 12, padding: 12 },
  buttonText: { color: '#FFF', fontWeight: '800', textAlign: 'center' },
});
