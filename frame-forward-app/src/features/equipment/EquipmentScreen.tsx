import React, { useCallback, useEffect, useRef, useState } from 'react';
import { Pressable, StyleSheet, Text, TextInput, View } from 'react-native';
import { EmptyState, FailureState, LoadingState } from '@components/ScreenState';
import type { EquipmentUseCases } from './application/EquipmentUseCases';
import type { EquipmentKind } from './equipmentModel';

export function EquipmentScreen({ useCases }: { useCases: EquipmentUseCases }) {
  const model = useRef(useCases.model);
  const [ready, setReady] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [query, setQuery] = useState('');
  const [pendingRemoval, setPendingRemoval] = useState<string | null>(null);
  const [, redraw] = useState(0);
  const refresh = useCallback(async () => {
    setReady(false);
    setError(null);
    try {
      await useCases.restore();
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : '无法加载器材。');
    } finally {
      setReady(true);
      redraw(value => value + 1);
    }
  }, [useCases]);
  useEffect(() => {
    refresh().catch(() => undefined);
  }, [refresh]);
  if (!ready) return <LoadingState title="正在加载器材" />;
  if (error) return <FailureState title="器材加载失败" message={error} onRetry={() => refresh()} />;
  const current = model.current!;
  const add = async (kind: EquipmentKind, id: string) => {
    try {
      await useCases.add(kind, id);
      redraw(value => value + 1);
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : '添加失败。');
    }
  };
  const remove = async (id: string) => {
    try {
      await useCases.remove(id);
      setPendingRemoval(null);
      redraw(value => value + 1);
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : '移除失败。');
    }
  };
  const makePrimary = async (id: string) => {
    try {
      await useCases.setPrimary(id);
      redraw(value => value + 1);
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : '主力相机切换失败。');
    }
  };
  const choices = current.search(query);
  return (
    <View style={styles.box} testID="equipment-screen">
      <Text style={styles.title}>我的器材</Text>
      <Text style={styles.caption}>先搜索目录，再加入你的器材库。</Text>
      <TextInput
        accessibilityLabel="搜索相机目录"
        value={query}
        onChangeText={setQuery}
        placeholder="搜索品牌或型号"
        style={styles.search}
      />
      {query ? (
        <Pressable accessibilityRole="button" onPress={() => setQuery('')}>
          <Text style={styles.link}>清除搜索</Text>
        </Pressable>
      ) : null}
      {choices.map(item => (
        <View key={item.id} style={styles.row}>
          <Text>
            {item.brand} {item.model}
          </Text>
          <Pressable accessibilityRole="button" onPress={() => add('CAMERA', item.id)}>
            <Text style={styles.action}>添加相机</Text>
          </Pressable>
        </View>
      ))}
      <Text style={styles.sectionTitle}>已拥有</Text>
      {!current.owned.length ? (
        <EmptyState title="还没有器材" message="从相机目录添加第一台相机，它会自动成为主力相机。" />
      ) : (
        current.owned.map(item => (
          <View key={item.id} style={styles.row}>
            <View>
              <Text>
                {item.catalogItemId}
                {item.primary ? ' · 主力相机' : ''}
              </Text>
              {item.kind === 'LENS' && current.incompatibilityFor(item.id) ? (
                <Text style={styles.warning}>{current.incompatibilityFor(item.id)}</Text>
              ) : null}
            </View>
            {item.kind === 'CAMERA' && !item.primary ? (
              <Pressable accessibilityRole="button" onPress={() => makePrimary(item.id)}>
                <Text style={styles.action}>设为主力</Text>
              </Pressable>
            ) : null}
            <Pressable accessibilityRole="button" onPress={() => setPendingRemoval(item.id)}>
              <Text style={styles.danger}>移除</Text>
            </Pressable>
          </View>
        ))
      )}
      {pendingRemoval ? (
        <View accessibilityRole="alert" style={styles.confirm}>
          <Text>移除后将不再用于拍摄建议。</Text>
          <Pressable accessibilityRole="button" onPress={() => remove(pendingRemoval)}>
            <Text style={styles.danger}>确认移除</Text>
          </Pressable>
          <Pressable accessibilityRole="button" onPress={() => setPendingRemoval(null)}>
            <Text style={styles.link}>取消</Text>
          </Pressable>
        </View>
      ) : null}
    </View>
  );
}

const styles = StyleSheet.create({
  box: { borderColor: '#C8D8D4', borderRadius: 16, borderWidth: 1, marginTop: 24, padding: 18 },
  title: { color: '#102A43', fontSize: 20, fontWeight: '800' },
  caption: { color: '#52616B', marginTop: 4 },
  search: { borderColor: '#C8D8D4', borderRadius: 8, borderWidth: 1, marginTop: 14, padding: 10 },
  row: {
    alignItems: 'center',
    borderBottomColor: '#E4ECEA',
    borderBottomWidth: StyleSheet.hairlineWidth,
    flexDirection: 'row',
    gap: 12,
    justifyContent: 'space-between',
    paddingVertical: 12,
  },
  sectionTitle: { color: '#102A43', fontSize: 16, fontWeight: '800', marginTop: 18 },
  action: { color: '#18A999', fontWeight: '800' },
  danger: { color: '#B42318', fontWeight: '800' },
  link: { color: '#102A43', fontWeight: '700', marginTop: 8 },
  warning: { color: '#9A6700', fontSize: 12, marginTop: 4 },
  confirm: { backgroundColor: '#FFF4E5', borderRadius: 8, marginTop: 12, padding: 12 },
});
