import { defineConfig, globalIgnores } from "eslint/config";
import globals from "globals";
import js from "@eslint/js";
import tseslint from "typescript-eslint";
import pluginReact from "eslint-plugin-react";
import pluginReactHooks from "eslint-plugin-react-hooks";
import pluginReactRefresh from "eslint-plugin-react-refresh";
import jest from "eslint-plugin-jest";
import formatjs from 'eslint-plugin-formatjs'

/** @type {import('eslint').Linter.Config[]} */
export default defineConfig([
  globalIgnores(["dist/", "dist-lib/*", "lib/*", "coverage/*"]),
  { files: ["**/*.{js,mjs,cjs,ts,jsx,tsx}"] },
  { files: ["**/*.{js,mjs,cjs,ts,jsx,tsx}"], languageOptions: { globals: globals.browser } },
  { files: ["**/*.{js,mjs,cjs,ts,jsx,tsx}"], plugins: { js }, extends: ["js/recommended"] },
  { files: ["**/*.test.{js,mjs,cjs,ts,jsx,tsx}"], plugins: { jest }, languageOptions: { globals: jest.environments.globals.globals } },
  tseslint.configs.recommended,
  pluginReact.configs.flat.recommended,
  pluginReact.configs.flat['jsx-runtime'],
  pluginReactRefresh.configs.vite,
  {
    plugins: {
      formatjs,
      'react-hooks': pluginReactHooks,
    }
  },
  {
    rules: {
      "react/prop-types": "off", // React 19+ uses TypeScript for prop types and this rule is not needed
      "react/react-in-jsx-scope": "off", // React 19+ uses JSX Transform and this rule is not needed
    },
  },
  {
    settings: {
      react: {
        version: 'detect'
      }
    }
  }
]);
