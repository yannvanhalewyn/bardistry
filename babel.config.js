// just to ensure TAMAGUI_TARGET is set
process.env.TAMAGUI_TARGET = 'native';

module.exports = {
  presets: ['module:metro-react-native-babel-preset'],
  plugins: [
    'nativewind/babel',
    ['transform-inline-environment-variables', {include: ['TAMAGUI_TARGET']}],
  ],
};
