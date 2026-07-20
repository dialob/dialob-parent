# Dialob Material UI Fill: Generic App

## Running the fill app locally (against the dev-env backend)

To preview forms locally by clicking **Preview** in the Composer, the fill app
and the backend `dialob-session` service need a few configuration changes.

### 1. App endpoint and session id (`.env`)

The app reads its backend endpoint and (optionally) a fixed session id from
`frontend/dialob-fill-demo-material/.env`:

```bash
VITE_DIALOB_ENDPOINT=http://localhost:8084/session/dialob
VITE_DIALOB_SESSION_ID=
```

- `VITE_DIALOB_ENDPOINT` points at the local `dialob-session` service.
- `VITE_DIALOB_SESSION_ID` **must be left empty**. When empty, the app reads the
  session id from the URL hash (e.g. `http://localhost:3001/#/<sessionId>`),
  which is how Composer's Preview button opens a form. If it is set to a fixed
  value (such as `00000000-0000-0000-0000-000000000000`), the app ignores the
  hash and tries to load that id instead.

### 2. Enable CORS on the session service (`dev-env/.env.session`)

The browser blocks the fill app's requests unless the `dialob-session` service
returns CORS headers for the app's origin. Activate the `cors` Spring profile
alongside `jdbc`:

```bash
SPRING_PROFILES_ACTIVE=jdbc,cors
```

The `cors` profile loads `dev-env/dialob-session-config/application-cors.yml`,
which whitelists the local dev origins (including `http://localhost:3001`).
After changing this, recreate the `dialob-session` container so it picks up the
new environment variable:

```bash
docker compose up -d --force-recreate dialob-session
```

### 3. Start the app

The app serves on its own port; start it on `3001` (the origin whitelisted in
the CORS config above):

```bash
pnpm start --port 3001 --strictPort
```

Then click **Preview** in the Composer (running on `5173`) to open the form in
the fill app.

---

## Building and deploying to demo.dialob.io

```bash
pnpm run build:aws
aws sso login
./aws-deploy.sh
```

This will build and deploy artifacts to AWS S3, making the template available with url: https://s3.eu-central-1.amazonaws.com/cdn.resys.io/dialob/dialob-fill-demo-material/main/index.html


---

## Contents: Material UI theming for Dialob

1. Overview of Dialob filling side customization using MUI
2. Contents of a theme file
3. How to create a theme project
4. What your project should contain
5. How to use this project as a starting point

---

## 1. Overview of Dialob filling side customization using Material UI

To create a custom filling theme, you need to write a new fill application. 

Theme and application are intertwined. Applying one theme outside of its original fill application will require certain degrees of code modification.

That is why, currently, a different filling application is required for each custom theme you want to use.

In general, the process of creating a custom Dialob theme has three steps:

1. Create a new filling application
2. Write Dialob components
3. Write MUI components for use on top of Dialob components

You can use existing Dialob components and use Material UI customisation and theming on top of them to save effort.

Theming is done primarily with Typescript. Dialob components can be highly customized simply by manipulation of theme.

### Prerequisites to theme development

* Dialob components
* Jenkins build inclusion
* Node development environment
* Storybook for mocking components locally

---
## 2. Contents of a theme file

* **palette:** The color scheme for your project containing primary, secondary, error, and text colors.

* **typography:** Project fonts, styles, colors.

* **props:** Specify global overrides for components.  Components whose props you specify can be passed into `overrides`.  

* **overrides:** Default MUI elements' styles can be overridden using `overrides:{ }` within the theme file. This allows for overriding individual component's props within that component. Overrides cannot be used, for example, to stipulate where a component will appear within the document structure.

Material UI theming documentation:  
https://material-ui.com/customization/theming/

Material UI Overrides documentation:  
https://material-ui.com/customization/components/

A simple way to preview your theme and components is by using Storybook.  
https://storybook.js.org/docs/react/get-started/introduction

---
Manipulation of theme is easily done using overrides and props within the theme file.

Component props are set in the theme's props, and styles are set in the theme's overrides. 

You can pass a palette prop into `createMuiTheme` to customize individual items.  See example:

```typescript
typography: {
    h1: {
      backgroundColor: palette.primary.main,
      color: palette.common.white
```

For global overrides, you can set props for components in theme.props. See example from this project:

```typescript
  props: {
    MuiTextField: {
      variant: 'filled'
    },
    MuiSelect: {
      variant: 'filled'
    }
  },
```

You can then set styles for these components in `overrides`:
```typescript
 overrides: {

 MuiTextField: {
      root: {
        marginBottom: 0
      }
    },
      MuiSelect: {
      root: {
        color: '#555',
        backgroundColor: '#fbfbfb',
        "&:hover": {
          backgroundColor: '#f1f1f1',
        }
      }
```

For more information on createMuiTheme, props, and overrides, see this helpful guide:  
 https://www.headway.io/blog/global-styling-with-material-ui-theme-overrides-and-props

---

## 3. How to create a theme project

Prerequisites:

1. pnpm package manager  

---

2. Create a javascript project. Example: my-super-dialob-theme
3. Add boilerplate (existing Dialob components if not writing fill application from scratch)
4. Set up dependencies
5. Create MUI theme using theme generator
6. Storybook to preview MUI theme components without using backend  
https://storybook.js.org/docs/react/get-started/introduction

**Why not use the backend?**

In addition to security considerations, there is no need to setup a local environment for previewing components. Mocking applications like Storybook do this job well enough. It is also much simpler to see each individual component in a flat layout, whereas in the context of a form, certain components may be hidden beneath multiple layers of other components and only appear under certain circumstances.

---

## 4. What your project should contain

1. package.json
2. Localizations
3. Theme
4. Dialob components

---

### 1. package.json

This defines the project's dependencies, including but not limited to:

* Date libraries
* Material UI libraries
* React libraries
* Resys Dialob libraries

---

### 2. Localizations

This defines the filling side languages to be supported.  

`src/intl`

This project has localizations for two elements: language selector and feedback button.The rest of the form translations/languages kept inside Dialob are modified using Composer UI.

---

### 3. Theme

Use a Material UI theme generator for initial color scheme and to preview colors and components.

* Material UI theming documentation:  
https://mui.com/material-ui/customization/theming/

* Material UI theme generator:  
https://zenoo.github.io/mui-theme-creator/

---

### 4. Dialob components

These are your custom versions of Dialob components.

This project has one custom Dialob component: AppHeader.

---

### 5. Development 

Use Storybook to preview components.  

Do `pnpm build` to create production build and ensure project compiles

---

## 5. How to use this project as a starting point 

Clone this repository and follow the steps below to create your own fill application using this project as a base.

## Quick start to creating a theme

The theme is just one file. It will consist of palette, overrides, props, and typography styles.

Use a Material UI theme generator to create a .json file for your project's theme. The theme generator linked here below will allow you to generate and manipulate overrides, props, and typography styles, in addition to generating your palette.

https://bareynol.github.io/mui-theme-creator/

  MUI palette documentation:  
  https://material-ui.com/customization/palette/

---

## Quick start to creating fill application

1. Run `pnpm run storybook` to preview components locally.

2. package.json: Change the "name" to your project name.

    There are several necessary Dialob dependences hosted in a Resys private repository.  You will also need to have your project added to the Jenkins build service. Both of these can be made available to you upon request.

3. Add your theme file somewhere under src/

4. Put your logo file into images/ and remove any unused items

5. Import your theme file, and change the theme here: 

6. Import any fonts you need so you can preview them in Storybook.

7. App.tsx is the application. Within app.tsx, children is the dialob filling part. You can freely build things around children if desired.  

---
