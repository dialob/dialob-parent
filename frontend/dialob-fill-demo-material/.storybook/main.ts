import type { StorybookConfig } from '@storybook/react-vite';

const config: StorybookConfig = {
  stories: ['../stories/**/*.stories.tsx'],
  addons: [
    '@storybook/addon-actions',
    '@storybook/addon-links',
    '@chromatic-com/storybook'
  ],
  framework: '@storybook/react-vite',

  docs: {
    autodocs: true
  },

  staticDirs: ['../public'],

  typescript: {
    reactDocgen: 'react-docgen-typescript'
  }
};

export default config;
