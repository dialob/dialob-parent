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
pnpm run test
```

Dialob form modification functions re covered with unit tests in `src/dialob/test/reducer.test.ts`. All modifications done there must have test coverage. 

## Linting

```bash
pnpm run lint
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
pnpm build
```

Builds static package under dist folder.

## Building and deploying to demo.dialob.io

```bash
pnpm run build:aws
aws sso login
./aws-deploy.sh
```

This will build and deploy artifacts to AWS S3, making the template available with url: https://s3.eu-central-1.amazonaws.com/cdn.resys.io/dialob-composer-material/dev/index.html

## Running

### For development

```bash
pnpm run dev
```

Starts development server that hot-reloads changes. Follow on-screen information for additional functions (`o`+`enter` - opens browser etc.)

### Testing built package

```bash
pnpm run preview
```

Starts preview server for built application from `/dist` (No hot-reload!). Run `pnpm build` first to build the package

## AI Translation Feature

The composer includes an AI-powered translation feature that helps automate the translation of form content across multiple languages.

### Configuration

The AI translation feature needs `translationServiceUrl` to be set in `AppConfig` - it can come from the backend (`config.translationServiceUrl` configuration parameter) or be set manually in the app config (e.g. in `index.html`). This is the URL of the translation service API, including the path to the translation service endpoint.

```typescript
export const appConfig: AppConfig = {
  ...
  translationServiceUrl: 'http://localhost:8083/api/translate',
}
```

### Key Features

- **Automated Translation**: Translate labels, descriptions, validation messages, and value set entries from one language to another using AI
- **Translation Tracking**: All AI-generated translations are tracked with metadata including:
  - Source and target languages
  - Translation timestamp
  - Translation type (label, description, validation, etc.)
- **Visual Indicators**: AI-translated content is marked with a visual indicator in the translation editor
- **Manual Validation**: Users can review AI translations and remove the AI flag once validated
- **Bulk Operations**: Support for translating entire choice lists and multiple form elements at once

### Usage

The AI translation feature is available in multiple places in the composer, either for translating single items or in bulk:
- in the translation dialog (for translating entire form content)
- in the item options dialog (for translating single items)
- in the choice editor (for translating value set entries)

The source language is always the active form language. Translations can be validated by clicking the AI indicator.

### Item type configuration

Item type configuration defines the structure of the "Add Item" menu and available item types with their properties and behavior.

**See full example:** [`src/defaults/itemTypes.ts`](src/defaults/itemTypes.ts)

**TypeScript types:** [`src/defaults/types.ts`](src/defaults/types.ts)

#### Menu Structure

The configuration supports **flexible menu nesting** with three options:

1. **Flat structure** - Direct items in a category
2. **Nested structure** - Items organized in subcategories
3. **Mixed mode** - Both direct items AND subcategories in the same category

#### Basic Example

```typescript
export const DEFAULT_ITEMTYPE_CONFIG: ItemTypeConfig = {
  categories: [
    {
      title: 'Structure',
      type: 'structure',
      items: [
        {
          title: 'Group',
          convertible: ['rowgroup'],
          optionEditors: [
            { name: 'Additional option', editor: PropEditors.InputProp }
          ],
          propEditors: {
            columns: {
              component: PropEditors.InputProp,
              props: {
                type: 'number',
                min: 1,
                max: 20
              }
            }
          },
          config: {
            type: 'group',
            props: {
              columns: 1
            }
          }
        }
      ]
    }
  ]
}
```

#### Nested Structure with Subcategories

```typescript
{
  title: 'Inputs',
  type: 'input',
  subcategories: [
    {
      title: 'Text fields',
      items: [
        {
          title: 'Text',
          convertible: ['textBox', 'address'],
          config: {
            type: 'text',
            view: 'text'
          }
        },
        {
          title: 'Text box',
          convertible: ['text', 'address'],
          config: {
            type: 'text',
            view: 'textBox'
          }
        }
      ]
    },
    {
      title: 'Numbers',
      items: [
        {
          title: 'Decimal',
          config: { type: 'decimal' }
        },
        {
          title: 'Integer',
          config: { type: 'number' }
        }
      ]
    }
  ]
}
```

This creates a menu hierarchy:
```
Inputs →
  ├─ Text fields →
  │    ├─ Text
  │    └─ Text box
  └─ Numbers →
       ├─ Decimal
       └─ Integer
```

#### Mixed Mode (Direct Items + Subcategories)

You can combine both approaches in the same category:

```typescript
{
  title: 'Inputs',
  type: 'input',
  items: [
    // Direct items (no submenu)
    { title: 'Boolean', config: { type: 'boolean' } },
    { title: 'Date', config: { type: 'date' } }
  ],
  subcategories: [
    // Nested items (with submenu)
    {
      title: 'Text fields',
      items: [
        { title: 'Text', config: { type: 'text', view: 'text' } },
        { title: 'Text box', config: { type: 'text', view: 'textBox' } }
      ]
    }
  ]
}
```

Renders as:
```
Inputs →
  ├─ Boolean (clickable)
  ├─ Date (clickable)
  ├─── (separator) ───
  └─ Text fields →
       ├─ Text
       └─ Text box
```

#### Configuration Reference

**Category attributes:**
* **`title`** - Label displayed in the menu
* **`type`** - Category type: `'structure'`, `'input'`, or `'output'`. Used to filter which items can be added in different contexts
* **`items`** - (Optional) Array of item configurations for direct menu items
* **`subcategories`** - (Optional) Array of subcategory objects for nested menu structure

**Subcategory attributes:**
* **`title`** - Label displayed in the submenu
* **`items`** - Array of item configurations

**Item attributes:**
* **`title`** - Label displayed in the menu
* **`convertible`** - (Optional) Array of type identifiers this item can be converted to. Matched by `view` first, then `type`. If omitted, item cannot be converted
* **`optionEditors`** - (Optional) Additional option pages for the item dialog: `{name: 'Title', editor: Component}`
* **`propEditors`** - (Optional) Custom property editors (see below). If not defined, falls back to plain text input
* **`config`** - Dialob form item configuration snippet. Required field: `type`. The item's default ID is based on `view` (if present) or `type`

**Note:** `props` in the config are item-specific properties available at form filling time, not to be confused with React component props.

#### Prop Editor Configurations

Built in editors:

* `PropEditors.InputProp` - Plain input component, supports HTML `<input>` attributes as props for defining input type etc.

* `PropEditors.ChoiceProp` - Dropdown selection list, `options` prop having an array of `{key: '', label: ''},` entries, where `key` is value stored in prop and `label` is text displayed in UI.

* `PropEditors.BoolProp` - Boolean switch.

* `PropEditors.MultiChoiceProp` - Dropdown selection list, allows multiple selections (Array of string value). `options` prop as in `ChoiceProp`, `allowAdditions` true/false -- Allow adding arbitrary strings to list.

Custom editing component template
```typescript
const CustomProp = ({ onChange, value, name, item, ...props }) => {
  // onChange(value) - callback function for setting the prop's value
  // value - current value of the prop
  // name - prop name
  // item - item data for current item. (Immutable.Map)
  // props - additional editor component props passed on from configuration

  // Return react component here that renders UI for prop editor
  return (<Input onChange={(e) => onChange(e.target.value)} value={value || ''} {...props} />);
};
```


### Item editor configuration

For example: `src/defaults/itemConfig.js`

Item editor configuration defines which kind of item editing components are used in which conditions.

```typescript
export const DEFAULT_ITEM_CONFIG: ItemConfig = {
  defaultIcon: Circle,
  items: [
    {
      matcher: item => item.type === 'group',
      component: Group,
      props: {
        icon: CropSquare,
        placeholder: 'placeholders.group',
        treeCollapsible: true,
      }
    },
    ...
  ]
} 
```

Where:

- **mathcer** Function that receives item data and returns true if this configuration is applied to the item
- **component** React component used for rendering the item in the form editor
- **props** Additional properties passed to the component
  - **icon** MUI Icon component used for representing the item in tree view
  - **placeholder** Placeholder text identifier shown when item has no title
  - **treeCollapsible** (Group items only) true/false - Allow collapsing/expanding the group in tree view

### Valueset property configuration

This is for controlling additional metadata properties for value set entries that are stored with form data, but *not communicated to filling side*.

For example: `src/defaults/valueSetProps.js`

```javascript
export const DEFAULT_VALUESET_PROPS: ValueSetProp[] = [
  {
    title: 'Custom attribute',
    name: 'attr',
    editor: Box,
  }
];
```

* `title` Title shown in table column header
* `attr` attribute name
* `editor` React component for editing the value

Valueset entry metadata prop editor React component template
```javascript
const CustomValueSetProp = ({ onChange, value }) => {
  // onChange(value) - callback function for setting the prop's value
  // value - current value of the prop

  // Return react component here that renders UI for prop editor
};
```
---

### Customixing Markdown components

You can customize how markdown is rendered by passing custom components to the `Markdown` component from `react-markdown`. 
  
List of available components can be found in [`src/defaults/markdown.tsx`](https://github.com/dialob/dialob-parent/blob/dev/frontend/dialob-composer-material/src/defaults/markdown.tsx). See also [`src/components/editors/LocalizedStringEditor.tsx`](https://github.com/dialob/dialob-parent/blob/dev/frontend/dialob-composer-material/src/components/editors/LocalizedStringEditor.tsx) for an example of how to use custom components. To support table rendering, you also need to include the `remark-gfm` plugin.
  
The same components can be used on the filling side as well, so that the markdown looks the same when editing and when filling the form.