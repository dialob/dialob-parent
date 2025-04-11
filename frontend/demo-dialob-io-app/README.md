# Demo Dialob App

## Linting

```bash
pnpm lint
```

Runs ESLint on all files in the project. This should be run before pushing changes to the repository.
If you want to ignore some ESLint rules in justified cases, you can use the `// eslint-disable-next-line` comment to disable the rule for the next line.

## Building

```bash
pnpm build
```

Builds static package under dist folder.

## Building and deploying to demo.dialob.io

```bash
pnpm build:aws
aws sso login
./aws-deploy.sh
```

## Running

### For development

```bash
pnpm dev
```

Starts development server that hot-reloads changes. Follow on-screen information for additional functions (`o`+`enter` - opens browser etc.)
