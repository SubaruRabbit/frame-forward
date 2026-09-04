import * as Keychain from 'react-native-keychain';

const service = 'com.frameforward.session';
export type SessionCredentials = { accessToken: string; refreshToken: string };
export const sessionCredentials = {
  async load(): Promise<SessionCredentials | null> {
    const value = await Keychain.getGenericPassword({ service });
    return value ? (JSON.parse(value.password) as SessionCredentials) : null;
  },
  async save(value: SessionCredentials): Promise<void> {
    await Keychain.setGenericPassword('session', JSON.stringify(value), { service });
  },
  async clear(): Promise<void> {
    await Keychain.resetGenericPassword({ service });
  },
};
