import { createAppDependencies } from './composition';

test('builds a network client from the supplied environment instead of a feature URL', async () => {
  const fetcher = jest
    .fn()
    .mockResolvedValue({ status: 200, json: async () => ({ items: [] }) } as Response);
  const dependencies = createAppDependencies(
    { apiBaseUrl: 'https://test-api.example.test' },
    {
      fetcher,
      sessionCredentials: {
        clear: async () => undefined,
        load: async () => ({ accessToken: 'token', refreshToken: 'refresh' }),
        save: async () => undefined,
      },
    },
  );

  await expect(dependencies.network.request({ path: '/equipment' })).resolves.toEqual({
    items: [],
  });
  expect(fetcher).toHaveBeenCalledWith(
    'https://test-api.example.test/equipment',
    expect.any(Object),
  );
});
