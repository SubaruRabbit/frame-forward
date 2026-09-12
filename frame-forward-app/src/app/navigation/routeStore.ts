import { defaultRoute, isTopLevelRoute, type TopLevelRoute } from '@contracts/navigation';
import { keyValueStorage } from '@services/storage';

const routeStorageKey = 'frame-forward.top-level-route';

export interface RouteStore {
  load(): Promise<TopLevelRoute>;
  save(route: TopLevelRoute): Promise<void>;
}

export const persistentRouteStore: RouteStore = {
  async load() {
    const savedRoute = await keyValueStorage.getItem(routeStorageKey);
    return isTopLevelRoute(savedRoute) ? savedRoute : defaultRoute;
  },
  async save(route) {
    await keyValueStorage.setItem(routeStorageKey, route);
  },
};
