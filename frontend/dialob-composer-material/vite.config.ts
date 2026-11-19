import { defineConfig } from 'vite';
import path from 'path';
import dts from 'vite-plugin-dts';

export default defineConfig({
  plugins: [dts({ rollupTypes: true })], // Use dts plugin to generate type definitions
  build: {
    outDir: 'dist-lib',
    lib: {
      entry: path.resolve(__dirname, 'src/lib/index.ts'), // Set entry point to the lib index file
      name: '@dialob/composer-material',
      fileName: 'index',
      formats: ['es'], // Only generate ESM bundle
    },
    rollupOptions: {
      // Exclude dependencies that shouldn't be bundled (since they will be used from the parent app).
      // Regexes also match subpaths (e.g. react/jsx-runtime, @mui/material/styles). Otherwise those get
      // bundled as CJS, and Rolldown (Vite 8) emits a require("react") that throws in the browser.
      external: [
        /^react($|\/)/,
        /^react-dom($|\/)/,
        'react-intl',
        'react-scroll',
        /^@mui\/material($|\/)/,
        /^@mui\/icons-material($|\/)/,
      ],
      output: {
        globals: {
          react: 'React',
          'react-dom': 'ReactDOM',
          'react-intl': 'ReactIntl',
          'react-scroll': 'ReactScroll',
          '@mui/material': 'MaterialUI',
          '@mui/icons-material': 'MaterialUIIcons',
        },
      },
    },
  },
});

