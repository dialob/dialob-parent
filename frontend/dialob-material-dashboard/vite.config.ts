import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';
import commonjs from '@rollup/plugin-commonjs';

export default defineConfig(({ command, mode }) => {
	if (command === 'serve') {
		return {
			plugins: [react(), commonjs()],
			root: path.resolve(__dirname, 'dev'),
			build: {
				outDir: path.resolve(__dirname, 'dist'),
			}
		};
	} else {
		return {
			plugins: [react(), commonjs()],
			build: {
				lib: {
					entry: path.resolve(__dirname, 'src/index.tsx'),
					name: 'Index',
					formats: ['es', 'umd'],
					fileName: (format) => `index.${format}.js`
				},
				rollupOptions: {
					external: ['react', 'react-dom'],
					output: {
						globals: {
							react: 'React',
							'react-dom': 'ReactDOM'
						}
					}
				}
			}
		};
	}
});
