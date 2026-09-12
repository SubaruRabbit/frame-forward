module.exports = {
  presets: ['module:@react-native/babel-preset'],
  plugins: [
    [
      'module-resolver',
      {
        root: ['./'],
        alias: {
          '@app': './src/app',
          '@features': './src/features',
          '@domains': './src/domains',
          '@components': './src/components',
          '@hooks': './src/hooks',
          '@utils': './src/utils',
          '@theme': './src/theme',
          '@services': './src/services',
          '@contracts': './src/contracts',
          '@config': './src/config',
          '@i18n': './src/i18n',
        },
      },
    ],
  ],
};
