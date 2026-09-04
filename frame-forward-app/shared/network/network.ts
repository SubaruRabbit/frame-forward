export type NetworkErrorKind =
  | 'NETWORK'
  | 'AUTHENTICATION'
  | 'AUTHORIZATION'
  | 'VALIDATION'
  | 'BUSINESS'
  | 'TIMEOUT'
  | 'CANCELLED'
  | 'UNKNOWN';

export class NetworkError extends Error {
  constructor(public readonly kind: NetworkErrorKind, public readonly status?: number) {
    super(kind);
  }
}

export type NetworkRequest = {
  path: string;
  method?: string;
  headers?: Record<string, string>;
  body?: string | FormData;
  signal?: AbortSignal;
};

export interface NetworkClient {
  request<T>(request: NetworkRequest): Promise<T>;
}

type NetworkClientOptions = {
  baseUrl: string;
  fetcher?: typeof fetch;
  getAccessToken?: () => Promise<string | null>;
  timeoutMs?: number;
};

const errorKindForStatus = (status: number): NetworkErrorKind => {
  if (status === 401) return 'AUTHENTICATION';
  if (status === 403) return 'AUTHORIZATION';
  if (status >= 400 && status < 500) return 'VALIDATION';
  if (status >= 500) return 'NETWORK';
  return 'BUSINESS';
};

export function createNetworkClient({
  baseUrl,
  fetcher = fetch,
  getAccessToken,
  timeoutMs = 15_000,
}: NetworkClientOptions): NetworkClient {
  return {
    async request<T>({
      path,
      method = 'GET',
      headers = {},
      body,
      signal,
    }: NetworkRequest): Promise<T> {
      const controller = new AbortController();
      let timedOut = false;
      const abortForCaller = () => controller.abort();
      signal?.addEventListener('abort', abortForCaller, { once: true });
      const timeout = setTimeout(() => {
        timedOut = true;
        controller.abort();
      }, timeoutMs);
      try {
        const accessToken = await getAccessToken?.();
        const response = await fetcher(`${baseUrl}${path}`, {
          body,
          headers: {
            Accept: 'application/json',
            ...(body instanceof FormData ? {} : { 'Content-Type': 'application/json' }),
            ...(accessToken ? { Authorization: `Bearer ${accessToken}` } : {}),
            ...headers,
          },
          method,
          signal: controller.signal,
        });
        if (response.status < 200 || response.status >= 300)
          throw new NetworkError(errorKindForStatus(response.status), response.status);
        if (response.status === 204) return undefined as T;
        return response.json() as Promise<T>;
      } catch (error) {
        if (error instanceof NetworkError) throw error;
        if (timedOut) throw new NetworkError('TIMEOUT');
        if (signal?.aborted) throw new NetworkError('CANCELLED');
        if (error instanceof Error && error.name === 'AbortError')
          throw new NetworkError('CANCELLED');
        throw new NetworkError('NETWORK');
      } finally {
        clearTimeout(timeout);
        signal?.removeEventListener('abort', abortForCaller);
      }
    },
  };
}
