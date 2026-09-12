export interface SessionValidator {
  hasValidSession(): Promise<boolean>;
}

// Authentication will supply the server-validated implementation in its own change.
export const unauthenticatedSession: SessionValidator = {
  async hasValidSession() {
    return false;
  },
};
export function createPersistedSessionValidator(
  loadCredentials: () => Promise<unknown>,
): SessionValidator {
  return {
    async hasValidSession() {
      return (await loadCredentials()) !== null;
    },
  };
}
