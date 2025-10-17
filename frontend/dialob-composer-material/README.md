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

### Item type configuration

For exmaple: `src/defaults/itemTypes.js`

Item type configuration corresponds to "Add new" item creation menu structure and also defines available item types including their configuration

```typescript
 DEFAULT_ITEMTYPE_CONFIG: ItemTypeConfig = {
   categories: [
    {
      title: 'Structure',
      type: 'structure',
      items: [
        {
          title: 'Group',
          optionEditors: [
            {name: 'Additional option', editor: PropEditors.InputProp}
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
        },
        {
          title: 'Multi-choice',
          convertible: ['list'],
          propEditors: {
            display: {
              component: PropEditors.ChoiceProp,
              props: {
                options: [
                  {key: 'dropdown', label: 'Dropdown'},
                  {key: 'button', label: 'Button'},
                  {key: 'checkbox', label: 'Checkbox'}
                ]
              }
            }
          },
          config: {
            type: 'multichoice',
            props: {
              display: 'dropdown',
            }
          }
        }
      ]
    },
        // ....
      ]
    },
    // ....
  ]
 }
```

See: `src/defaults/type.ts` for TypeScript types

* `categories` defines top-level categories, category object contains following attributes:
  * `Title` Label used in UI
  * `type` Category type, allowed values: `structure`, `input`, `output` - These are used to limit certain categories of items to be added into form depending on conditions
  * `items` Array of item configurations within this category. Item objects contain following attributes
    * `title` Label used in UI
    * `convertible` (Optional) Array of item type identifiers into which this item can be converted. Entries are first matched by `view` attribute, if not found then by `type`. If omitted, item can't be converted to other types.
    * `optionEditors` (Optional) Array of additional pages for item options dialog. Array of objects: `{name: 'Title of page', editor: OptionEditorComponent}` (see below)
    * `propEditors` (Optional) if custom property editors are configured for item. If prop editor is not defined, it will be fallen back to plain text. Editor configuration is set of objects having prop name as a key:
      * `component` : React component to use for editing the prop
      * `props` : (Optional) Additional properties for the editing component. (see below)
    * `config` : Snippet of Dialob form item configuration (See Dialob Form API). Any predefined structure is supported. only mandatory attribute is `type`. Item's default ID will be based on `view` attribute falling back to `type`

**Note!** `props` Are item specific properties that are available at filling time

#### Prop editor configrurations

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
