import type { NetworkClient } from '../../../shared/network/network';
import type { AuthPort } from '../application/AuthPort';
export const createNetworkAuthPort = (network: NetworkClient): AuthPort => ({
  login: command =>
    network.request({
      path: '/auth/login',
      method: 'POST',
      body: JSON.stringify({ identifier: command.identifier, password: command.password }),
    }),
  register: command =>
    network.request({
      path: '/auth/register',
      method: 'POST',
      body: JSON.stringify({
        username: command.username,
        email: command.email,
        password: command.password,
      }),
    }),
});
