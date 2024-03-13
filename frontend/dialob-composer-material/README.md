# Dialob Composer Material

## Where's what

* **src/dialob** Dialob form state management
  * **test/** Jest test cases and data
  * **reducer.ts** All form data modification functions need to be here
  * **react/** React components for form state management: `useComposer` hook and `<ComposerProvider>` context provider component.
  * **types.ts** TypeScript types defining a Dialob form document
* **src/editor** Composer editor state management, works similarly to Dialob state management using the `useEditor` hook and `<EditorProvider>` context provider component.
* **src/components** React generic components, sometimes divided into subfolders for better organization
* **src/default** Dialob configurations
* **src/intl** Localization (currently only English)
* **src/items** Components related to rendering form items
* **src/theme/siteTheme** MUI theme, copied from DigiExpress composer projects
* **src/utils** Used to store files that contain helper functions
* **src/views** Views that compose the visible layout of the application
* **src/App.tsx** Application root
* **src/main.tsx** Entry point script

* **index.html** Index page template
* **vite.config.ts** Vite build system configuration (you can set default dev server port here)

---

## Testing

```bash
yarn test
```

Dialob form modification functions re covered with unit tests in `src/dialob/test/reducer.test.ts`. All modifications done there must have test coverage. 

## Linting

```bash
yarn lint
```

Runs ESLint on all files in the project. This should be run before pushing changes to the repository.
If you want to ignore some ESLint rules in justified cases, you can use the `// eslint-disable-next-line` comment to disable the rule for the next line.

## Formatting

If using VSCode, adjust your workspace settings to include the following:
  
```json
{
  "editor.tabSize": 2,
  "editor.detectIndentation": false,
  "editor.insertSpaces": true,
  "editor.rulers": [
    150
  ],
  "[typescript]": {
    "editor.formatOnSave": true,
    "editor.defaultFormatter": "vscode.typescript-language-features"
  },
  "[typescriptreact]": {
    "editor.formatOnSave": true,
    "editor.defaultFormatter": "vscode.typescript-language-features"
  },
}
```

This will enable automatic formatting on save for `.ts` and `.tsx` files. The ruler at 150 characters is optional, but it's a good idea to keep lines short.

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
