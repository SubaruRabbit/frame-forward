const secretFields = new Set(['accesstoken', 'refreshtoken', 'password', 'token', 'authorization']);

export function redactSensitiveData(values: Record<string, unknown>): Record<string, unknown> {
  return Object.fromEntries(
    Object.entries(values).map(([key, value]) => {
      if (secretFields.has(key.toLowerCase())) return [key, '[REDACTED]'];
      if (key.toLowerCase() === 'email' && typeof value === 'string') {
        const [local, domain] = value.split('@');
        return [key, local && domain ? `${local.charAt(0)}***@${domain}` : '[REDACTED]'];
      }
      return [key, value];
    }),
  );
}
