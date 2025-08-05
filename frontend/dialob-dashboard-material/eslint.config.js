import { defineConfig, globalIgnores } from "eslint/config";
import globals from "globals";
import js from "@eslint/js";
import tseslint from "typescript-eslint";
import pluginReact from "eslint-plugin-react";
import pluginReactHooks from "eslint-plugin-react-hooks";
import pluginReactRefresh from "eslint-plugin-react-refresh";
import formatjs from 'eslint-plugin-formatjs'


/** @type {import('eslint').Linter.Config[]} */
export default defineConfig([
  globalIgnores(["dist/", "lib/*", "coverage/*"]),
  { files: ["**/*.{js,mjs,cjs,ts,jsx,tsx}"] },
  { files: ["**/*.{js,mjs,cjs,ts,jsx,tsx}"], languageOptions: { globals: globals.browser } },
  { files: ["**/*.{js,mjs,cjs,ts,jsx,tsx}"], plugins: { js }, extends: ["js/recommended"] },
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
    settings: {
      react: {
        version: 'detect'
      }
    }
  },
  {
    files: ["**/*.ts", "**/*.tsx"],
    rules: {
      "react/prop-types": "off"
    }
  }
]);
