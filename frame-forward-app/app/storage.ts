import AsyncStorage from '@react-native-async-storage/async-storage';
import { defaultRoute, isTopLevelRoute, type TopLevelRoute } from './routes';

const routeStorageKey = 'frame-forward.top-level-route';

export interface RouteStore {
  load(): Promise<TopLevelRoute>;
  save(route: TopLevelRoute): Promise<void>;
}

export const persistentRouteStore: RouteStore = {
  async load() {
    const savedRoute = await AsyncStorage.getItem(routeStorageKey);
    return isTopLevelRoute(savedRoute) ? savedRoute : defaultRoute;
  },
  async save(route) {
    await AsyncStorage.setItem(routeStorageKey, route);
  },
};
