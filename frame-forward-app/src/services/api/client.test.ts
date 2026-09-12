import { createNetworkClient, NetworkError } from './client';

const response = (status: number, body: unknown = { ok: true }) =>
  ({
    json: async () => body,
    status,
  } as Response);

test('adds the injected access token to requests and returns decoded JSON', async () => {
  const fetcher = jest.fn().mockResolvedValue(response(200, { id: 'camera-1' }));
  const client = createNetworkClient({
    baseUrl: 'https://api.example.test',
    fetcher,
    getAccessToken: async () => 'access-token',
  });

  await expect(client.request<{ id: string }>({ path: '/equipment' })).resolves.toEqual({
    id: 'camera-1',
  });
  expect(fetcher).toHaveBeenCalledWith(
    'https://api.example.test/equipment',
    expect.objectContaining({
      headers: expect.objectContaining({ Authorization: 'Bearer access-token' }),
    }),
  );
});

test.each([
  [401, 'AUTHENTICATION'],
  [403, 'AUTHORIZATION'],
  [422, 'VALIDATION'],
])('maps HTTP %i to a typed %s error', async (status, kind) => {
  const client = createNetworkClient({
    baseUrl: 'https://api.example.test',
    fetcher: jest.fn().mockResolvedValue(response(status)),
  });

  await expect(client.request({ path: '/protected' })).rejects.toMatchObject<Partial<NetworkError>>(
    { kind: kind as NetworkError['kind'], status },
  );
});

test('maps an elapsed request deadline to TIMEOUT', async () => {
  jest.useFakeTimers();
  const client = createNetworkClient({
    baseUrl: 'https://api.example.test',
    timeoutMs: 10,
    fetcher: (_url, init) =>
      new Promise((_resolve, reject) =>
        init?.signal?.addEventListener('abort', () =>
          reject(Object.assign(new Error('aborted'), { name: 'AbortError' })),
        ),
      ),
  });
  const pending = client.request({ path: '/slow' });

  await Promise.resolve();
  jest.advanceTimersByTime(10);
  await expect(pending).rejects.toMatchObject<Partial<NetworkError>>({ kind: 'TIMEOUT' });
  jest.useRealTimers();
});

test('maps a caller aborted request to CANCELLED', async () => {
  const controller = new AbortController();
  const client = createNetworkClient({
    baseUrl: 'https://api.example.test',
    fetcher: (_url, init) =>
      new Promise((_resolve, reject) =>
        init?.signal?.addEventListener('abort', () =>
          reject(Object.assign(new Error('aborted'), { name: 'AbortError' })),
        ),
      ),
  });
  const pending = client.request({ path: '/cancel', signal: controller.signal });

  await Promise.resolve();
  controller.abort();
  await expect(pending).rejects.toMatchObject<Partial<NetworkError>>({ kind: 'CANCELLED' });
});
