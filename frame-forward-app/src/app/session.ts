import { createPersistedSessionValidator } from '@domains/session';
import { sessionCredentials } from '@services/storage';

export const persistedSession = createPersistedSessionValidator(() => sessionCredentials.load());

export type { SessionValidator } from '@domains/session';
