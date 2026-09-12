import React, { useCallback, useEffect, useState } from 'react';
import { Pressable, Text, TextInput, View } from 'react-native';
import type { PortfolioUseCases } from './application/PortfolioUseCases';
import type { Work } from './domain/portfolio';

export type { Availability, DeletionJob, Filter, Work } from './domain/portfolio';

export function PortfolioScreen({
  useCases,
  onReanalyze,
  onCreateSession,
  onCompare,
}: {
  useCases: PortfolioUseCases;
  onReanalyze?: (input: { mediaId: string; sessionId?: string }) => void;
  onCreateSession?: (input: { shootingPlanId: string; planContext: string }) => void;
  onCompare?: (input: { originalEvaluationId: string; retakeEvaluationId: string }) => void;
}) {
  const [items, setItems] = useState<Work[]>([]),
    [detail, setDetail] = useState<Work | null>(null),
    [camera, setCamera] = useState(''),
    [lens, setLens] = useState(''),
    [error, setError] = useState<string | null>(null),
    [confirming, setConfirming] = useState(false),
    [deletion, setDeletion] = useState<string | null>(null);
  const load = useCallback(async () => {
    try {
      setError(null);
      setItems(
        (await useCases.loadWorks({ camera: camera || undefined, lens: lens || undefined })).items,
      );
    } catch (e) {
      setError(e instanceof Error ? e.message : '作品集读取失败，请重试。');
    }
  }, [camera, lens, useCases]);
  useEffect(() => {
    load().catch(() => undefined);
  }, [load]);
  const favorite = async (w: Work) => {
    const x = await useCases.changeFavorite(w.mediaId, !w.favorite);
    setDetail(d => (d?.mediaId === x.mediaId ? { ...d, favorite: x.favorite } : d));
  };
  const remove = async (w: Work) => {
    setConfirming(false);
    try {
      const job = await useCases.deleteWork(w.mediaId);
      if (job.state === 'COMPLETED') {
        setDeletion('删除完成');
        setDetail(null);
        await load();
      } else setDeletion(job.state === 'FAILED' ? '删除未完成，请重试。' : '正在删除作品…');
    } catch {
      setDeletion('删除未完成，请重试。');
    }
  };
  if (detail)
    return (
      <View testID="portfolio-detail">
        <Text>作品详情</Text>
        <Text>{detail.availability.exif ? 'EXIF 已记录' : 'EXIF 暂不可用'}</Text>
        <Text>{detail.availability.evaluation ? '已有评分' : '评分暂不可用'}</Text>
        <Text>{detail.availability.sourcePlan ? '已有拍摄方案' : '拍摄方案暂不可用'}</Text>
        <Text>{detail.availability.retake ? '已有重拍关联' : '重拍关联暂不可用'}</Text>
        <Pressable
          accessibilityRole="button"
          disabled={!onReanalyze}
          testID="portfolio-reanalyze"
          onPress={() =>
            onReanalyze?.({
              mediaId: detail.mediaId,
              sessionId: detail.workflowContext?.evaluation?.sessionId ?? undefined,
            })
          }
        >
          <Text>重新分析照片</Text>
        </Pressable>
        {detail.workflowContext?.sourcePlan ? (
          <Pressable
            testID="portfolio-create-session"
            onPress={() => onCreateSession?.(detail.workflowContext!.sourcePlan!)}
          >
            <Text>创建拍摄任务</Text>
          </Pressable>
        ) : (
          <Text>拍摄方案暂不可用</Text>
        )}
        {detail.workflowContext?.comparisonCandidates.length &&
        detail.workflowContext.comparisonCandidates.length > 1 ? (
          <Pressable
            testID="portfolio-open-comparison"
            onPress={() =>
              onCompare?.({
                originalEvaluationId: detail.workflowContext!.comparisonCandidates[0].evaluationId,
                retakeEvaluationId: detail.workflowContext!.comparisonCandidates[1].evaluationId,
              })
            }
          >
            <Text>查看重拍对比</Text>
          </Pressable>
        ) : (
          <Text>暂无可比较的同会话作品</Text>
        )}
        {deletion && <Text>{deletion}</Text>}
        <Pressable testID={`portfolio-favorite-${detail.mediaId}`} onPress={() => favorite(detail)}>
          <Text>{detail.favorite ? '已收藏' : '收藏'}</Text>
        </Pressable>
        <Pressable
          testID={`portfolio-delete-${detail.mediaId}`}
          onPress={() => setConfirming(true)}
        >
          <Text>删除作品</Text>
        </Pressable>
        {confirming && (
          <Pressable testID="portfolio-delete-confirm" onPress={() => remove(detail)}>
            <Text>确认删除作品及其分析数据</Text>
          </Pressable>
        )}
      </View>
    );
  return (
    <View testID="portfolio-screen">
      <Text>作品集</Text>
      {deletion && <Text>{deletion}</Text>}
      <TextInput
        testID="portfolio-camera-filter"
        placeholder="相机"
        value={camera}
        onChangeText={setCamera}
      />
      <TextInput
        testID="portfolio-lens-filter"
        placeholder="镜头"
        value={lens}
        onChangeText={setLens}
      />
      <Pressable testID="portfolio-apply-filter" onPress={() => load()}>
        <Text>筛选</Text>
      </Pressable>
      {error && <Text>{error}</Text>}
      {!error && !items.length ? (
        <Text testID="portfolio-empty">还没有作品</Text>
      ) : (
        items.map(w => (
          <Pressable
            key={w.mediaId}
            testID={`portfolio-work-${w.mediaId}`}
            onPress={() =>
              useCases
                .loadWork(w.mediaId)
                .then(setDetail)
                .catch(e => setError(e.message))
            }
          >
            <Text>
              {w.camera ?? '未记录相机'} · {w.lens ?? '未记录镜头'}
            </Text>
          </Pressable>
        ))
      )}
    </View>
  );
}
