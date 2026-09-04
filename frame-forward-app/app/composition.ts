import { createNetworkClient, type NetworkClient } from '../shared/network/network';
import { sessionCredentials, type SessionCredentials } from '../shared/storage/sessionCredentials';
import type { AppEnvironment } from './environment';

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
