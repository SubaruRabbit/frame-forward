import {
  ShootingPlanScreen,
  createShootingPlanUseCases,
  createNetworkShootingPlanPort,
} from '@features/shooting-plan';
import { AccountDeletionPanel } from '@features/settings';
import {
  SceneAnalysisScreen,
  createSceneAnalysisUseCases,
  createNetworkSceneAnalysisPort,
} from '@features/scene-analysis';
import {
  PortfolioScreen,
  createPortfolioUseCases,
  createNetworkPortfolioPort,
} from '@features/portfolio';
import React, { useEffect, useRef, useState } from 'react';
import {
  ActivityIndicator,
  Pressable,
  ScrollView,
  StatusBar,
  StyleSheet,
  Text,
  View,
} from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { defaultRoute, type TopLevelRoute } from '@contracts/navigation';
import { persistedSession, type SessionValidator } from './session';
import { persistentRouteStore, type RouteStore } from './navigation/routeStore';
import { createAppDependencies, type AppDependencies } from './composition';
import { developmentEnvironment } from '@config/environment';
import {
  LearningScreen,
  createLearningUseCases,
  createNetworkLearningPort,
} from '@features/learning';
import {
  PhotoImportScreen,
  createDevicePhotoImportPort,
  createPhotoImportUseCases,
} from '@features/photo-import';
import { AuthScreen, createAuthUseCases, createNetworkAuthPort } from '@features/auth';
import {
  EquipmentScreen,
  createEquipmentUseCases,
  createNetworkEquipmentPort,
} from '@features/equipment';
import { createWorkflowUseCases } from './workflowComposition';
import { colors, elevation, radii, spacing } from '@theme/tokens';
import { LightJournalHome } from '@features/light-journal-home';

const destinations: Array<{ key: TopLevelRoute; label: string; kicker: string }> = [
  { key: 'home', label: '首页', kicker: '准备好拍下一张了吗？' },
  { key: 'learn', label: '教程', kicker: '把相机知识用到真实场景。' },
  { key: 'portfolio', label: '图库', kicker: '记录每一次更好的尝试。' },
  { key: 'profile', label: '评分', kicker: '你的器材、进度与设置。' },
];
export type AppShellProps = {
  dependencies?: AppDependencies;
  sessionValidator?: SessionValidator;
  routeStore?: RouteStore;
};
export function AppShell({
  dependencies = createAppDependencies(developmentEnvironment),
  sessionValidator = persistedSession,
  routeStore = persistentRouteStore,
}: AppShellProps) {
  const insets = useSafeAreaInsets();
  const [ready, setReady] = useState(false);
  const [authenticated, setAuthenticated] = useState(false);
  const [route, setRoute] = useState<TopLevelRoute>(defaultRoute);
  const [sceneAnalysisId, setSceneAnalysisId] = useState<string | null>(null);
  const [workflowNotice, setWorkflowNotice] = useState<string | null>(null);
  const scrollView = useRef<React.ComponentRef<typeof ScrollView>>(null);
  const [captureSectionY, setCaptureSectionY] = useState(0);
  const workflows = createWorkflowUseCases(dependencies.network);
  useEffect(() => {
    let active = true;
    Promise.all([sessionValidator.hasValidSession(), routeStore.load()]).then(([valid, saved]) => {
      if (active) {
        setAuthenticated(valid);
        setRoute(saved);
        setReady(true);
      }
    });
    return () => {
      active = false;
    };
  }, [routeStore, sessionValidator]);
  const selectRoute = (next: TopLevelRoute) => {
    setRoute(next);
    routeStore.save(next).catch(() => {});
  };
  const logout = async () => {
    await dependencies.sessionCredentials.clear();
    setAuthenticated(false);
  };
  if (!ready)
    return (
      <View
        accessibilityRole="progressbar"
        accessibilityLabel="正在准备摄影工作台"
        style={styles.center}
        testID="app-shell-loading"
      >
        <ActivityIndicator color={colors.accent} size="large" />
        <Text allowFontScaling style={styles.loadingText}>
          正在准备摄影工作台
        </Text>
      </View>
    );
  if (!authenticated)
    return (
      <AuthScreen
        useCases={createAuthUseCases(
          createNetworkAuthPort(dependencies.network),
          dependencies.sessionCredentials,
        )}
        onAuthenticated={() => setAuthenticated(true)}
      />
    );
  const destination = destinations.find(item => item.key === route)!;
  return (
    <View style={[styles.app, { paddingTop: insets.top }]}>
      <StatusBar barStyle="dark-content" />
      <ScrollView
        ref={scrollView}
        contentContainerStyle={styles.content}
        contentInsetAdjustmentBehavior="never"
        testID={`${route}-screen`}
      >
        {route === 'home' ? (
          <>
            <LightJournalHome
              onStartCapture={() =>
                scrollView.current?.scrollTo({ animated: true, y: captureSectionY })
              }
            />
            <View
              onLayout={event => setCaptureSectionY(event.nativeEvent.layout.y)}
              style={styles.captureWorkflow}
            >
              <PhotoImportScreen
                useCases={createPhotoImportUseCases(
                  createDevicePhotoImportPort(dependencies.network),
                )}
              />
              <SceneAnalysisScreen
                useCases={createSceneAnalysisUseCases(
                  createNetworkSceneAnalysisPort(dependencies.network),
                )}
                onCompleted={result => setSceneAnalysisId(result.sceneAnalysisId)}
              />
              {sceneAnalysisId && (
                <ShootingPlanScreen
                  sceneAnalysisId={sceneAnalysisId}
                  useCases={createShootingPlanUseCases(
                    createNetworkShootingPlanPort(dependencies.network),
                  )}
                />
              )}
            </View>
          </>
        ) : route === 'learn' ? (
          <>
            <RouteHeader destination={destination} />
            <LearningScreen
              useCases={createLearningUseCases(createNetworkLearningPort(dependencies.network))}
            />
          </>
        ) : route === 'profile' ? (
          <>
            <RouteHeader destination={destination} />
            <EquipmentScreen
              useCases={createEquipmentUseCases(createNetworkEquipmentPort(dependencies.network))}
            />
            <AccountDeletionPanel onConfirm={logout} />
          </>
        ) : (
          <>
            <RouteHeader destination={destination} />
            <PortfolioScreen
              useCases={createPortfolioUseCases(createNetworkPortfolioPort(dependencies.network))}
              onReanalyze={({ mediaId, sessionId }) =>
                workflows.photoReview
                  .reanalyze(mediaId, sessionId)
                  .then(() => setWorkflowNotice('照片重新分析完成。'))
                  .catch(() => setWorkflowNotice('照片重新分析失败，请重试。'))
              }
              onCreateSession={({ shootingPlanId, planContext }) =>
                workflows.shootingSession
                  .createSession(shootingPlanId, planContext)
                  .then(() => setWorkflowNotice('拍摄任务已创建。'))
                  .catch(() => setWorkflowNotice('拍摄任务创建失败，请重试。'))
              }
              onCompare={({ retakeEvaluationId }) =>
                workflows.shootingSession
                  .getComparison(retakeEvaluationId)
                  .then(() => setWorkflowNotice('重拍对比已加载。'))
                  .catch(() => setWorkflowNotice('重拍对比读取失败，请重试。'))
              }
            />
          </>
        )}
        {workflowNotice ? (
          <View
            accessibilityLiveRegion="polite"
            accessibilityRole="alert"
            style={styles.notice}
            testID="workflow-notice"
          >
            <Text allowFontScaling style={styles.noticeLabel}>
              拍摄进度
            </Text>
            <Text allowFontScaling style={styles.noticeText}>
              {workflowNotice}
            </Text>
          </View>
        ) : null}
        {route === 'profile' && (
          <Pressable
            accessibilityLabel="退出登录"
            accessibilityRole="button"
            onPress={logout}
            style={styles.logout}
            testID="logout-button"
          >
            <Text allowFontScaling style={styles.logoutText}>
              退出登录
            </Text>
          </Pressable>
        )}
      </ScrollView>
      <View
        accessibilityRole="tablist"
        style={[styles.tabs, { paddingBottom: Math.max(insets.bottom, spacing.sm) }]}
      >
        {destinations.map(item => {
          const selected = item.key === route;
          return (
            <Pressable
              accessibilityRole="tab"
              accessibilityLabel={`切换到${item.label}`}
              accessibilityState={{ selected }}
              key={item.key}
              onPress={() => selectRoute(item.key)}
              style={({ pressed }) => [
                styles.tab,
                selected && styles.selectedTab,
                pressed && styles.pressedTab,
              ]}
              testID={`nav-${item.key}`}
            >
              <Text style={[styles.tabLabel, selected && styles.selectedTabLabel]}>
                {item.label}
              </Text>
            </Pressable>
          );
        })}
      </View>
    </View>
  );
}

function RouteHeader({ destination }: { destination: (typeof destinations)[number] }) {
  return (
    <View style={styles.header}>
      <Text allowFontScaling style={styles.eyebrow}>
        LIGHT JOURNAL
      </Text>
      <Text allowFontScaling style={styles.heading}>
        {destination.label}
      </Text>
      <Text allowFontScaling style={styles.kicker}>
        {destination.kicker}
      </Text>
    </View>
  );
}
const styles = StyleSheet.create({
  app: { backgroundColor: colors.canvas, flex: 1 },
  center: {
    alignItems: 'center',
    backgroundColor: colors.canvas,
    flex: 1,
    gap: spacing.md,
    justifyContent: 'center',
    padding: spacing.xl,
  },
  loadingText: { color: colors.mutedText, fontSize: 15, fontWeight: '600' },
  content: { padding: spacing.xl, paddingBottom: spacing.xxxl },
  captureWorkflow: { marginTop: spacing.xl },
  header: { marginBottom: spacing.md },
  eyebrow: { color: colors.accentPressed, fontSize: 12, fontWeight: '800', letterSpacing: 1.8 },
  heading: { color: colors.text, fontSize: 34, fontWeight: '800', marginTop: spacing.md },
  kicker: { color: colors.mutedText, fontSize: 16, lineHeight: 24, marginTop: spacing.sm },
  notice: {
    backgroundColor: colors.accentSoft,
    borderRadius: radii.md,
    gap: spacing.xs,
    marginTop: spacing.xl,
    padding: spacing.lg,
  },
  noticeLabel: { color: colors.accentPressed, fontSize: 12, fontWeight: '800', letterSpacing: 0.7 },
  noticeText: { color: colors.text, fontSize: 15, lineHeight: 22 },
  logout: {
    alignSelf: 'flex-start',
    minHeight: 44,
    justifyContent: 'center',
    marginTop: spacing.xl,
    paddingHorizontal: spacing.md,
  },
  logoutText: { color: colors.danger, fontWeight: '800' },
  tabs: {
    backgroundColor: colors.surface,
    borderTopColor: colors.divider,
    borderTopWidth: StyleSheet.hairlineWidth,
    elevation: elevation.navigation,
    flexDirection: 'row',
    paddingHorizontal: 8,
    paddingTop: 8,
  },
  tab: {
    alignItems: 'center',
    borderRadius: radii.sm,
    flex: 1,
    minHeight: 48,
    justifyContent: 'center',
  },
  selectedTab: { backgroundColor: colors.accentSoft },
  pressedTab: { backgroundColor: colors.surfaceSubtle },
  tabLabel: { color: colors.muted, fontSize: 13, fontWeight: '600' },
  selectedTabLabel: { color: colors.accentPressed, fontWeight: '800' },
});
