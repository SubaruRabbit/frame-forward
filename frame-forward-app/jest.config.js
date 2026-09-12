module.exports = {
  preset: '@react-native/jest-preset',
  haste: { defaultPlatform: 'android', platforms: ['android', 'ios', 'native'] },
  setupFiles: ['<rootDir>/jest.setup.js'],
};
