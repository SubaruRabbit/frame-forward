import React from 'react';
import { Pressable, StyleSheet, Text, View } from 'react-native';

import { commonMessages } from '@i18n';
import { colors, spacing } from '@theme/tokens';

type StateKind = 'loading' | 'empty' | 'denied' | 'failure';
type ScreenStateProps = { kind: StateKind; title: string; message?: string; onRetry?: () => void };
const icons: Record<StateKind, string> = { loading: '◌', empty: '□', denied: '⊘', failure: '!' };

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
      <Text accessible={false} style={styles.icon}>
        {icons[kind]}
      </Text>
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
  icon: { color: colors.accent, fontSize: 32, fontWeight: '700' },
  title: { color: colors.text, fontSize: 18, fontWeight: '700' },
  message: { color: colors.mutedText, fontSize: 14, textAlign: 'center' },
  retry: {
    backgroundColor: colors.text,
    borderRadius: 8,
    marginTop: spacing.sm,
    paddingHorizontal: 18,
    paddingVertical: 10,
  },
  retryText: { color: colors.onAccent, fontWeight: '700' },
});
