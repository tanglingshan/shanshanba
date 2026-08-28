import { defineConfig } from 'father';

export default defineConfig({
  // more father config: https://github.com/umijs/father/blob/master/docs/config.md
  esm: {
    output: 'dist',
  },
  // Skip declaration generation due to type definition conflicts
  extraBabelPlugins: [],
  prebundle: {},
});
