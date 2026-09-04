/* eslint-env jest */
jest.mock('@react-native-async-storage/async-storage', () => ({
  getItem: jest.fn(),
  setItem: jest.fn(),
}));

jest.mock('react-native-safe-area-context', () => ({
  SafeAreaProvider: ({ children }) => children,
  useSafeAreaInsets: () => ({ bottom: 0, left: 0, right: 0, top: 0 }),
}));

jest.mock(
  '@react-native-documents/picker',
  () => ({
    errorCodes: { OPERATION_CANCELED: 'OPERATION_CANCELED' },
    isErrorWithCode: jest.fn(() => false),
    pick: jest.fn(),
    types: { images: 'image/*' },
  }),
  { virtual: true },
);
