export interface SessionValidator {
  hasValidSession(): Promise<boolean>;
}

import { sessionCredentials } from '../shared/storage/sessionCredentials';

// Authentication will supply the server-validated implementation in its own change.
export const unauthenticatedSession: SessionValidator = {
  async hasValidSession() {
    return false;
  },
};
export const persistedSession: SessionValidator = {
  async hasValidSession() {
    return (await sessionCredentials.load()) !== null;
  },
};
