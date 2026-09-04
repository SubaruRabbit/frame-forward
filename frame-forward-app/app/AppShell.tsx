import React, { useEffect, useState } from 'react';
import { ActivityIndicator, Pressable, StatusBar, StyleSheet, Text, View } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { defaultRoute, type TopLevelRoute } from './routes';
import { persistedSession, type SessionValidator } from './session';
import { persistentRouteStore, type RouteStore } from './storage';
import { createAppDependencies, type AppDependencies } from './composition';
import { developmentEnvironment } from './environment';
import { AuthScreen } from '../features/auth/AuthScreen';
import { createAuthUseCases } from '../features/auth/application/AuthUseCases';
import { createNetworkAuthPort } from '../features/auth/infrastructure/NetworkAuthPort';
import { PhotoImportScreen } from '../features/photo-import/PhotoImportScreen';
import { createPhotoImportUseCases } from '../features/photo-import/application/PhotoImportUseCases';
import { createDevicePhotoImportPort } from '../features/photo-import/infrastructure/DevicePhotoImportPort';
import { LearningScreen } from '../features/learning/LearningScreen';
import { EquipmentScreen } from '../features/equipment/EquipmentScreen';
import { createEquipmentUseCases } from '../features/equipment/application/EquipmentUseCases';
import { createNetworkEquipmentPort } from '../features/equipment/infrastructure/NetworkEquipmentPort';
import { createLearningUseCases } from '../features/learning/application/LearningUseCases';
import { cachedLearningPort } from '../features/learning/infrastructure/CachedLearningPort';
import { SceneAnalysisScreen } from '../features/scene-analysis/SceneAnalysisScreen';
import { createSceneAnalysisUseCases } from '../features/scene-analysis/application/SceneAnalysisUseCases';
import { createNetworkSceneAnalysisPort } from '../features/scene-analysis/infrastructure/NetworkSceneAnalysisPort';
import { ShootingPlanScreen } from '../features/shooting-plan/ShootingPlanScreen';
import { createShootingPlanUseCases } from '../features/shooting-plan/application/ShootingPlanUseCases';
import { createNetworkShootingPlanPort } from '../features/shooting-plan/infrastructure/NetworkShootingPlanPort';
import { PortfolioScreen } from '../features/portfolio/PortfolioScreen';
import { createPortfolioUseCases } from '../features/portfolio/application/PortfolioUseCases';
import { createNetworkPortfolioPort } from '../features/portfolio/infrastructure/NetworkPortfolioPort';
import { createWorkflowUseCases } from './workflowComposition';
import { AccountDeletionPanel } from '../features/settings/AccountDeletionPanel';

const destinations: Array<{ key: TopLevelRoute; label: string; kicker: string }> = [
  { key: 'home', label: '首页', kicker: '准备好拍下一张了吗？' },
  { key: 'learn', label: '学习', kicker: '把相机知识用到真实场景。' },
  { key: 'portfolio', label: '作品', kicker: '记录每一次更好的尝试。' },
  { key: 'profile', label: '我的', kicker: '你的器材、进度与设置。' },
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
      <View style={styles.center}>
        <ActivityIndicator color="#18A999" size="large" />
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
    <View style={[styles.app, { paddingTop: insets.top, paddingBottom: insets.bottom }]}>
      <StatusBar barStyle="dark-content" />
      <View style={styles.content} testID={`${route}-screen`}>
        <Text style={styles.eyebrow}>FRAME FORWARD</Text>
        <Text style={styles.heading}>{destination.label}</Text>
        <Text style={styles.kicker}>{destination.kicker}</Text>
        {route === 'home' ? (
          <>
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
          </>
        ) : route === 'learn' ? (
          <LearningScreen useCases={createLearningUseCases(cachedLearningPort)} />
        ) : route === 'profile' ? (
          <>
            <EquipmentScreen
              useCases={createEquipmentUseCases(createNetworkEquipmentPort(dependencies.network))}
            />
            <AccountDeletionPanel onConfirm={logout} />
          </>
        ) : (
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
        )}{' '}
        {workflowNotice ? <Text testID="workflow-notice">{workflowNotice}</Text> : null}
        {route === 'profile' && (
          <Pressable accessibilityRole="button" onPress={logout} testID="logout-button">
            <Text>退出登录</Text>
          </Pressable>
        )}
      </View>
      <View accessibilityRole="tablist" style={styles.tabs}>
        {destinations.map(item => {
          const selected = item.key === route;
          return (
            <Pressable
              accessibilityRole="tab"
              accessibilityState={{ selected }}
              key={item.key}
              onPress={() => selectRoute(item.key)}
              style={[styles.tab, selected && styles.selectedTab]}
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
const styles = StyleSheet.create({
  app: { backgroundColor: '#F5F9F8', flex: 1 },
  center: { alignItems: 'center', backgroundColor: '#F5F9F8', flex: 1, justifyContent: 'center' },
  content: { flex: 1, justifyContent: 'center', padding: 28 },
  eyebrow: { color: '#18A999', fontSize: 12, fontWeight: '800', letterSpacing: 2 },
  heading: { color: '#102A43', fontSize: 36, fontWeight: '800', marginTop: 12 },
  kicker: { color: '#52616B', fontSize: 17, lineHeight: 26, marginTop: 8 },
  focusFrame: {
    alignItems: 'center',
    borderColor: '#18A999',
    borderRadius: 18,
    borderWidth: 2,
    justifyContent: 'center',
    marginTop: 34,
    minHeight: 160,
  },
  focusText: { color: '#102A43', fontSize: 16, fontWeight: '600' },
  tabs: {
    backgroundColor: '#FFFFFF',
    borderTopColor: '#D9E5E2',
    borderTopWidth: StyleSheet.hairlineWidth,
    flexDirection: 'row',
    paddingHorizontal: 8,
    paddingTop: 8,
  },
  tab: { alignItems: 'center', flex: 1, minHeight: 48, justifyContent: 'center' },
  selectedTab: { borderTopColor: '#18A999', borderTopWidth: 3 },
  tabLabel: { color: '#718096', fontSize: 13, fontWeight: '600' },
  selectedTabLabel: { color: '#102A43', fontWeight: '800' },
});
