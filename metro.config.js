const {getDefaultConfig, mergeConfig} = require('@react-native/metro-config');
const {
  applyConfigForLinkedDependencies,
} = require('@carimus/metro-symlinked-deps');

/**
 * Metro configuration
 * https://facebook.github.io/metro/docs/configuration
 *
 * @type {import('metro-config').MetroConfig}
 */
const config = {};

module.exports = applyConfigForLinkedDependencies(
  mergeConfig(getDefaultConfig(__dirname), config),
);
