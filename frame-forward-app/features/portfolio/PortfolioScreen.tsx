import React, { useCallback, useEffect, useState } from 'react';
import { Pressable, Text, TextInput, View } from 'react-native';
import type { NetworkClient } from '../../shared/network/network';
export type Availability = {
  exif: boolean;
  evaluation: boolean;
  sourcePlan: boolean;
  retake: boolean;
};
export type Work = {
  mediaId: string;
  width: number;
  height: number;
  subject: string | null;
  camera: string | null;
  lens: string | null;
  favorite: boolean;
  availability: Availability;
  exif?: Record<string, string> | null;
  evaluation?: object | null;
  sourcePlan?: object | null;
  retake?: object | null;
};
export type Filter = { subject?: string; camera?: string; lens?: string; favorite?: boolean };
export type DeletionJob = {
  jobId: string;
  mediaId: string;
  state: 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'FAILED';
  failureReason: string | null;
};
export type PortfolioApi = {
  list(filter?: Filter): Promise<{ items: Work[]; nextCursor: string | null }>;
  detail(id: string): Promise<Work>;
  favorite(id: string, value: boolean): Promise<{ mediaId: string; favorite: boolean }>;
  deleteWork(id: string): Promise<DeletionJob>;
};
export function createPortfolioApi(network: NetworkClient): PortfolioApi {
  return {
    async list(filter = {}) {
      const q = Object.entries(filter)
        .filter(([, v]) => v)
        .map(([k, v]) => `${k}=${encodeURIComponent(String(v))}`)
        .join('&');
      return network.request({ path: `/portfolio/works${q ? `?${q}` : ''}` });
    },
    detail: id => network.request({ path: `/portfolio/works/${id}` }),
    favorite: (id, value) =>
      network.request({
        path: `/portfolio/works/${id}`,
        method: 'PUT',
        body: JSON.stringify({ favorite: value }),
      }),
    deleteWork: id => network.request({ path: `/portfolio/works/${id}`, method: 'DELETE' }),
  };
}
export function PortfolioScreen({ api: client }: { api: PortfolioApi }) {
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
      setItems((await client.list({ camera: camera || undefined, lens: lens || undefined })).items);
    } catch (e) {
      setError(e instanceof Error ? e.message : '作品集读取失败，请重试。');
    }
  }, [camera, client, lens]);
  useEffect(() => {
    load().catch(() => undefined);
  }, [load]);
  const favorite = async (w: Work) => {
    const x = await client.favorite(w.mediaId, !w.favorite);
    setDetail(d => (d?.mediaId === x.mediaId ? { ...d, favorite: x.favorite } : d));
  };
  const remove = async (w: Work) => {
    setConfirming(false);
    try {
      const job = await client.deleteWork(w.mediaId);
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
              client
                .detail(w.mediaId)
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
