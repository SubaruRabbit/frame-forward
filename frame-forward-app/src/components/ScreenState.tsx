import React from 'react';
import { ActivityIndicator, Pressable, StyleSheet, Text, View } from 'react-native';

import { commonMessages } from '@i18n';
import { colors, radii, spacing } from '@theme/tokens';

type StateKind = 'loading' | 'empty' | 'denied' | 'failure';
type ScreenStateProps = { kind: StateKind; title: string; message?: string; onRetry?: () => void };
const stateLabels: Record<StateKind, string> = {
  loading: '正在加载',
  empty: '暂无内容',
  denied: '无权访问',
  failure: '需要处理',
};

export function ScreenState({ kind, title, message, onRetry }: ScreenStateProps) {
  const announcement = message ? `${title}：${message}` : title;
  return (
    <View
      accessible
      accessibilityLabel={announcement}
      accessibilityLiveRegion="polite"
      accessibilityRole="alert"
      style={styles.container}
      testID={`${kind}-state`}
    >
      {kind === 'loading' ? (
        <ActivityIndicator accessibilityLabel={stateLabels[kind]} color={colors.accent} />
      ) : null}
      {kind !== 'loading' ? <Text style={styles.status}>{stateLabels[kind]}</Text> : null}
      <Text allowFontScaling style={styles.title}>
        {title}
      </Text>
      {message ? (
        <Text allowFontScaling style={styles.message}>
          {message}
        </Text>
      ) : null}
      {onRetry ? (
        <Pressable
          accessibilityLabel={`${commonMessages.retry}：${title}`}
          accessibilityRole="button"
          onPress={onRetry}
          style={styles.retry}
        >
          <Text allowFontScaling style={styles.retryText}>
            {commonMessages.retry}
          </Text>
        </Pressable>
      ) : null}
    </View>
  );
}

export const LoadingState = (props: Omit<ScreenStateProps, 'kind'>) => (
  <ScreenState {...props} kind="loading" />
);
export const EmptyState = (props: Omit<ScreenStateProps, 'kind'>) => (
  <ScreenState {...props} kind="empty" />
);
export const DeniedState = (props: Omit<ScreenStateProps, 'kind'>) => (
  <ScreenState {...props} kind="denied" />
);
export const FailureState = (props: Omit<ScreenStateProps, 'kind'>) => (
  <ScreenState {...props} kind="failure" />
);

const styles = StyleSheet.create({
  container: {
    alignItems: 'center',
    justifyContent: 'center',
    padding: spacing.xxl,
    gap: spacing.sm,
  },
  status: { color: colors.accentPressed, fontSize: 13, fontWeight: '800', letterSpacing: 0.8 },
  title: { color: colors.text, fontSize: 18, fontWeight: '700' },
  message: { color: colors.mutedText, fontSize: 14, textAlign: 'center' },
  retry: {
    backgroundColor: colors.accent,
    borderRadius: radii.sm,
    marginTop: spacing.sm,
    minHeight: 44,
    paddingHorizontal: 18,
    justifyContent: 'center',
  },
  retryText: { color: colors.onAccent, fontWeight: '700' },
});
