import { redactSensitiveData } from './redactingLogger';

test('removes credentials and masks direct identifiers before diagnostic logging', () => {
  expect(
    redactSensitiveData({
      accessToken: 'access-secret',
      refreshToken: 'refresh-secret',
      password: 'password-secret',
      email: 'photographer@example.test',
      requestId: 'safe-id',
    }),
  ).toEqual({
    accessToken: '[REDACTED]',
    refreshToken: '[REDACTED]',
    password: '[REDACTED]',
    email: 'p***@example.test',
    requestId: 'safe-id',
  });
});
