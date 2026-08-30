export const topLevelRoutes = ['home', 'learn', 'portfolio', 'profile'] as const;
export type TopLevelRoute = (typeof topLevelRoutes)[number];
export const defaultRoute: TopLevelRoute = 'home';

export function isTopLevelRoute(value: string | null): value is TopLevelRoute {
  return value !== null && (topLevelRoutes as readonly string[]).includes(value);
}
