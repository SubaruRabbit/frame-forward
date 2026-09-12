jest.mock('react-native-keychain', () => ({
  getGenericPassword: jest.fn(),
  resetGenericPassword: jest.fn(),
  setGenericPassword: jest.fn(),
}));

import * as Keychain from 'react-native-keychain';
import { sessionCredentials } from './sessionCredentials';

test('stores session credentials only through the Keychain service', async () => {
  await sessionCredentials.save({ accessToken: 'access-secret', refreshToken: 'refresh-secret' });

  expect(Keychain.setGenericPassword).toHaveBeenCalledWith(
    'session',
    JSON.stringify({ accessToken: 'access-secret', refreshToken: 'refresh-secret' }),
    { service: 'com.frameforward.session' },
  );
});

test('clears credentials from the same Keychain service', async () => {
  await sessionCredentials.clear();

  expect(Keychain.resetGenericPassword).toHaveBeenCalledWith({
    service: 'com.frameforward.session',
  });
});
