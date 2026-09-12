import { createAuthUseCases } from './AuthUseCases';

test('认证成功后保存会话，失败时不写入凭据', async () => {
  const credentials = { save: jest.fn() };
  const port = {
    login: jest.fn().mockResolvedValue({ accessToken: 'access', refreshToken: 'refresh' }),
    register: jest.fn(),
  };
  await createAuthUseCases(port, credentials).authenticate('login', {
    identifier: 'user',
    password: 'password',
  });
  expect(credentials.save).toHaveBeenCalledWith({ accessToken: 'access', refreshToken: 'refresh' });
  await expect(
    createAuthUseCases(
      { ...port, login: jest.fn().mockRejectedValue(new Error('invalid')) },
      credentials,
    ).authenticate('login', { identifier: 'user', password: 'bad' }),
  ).rejects.toThrow('invalid');
});
