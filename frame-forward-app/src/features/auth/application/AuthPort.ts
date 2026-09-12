import type { SessionCredentials } from '@contracts/session';
export type AuthCommand = {
  identifier?: string;
  username?: string;
  email?: string;
  password: string;
};
export interface AuthPort {
  login(command: AuthCommand): Promise<SessionCredentials>;
  register(command: AuthCommand): Promise<SessionCredentials>;
}
