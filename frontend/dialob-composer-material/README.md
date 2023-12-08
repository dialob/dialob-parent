# Dilob Composer Material

## Where's what

* **index.html** Index page template
* **src/main.tsx** Entry point script
* **src/App.tsx** Application root
* **src/theme/siteTheme** MUI theme, copied from DigiExpress composer projects
* **src/dialob** Dialob form state management
  * **test/** Jest test cases and data
  * **reducer.ts** All form data modification functions need to be here
  * **react/** React components for form state management: `useComposer` hook and `<ComposerProvider>` context provider component.
  * **types.ts** TypeScript types defining a Dialob form document


* **vite.config.ts** Vite build system configuration (you can set default dev server port here)

## Testing

```bash
yarn test
```

Dialob form modification functions re covered with unit tests in `src/dialob/test/reducer.test.ts`. All modifications done there must have test coverage. 


## Building

```bash
yarn build
```

Builds static package under dist folder.

## Running

### For development

```bash
yarn dev
```

Starts development server that hot-reloads changes. Follow on-screen information for additional functions (`o`+`enter` - opens browser etc.)

### Testing built package

```bash
yarn preview
```

Starts preview server for built application from `/dist` (No hot-reload!). Run `yarn build` first to build the package

---

# React + TypeScript + Vite

**Vite build system documentation: https://vitejs.dev/guide/**


This template provides a minimal setup to get React working in Vite with HMR and some ESLint rules.

Currently, two official plugins are available:

- [@vitejs/plugin-react](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react/README.md) uses [Babel](https://babeljs.io/) for Fast Refresh
- [@vitejs/plugin-react-swc](https://github.com/vitejs/vite-plugin-react-swc) uses [SWC](https://swc.rs/) for Fast Refresh

## Expanding the ESLint configuration

If you are developing a production application, we recommend updating the configuration to enable type aware lint rules:

- Configure the top-level `parserOptions` property like this:

```js
export default {
  // other rules...
  parserOptions: {
    ecmaVersion: 'latest',
    sourceType: 'module',
    project: ['./tsconfig.json', './tsconfig.node.json'],
    tsconfigRootDir: __dirname,
  },
}
```

- Replace `plugin:@typescript-eslint/recommended` to `plugin:@typescript-eslint/recommended-type-checked` or `plugin:@typescript-eslint/strict-type-checked`
- Optionally add `plugin:@typescript-eslint/stylistic-type-checked`
- Install [eslint-plugin-react](https://github.com/jsx-eslint/eslint-plugin-react) and add `plugin:react/recommended` & `plugin:react/jsx-runtime` to the `extends` list
