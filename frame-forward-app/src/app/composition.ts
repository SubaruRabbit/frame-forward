import { createNetworkClient, type NetworkClient } from '@services/api';
import { sessionCredentials } from '@services/storage';
import type { SessionCredentials } from '@contracts/session';
import type { AppEnvironment } from '@config/environment';

type CompositionOverrides = {
  fetcher?: typeof fetch;
  sessionCredentials?: {
    clear(): Promise<void>;
    load(): Promise<SessionCredentials | null>;
    save(value: SessionCredentials): Promise<void>;
  };
};

export type AppDependencies = {
  network: NetworkClient;
  sessionCredentials: NonNullable<CompositionOverrides['sessionCredentials']>;
};

export function createAppDependencies(
  environment: AppEnvironment,
  overrides: CompositionOverrides = {},
): AppDependencies {
  const credentials = overrides.sessionCredentials ?? sessionCredentials;
  return {
    network: createNetworkClient({
      baseUrl: environment.apiBaseUrl,
      fetcher: overrides.fetcher,
      getAccessToken: async () => (await credentials.load())?.accessToken ?? null,
    }),
    sessionCredentials: credentials,
  };
}
