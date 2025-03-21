import { defineConfig } from 'vite';
import path from 'path';
import dts from 'vite-plugin-dts';

export default defineConfig({
  plugins: [dts({ rollupTypes: true })],
  build: {
    outDir: 'dist-lib',
    lib: {
      entry: path.resolve(__dirname, 'src/lib/index.ts'), 
      name: '@dialob/demo-dialob-io-app',
      fileName: 'index',
      formats: ['es'], 
    }
  },
});

