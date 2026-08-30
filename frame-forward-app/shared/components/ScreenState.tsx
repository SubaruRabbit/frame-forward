import React from 'react';
import {Pressable, StyleSheet, Text, View} from 'react-native';

type StateKind = 'loading' | 'empty' | 'denied' | 'failure';
type ScreenStateProps = {kind: StateKind; title: string; message?: string; onRetry?: () => void};
const icons: Record<StateKind, string> = {loading: '◌', empty: '□', denied: '⊘', failure: '!'};

export function ScreenState({kind, title, message, onRetry}: ScreenStateProps) {
  return <View accessibilityRole="alert" style={styles.container} testID={`${kind}-state`}>
    <Text style={styles.icon}>{icons[kind]}</Text><Text style={styles.title}>{title}</Text>
    {message ? <Text style={styles.message}>{message}</Text> : null}
    {onRetry ? <Pressable accessibilityRole="button" onPress={onRetry} style={styles.retry}><Text style={styles.retryText}>重试</Text></Pressable> : null}
  </View>;
}

export const LoadingState = (props: Omit<ScreenStateProps, 'kind'>) => <ScreenState {...props} kind="loading" />;
export const EmptyState = (props: Omit<ScreenStateProps, 'kind'>) => <ScreenState {...props} kind="empty" />;
export const DeniedState = (props: Omit<ScreenStateProps, 'kind'>) => <ScreenState {...props} kind="denied" />;
export const FailureState = (props: Omit<ScreenStateProps, 'kind'>) => <ScreenState {...props} kind="failure" />;

const styles = StyleSheet.create({container: {alignItems: 'center', justifyContent: 'center', padding: 32, gap: 8}, icon: {color: '#18A999', fontSize: 32, fontWeight: '700'}, title: {color: '#102A43', fontSize: 18, fontWeight: '700'}, message: {color: '#52616B', fontSize: 14, textAlign: 'center'}, retry: {backgroundColor: '#102A43', borderRadius: 8, marginTop: 8, paddingHorizontal: 18, paddingVertical: 10}, retryText: {color: '#FFFFFF', fontWeight: '700'}});
