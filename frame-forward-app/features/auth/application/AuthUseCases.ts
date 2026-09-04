import type { AuthCommand, AuthPort } from './AuthPort';
export type CredentialStore = {
  save(value: { accessToken: string; refreshToken: string }): Promise<void>;
};
export const createAuthUseCases = (port: AuthPort, credentials: CredentialStore) => ({
  async authenticate(mode: 'login' | 'register', command: AuthCommand) {
    const session = await (mode === 'login' ? port.login(command) : port.register(command));
    await credentials.save(session);
  },
});
export type AuthUseCases = ReturnType<typeof createAuthUseCases>;
