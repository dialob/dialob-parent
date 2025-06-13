import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';
import peerDepsExternal from 'rollup-plugin-peer-deps-external';

export default defineConfig(({ command }) => {
  if (command === 'serve') {
    return {
      plugins: [react()],
      root: path.resolve(__dirname, 'dev'),
      build: {
        outDir: path.resolve(__dirname, 'dist'),
      }
    };
  } else {
    return {
      plugins: [react()],
      build: {
        lib: {
          entry: path.resolve(__dirname, 'src/index.tsx'),
          name: 'Index',
          formats: ['es', 'umd'],
          fileName: (format) => `index.${format}.js`
        },
        rollupOptions: {
          plugins: [peerDepsExternal()],
          output: {
            globals: {
              react: 'React',
              'react-dom': 'ReactDOM',
              '@mui/material': 'MaterialUI',
              '@mui/system': 'MaterialUISystem',
              '@mui/icons-material': 'MaterialUIIcons',
              '@mui/x-date-pickers': 'MUIXDatePickers'
            }
          }
        }
      }
    };
  }
});
