import AsyncStorage from '@react-native-async-storage/async-storage';
import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import {SafeAreaProvider} from 'react-native-safe-area-context';

jest.mock('react-native-keychain', () => ({getGenericPassword: jest.fn(), setGenericPassword: jest.fn(), resetGenericPassword: jest.fn()}));

import {AppShell} from '../app/AppShell';
import {persistentRouteStore, type RouteStore} from '../app/storage';
import {DeniedState, EmptyState, FailureState, LoadingState} from '../shared/components/ScreenState';

const authenticatedSession = {hasValidSession: async () => true};

function createRouteStore(initialRoute = 'home'): RouteStore & {saved: string[]} {
  let route = initialRoute as 'home' | 'learn' | 'portfolio' | 'profile';
  const saved: string[] = [];
  return {saved, load: async () => route, save: async nextRoute => { route = nextRoute; saved.push(nextRoute); }};
}

function shell(store: RouteStore, session = authenticatedSession) {
  return <SafeAreaProvider><AppShell routeStore={store} sessionValidator={session} /></SafeAreaProvider>;
}

async function renderShell(store: RouteStore, session = authenticatedSession) {
  let renderer!: ReactTestRenderer.ReactTestRenderer;
  await ReactTestRenderer.act(async () => { renderer = ReactTestRenderer.create(shell(store, session)); });
  await ReactTestRenderer.act(async () => { await new Promise<void>(resolve => setImmediate(resolve)); });
  return renderer;
}

test('navigation reaches every top-level placeholder', async () => {
  const store = createRouteStore(); const renderer = await renderShell(store);
  for (const route of ['home', 'learn', 'portfolio', 'profile'] as const) {
    await ReactTestRenderer.act(async () => { renderer.root.findByProps({testID: `nav-${route}`}).props.onPress(); });
    expect(renderer.root.findByProps({testID: `${route}-screen`})).toBeTruthy();
  }
  expect(store.saved).toEqual(['home', 'learn', 'portfolio', 'profile']);
});

test('an invalid session opens the login route', async () => {
  const renderer = await renderShell(createRouteStore(), {hasValidSession: async () => false});
  expect(renderer.root.findByProps({testID: 'login-screen'})).toBeTruthy();
});

test('logout returns the App to login', async () => {
  const renderer = await renderShell(createRouteStore());
  await ReactTestRenderer.act(async () => { renderer.root.findByProps({testID: 'nav-profile'}).props.onPress(); });
  await ReactTestRenderer.act(async () => { renderer.root.findByProps({testID: 'logout-button'}).props.onPress(); });
  expect(renderer.root.findByProps({testID: 'login-screen'})).toBeTruthy();
});

test('restart restores a valid route and rejects an unknown saved route', async () => {
  const renderer = await renderShell(createRouteStore('portfolio'));
  expect(renderer.root.findByProps({testID: 'portfolio-screen'})).toBeTruthy();
  (AsyncStorage.getItem as jest.Mock).mockResolvedValueOnce('unknown-route');
  expect(await persistentRouteStore.load()).toBe('home');
});

test('common state components only expose retry when supplied', async () => {
  let renderer!: ReactTestRenderer.ReactTestRenderer;
  await ReactTestRenderer.act(async () => { renderer = ReactTestRenderer.create(<><LoadingState title="加载中" /><EmptyState title="暂无内容" /><DeniedState title="无权访问" /><FailureState title="连接失败" /></>); });
  expect(() => renderer.root.findByProps({accessibilityRole: 'button'})).toThrow();
  const retry = jest.fn(); let failure!: ReactTestRenderer.ReactTestRenderer;
  await ReactTestRenderer.act(async () => { failure = ReactTestRenderer.create(<FailureState onRetry={retry} title="连接失败" />); });
  failure.root.findByProps({accessibilityRole: 'button'}).props.onPress();
  expect(retry).toHaveBeenCalledTimes(1);
});
