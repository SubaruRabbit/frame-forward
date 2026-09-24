import AsyncStorage from '@react-native-async-storage/async-storage';
import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { StyleSheet } from 'react-native';
import { SafeAreaProvider } from 'react-native-safe-area-context';

jest.mock('react-native-keychain', () => ({
  getGenericPassword: jest.fn(),
  setGenericPassword: jest.fn(),
  resetGenericPassword: jest.fn(),
}));

import { AppShell, type AppDependencies } from '@app';
import { persistentRouteStore, type RouteStore } from '@app/testing';
import { DeniedState, EmptyState, FailureState, LoadingState } from '@components/ScreenState';

const authenticatedSession = { hasValidSession: async () => true };

function createRouteStore(initialRoute = 'home'): RouteStore & { saved: string[] } {
  let route = initialRoute as 'home' | 'learn' | 'portfolio' | 'profile';
  const saved: string[] = [];
  return {
    saved,
    load: async () => route,
    save: async nextRoute => {
      route = nextRoute;
      saved.push(nextRoute);
    },
  };
}

const dependencies: AppDependencies = {
  network: { request: jest.fn(() => new Promise(() => {})) },
  sessionCredentials: { clear: jest.fn(), load: async () => null, save: async () => undefined },
};

function shell(store: RouteStore, session = authenticatedSession, appDependencies = dependencies) {
  return (
    <SafeAreaProvider>
      <AppShell dependencies={appDependencies} routeStore={store} sessionValidator={session} />
    </SafeAreaProvider>
  );
}

async function renderShell(
  store: RouteStore,
  session = authenticatedSession,
  appDependencies = dependencies,
) {
  let renderer!: ReactTestRenderer.ReactTestRenderer;
  await ReactTestRenderer.act(async () => {
    renderer = ReactTestRenderer.create(shell(store, session, appDependencies));
  });
  await ReactTestRenderer.act(async () => {
    await new Promise<void>(resolve => setImmediate(resolve));
  });
  return renderer;
}

test('navigation reaches every top-level placeholder', async () => {
  const store = createRouteStore();
  const renderer = await renderShell(store);
  for (const route of ['home', 'learn', 'portfolio', 'profile'] as const) {
    await ReactTestRenderer.act(async () => {
      renderer.root.findByProps({ testID: `nav-${route}` }).props.onPress();
    });
    expect(renderer.root.findByProps({ testID: `${route}-screen` })).toBeTruthy();
  }
  expect(store.saved).toEqual(['home', 'learn', 'portfolio', 'profile']);
});

test('bottom navigation exposes labelled selected targets with touch-safe geometry', async () => {
  const renderer = await renderShell(createRouteStore());
  const home = renderer.root.findByProps({ testID: 'nav-home' });
  expect(home.props.accessibilityLabel).toBe('切换到首页');
  expect(home.props.accessibilityState).toEqual({ selected: true });
  expect(StyleSheet.flatten(home.props.style({ pressed: false })).minHeight).toBeGreaterThanOrEqual(
    44,
  );

  await ReactTestRenderer.act(async () => {
    renderer.root.findByProps({ testID: 'nav-portfolio' }).props.onPress();
  });
  expect(renderer.root.findByProps({ testID: 'nav-portfolio' }).props.accessibilityState).toEqual({
    selected: true,
  });
});

test('home reproduces the Light Journal photography hierarchy and capture entry', async () => {
  const renderer = await renderShell(createRouteStore());
  expect(renderer.root.findByProps({ testID: 'light-journal-home' })).toBeTruthy();
  expect(renderer.root.findByProps({ children: '记录今天的光' })).toBeTruthy();
  expect(renderer.root.findByProps({ children: '今日摄影灵感' })).toBeTruthy();
  expect(renderer.root.findByProps({ children: '快速学习' })).toBeTruthy();
  expect(renderer.root.findByProps({ children: '最近评分' })).toBeTruthy();
  expect(renderer.root.findByProps({ testID: 'start-capture' }).props.accessibilityLabel).toBe(
    '开始拍摄，进入导入照片流程',
  );
});

test('learning route loads the catalog through injected app network dependencies', async () => {
  const network = {
    request: jest
      .fn()
      .mockResolvedValue([
        { id: 'p0', title: '摄影基础', category: 'BASICS', contentVersion: 'v1', lessonCount: 1 },
      ]),
  };
  const renderer = await renderShell(
    createRouteStore('learn'),
    { hasValidSession: async () => true },
    {
      ...dependencies,
      network,
    },
  );

  expect(network.request).toHaveBeenCalledWith({ path: '/courses' });
  expect(renderer.root.findByProps({ testID: 'course-p0' })).toBeTruthy();
  expect(renderer.root.findByProps({ testID: 'learn-screen' })).toBeTruthy();
});

test('an invalid session opens the login route', async () => {
  const renderer = await renderShell(createRouteStore(), { hasValidSession: async () => false });
  expect(renderer.root.findByProps({ testID: 'login-screen' })).toBeTruthy();
});

test('logout returns the App to login', async () => {
  const renderer = await renderShell(createRouteStore());
  await ReactTestRenderer.act(async () => {
    renderer.root.findByProps({ testID: 'nav-profile' }).props.onPress();
    await new Promise<void>(resolve => setImmediate(resolve));
  });
  expect(renderer.root.findByProps({ testID: 'profile-screen' })).toBeTruthy();
  await ReactTestRenderer.act(async () => {
    renderer.root.findByProps({ testID: 'logout-button' }).props.onPress();
  });
  expect(renderer.root.findByProps({ testID: 'login-screen' })).toBeTruthy();
});

test('restart restores a valid route and rejects an unknown saved route', async () => {
  const renderer = await renderShell(createRouteStore('portfolio'));
  expect(renderer.root.findByProps({ testID: 'portfolio-screen' })).toBeTruthy();
  (AsyncStorage.getItem as jest.Mock).mockResolvedValueOnce('unknown-route');
  expect(await persistentRouteStore.load()).toBe('home');
});

test('workflow completion is announced without leaving the current destination', async () => {
  const work = {
    availability: { evaluation: true, exif: true, retake: false, sourcePlan: false },
    camera: 'Sony α6700',
    favorite: false,
    height: 1080,
    lens: 'E 35mm',
    mediaId: 'work-1',
    subject: '人像',
    width: 1920,
    workflowContext: {
      comparisonCandidates: [],
      evaluation: { evaluationId: 'evaluation-1', sessionId: null },
      mediaId: 'work-1',
      session: null,
      sourcePlan: null,
    },
  };
  const request: AppDependencies['network']['request'] = async <T,>({
    path,
  }: Parameters<AppDependencies['network']['request']>[0]) => {
    if (path === '/portfolio/works') return { items: [work], nextCursor: null } as T;
    if (path === '/portfolio/works/work-1') return work as T;
    if (path === '/photo-evaluations') {
      return {
        result: {
          dimensions: {},
          primaryProblems: [],
          priorityImprovement: '保持构图重点',
          retakeSteps: [],
          strengths: [],
          technicalDiagnosis: { certainty: 'OBSERVATION', text: '曝光稳定' },
          total: 86,
        },
        state: 'SUCCEEDED',
        taskId: 'task-1',
      } as T;
    }
    throw new Error(`Unexpected request: ${path}`);
  };
  const network: AppDependencies['network'] = { request };
  const renderer = await renderShell(createRouteStore('portfolio'), authenticatedSession, {
    ...dependencies,
    network,
  });
  await ReactTestRenderer.act(async () => {
    renderer.root.findByProps({ testID: 'portfolio-work-work-1' }).props.onPress();
    await new Promise<void>(resolve => setImmediate(resolve));
  });
  await ReactTestRenderer.act(async () => {
    renderer.root.findByProps({ testID: 'portfolio-reanalyze' }).props.onPress();
    await new Promise<void>(resolve => setImmediate(resolve));
  });

  const notice = renderer.root.findByProps({ testID: 'workflow-notice' });
  expect(notice.props).toMatchObject({
    accessibilityLiveRegion: 'polite',
    accessibilityRole: 'alert',
  });
  expect(renderer.root.findByProps({ children: '照片重新分析完成。' })).toBeTruthy();
  expect(renderer.root.findByProps({ testID: 'portfolio-screen' })).toBeTruthy();
});

test('common state components only expose retry when supplied', async () => {
  let renderer!: ReactTestRenderer.ReactTestRenderer;
  await ReactTestRenderer.act(async () => {
    renderer = ReactTestRenderer.create(
      <>
        <LoadingState title="加载中" />
        <EmptyState title="暂无内容" />
        <DeniedState title="无权访问" />
        <FailureState title="连接失败" />
      </>,
    );
  });
  expect(() => renderer.root.findByProps({ accessibilityRole: 'button' })).toThrow();
  const retry = jest.fn();
  let failure!: ReactTestRenderer.ReactTestRenderer;
  await ReactTestRenderer.act(async () => {
    failure = ReactTestRenderer.create(<FailureState onRetry={retry} title="连接失败" />);
  });
  failure.root.findByProps({ accessibilityRole: 'button' }).props.onPress();
  expect(retry).toHaveBeenCalledTimes(1);
});
