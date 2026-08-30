import React from 'react';
import {SafeAreaProvider} from 'react-native-safe-area-context';

import {AppShell} from './app/AppShell';

export default function App() {
  return (
    <SafeAreaProvider>
      <AppShell />
    </SafeAreaProvider>
  );
}
