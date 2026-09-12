import React from 'react';
import { SafeAreaProvider } from 'react-native-safe-area-context';

import { AppShell } from './AppShell';

export function AppRoot() {
  return (
    <SafeAreaProvider>
      <AppShell />
    </SafeAreaProvider>
  );
}
