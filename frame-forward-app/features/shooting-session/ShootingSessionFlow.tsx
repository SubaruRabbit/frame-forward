import React, { useState } from 'react';
import { Pressable, Text, View } from 'react-native';
import {
  ShootingSessionPanel,
  type RetakeComparison,
  type SessionPlan,
} from './ShootingSessionPanel';
import type { ShootingSessionUseCases } from './application/ShootingSessionUseCases';

export function ShootingSessionFlow({
  plans,
  useCases,
  retakeEvaluationId,
}: {
  plans: SessionPlan[];
  useCases: ShootingSessionUseCases;
  retakeEvaluationId?: string;
}) {
  const [sessions, setSessions] = useState<Array<{ id: string; planId: string; label: string }>>(
    [],
  );
  const [comparison, setComparison] = useState<RetakeComparison>();
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const createSession = async (planId: string) => {
    if (loading) return;
    const plan = plans.find(item => item.id === planId);
    if (!plan) return;
    setLoading(true);
    setError(null);
    try {
      const session = await useCases.createSession(plan.id, plan.summary);
      setSessions(current => [
        ...current,
        { id: session.id, planId: session.planId, label: plan.label },
      ]);
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : '拍摄任务创建失败，请重试。');
    } finally {
      setLoading(false);
    }
  };
  const loadComparison = async () => {
    if (!retakeEvaluationId || loading) return;
    setLoading(true);
    setError(null);
    try {
      setComparison(await useCases.getComparison(retakeEvaluationId));
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : '重拍对比读取失败，请重试。');
    } finally {
      setLoading(false);
    }
  };
  return (
    <View>
      <ShootingSessionPanel
        plans={plans}
        sessions={sessions}
        comparison={comparison}
        onCreateSession={createSession}
      />
      {retakeEvaluationId ? (
        <Pressable testID="load-retake-comparison" onPress={loadComparison}>
          <Text>查看重拍对比</Text>
        </Pressable>
      ) : null}
      {loading ? <Text testID="shooting-session-loading">处理中…</Text> : null}
      {error ? (
        <View>
          <Text testID="shooting-session-error">{error}</Text>
          <Pressable
            testID="shooting-session-retry"
            onPress={retakeEvaluationId ? loadComparison : () => {}}
          >
            <Text>重试</Text>
          </Pressable>
        </View>
      ) : null}
    </View>
  );
}
