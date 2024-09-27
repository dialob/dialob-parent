import { defineConfig } from 'vite';
import path from 'path';
import dts from 'vite-plugin-dts';

export default defineConfig({
  plugins: [dts({
    include: 'src/items/**',  // Only include the folder being built
  })],
  build: {
    outDir: 'src/items/dist', // Output to a dist folder within the folder being built
    lib: {
      entry: path.resolve(__dirname, 'src/items/index.ts'), // Set entry point to the index file
      fileName: (format) => `items.${format}.js`,
      formats: ['es'], // Format as ES module
    },
    rollupOptions: {
      // Exclude dependencies that shouldn't be bundled (since they will be used from the parent app)
      external: [
        'react',
        'react-dom',
        'react-intl',
        'react-scroll',
        '@mui/material',
        '@mui/icons-material',
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

