# Dialob Docusaurus Documentation Site

This directory contains the [Docusaurus](https://docusaurus.io/) site that publishes the Dialob user documentation.

## Overview

The **source of truth for all documentation is the [GitHub wiki](https://github.com/dialob/dialob-parent/wiki)**. The files in `docs/` are generated automatically — do not edit them by hand. Edit the wiki instead; a GitHub Actions workflow will sync the changes here.

---

## Directory structure

```
dialob-docusaurus/
├── docs/                  # Generated docs (synced from the wiki — do not edit manually)
├── src/css/custom.css     # Theme overrides
├── static/img/            # Logo and favicon
├── docusaurus.config.ts   # Site configuration
├── sidebars.ts            # Sidebar order
└── package.json           # Node.js dependencies
```

---

## How the wiki sync works

Every time a wiki page is saved, the `Wiki - Sync to Docusaurus` GitHub Actions workflow ([`.github/workflows/wiki-sync.yaml`](../.github/workflows/wiki-sync.yaml)) runs automatically via the `gollum` event. It can also be triggered manually from the Actions tab.

The workflow:

1. Checks out this repository with a write token.
2. Clones the wiki (`dialob-parent.wiki`) into a temporary `wiki/` directory.
3. Runs [`scripts/sync-wiki.sh`](../scripts/sync-wiki.sh) from the repo root.
4. Commits and pushes any changes to `dialob-docusaurus/docs/` with message `docs: sync wiki to Docusaurus [skip ci]`.

### What the sync script does

The script ([`sync-wiki.sh`](../scripts/sync-wiki.sh)) processes each of the 9 wiki pages and applies three transformations before writing to `docs/`:

| Transformation | Why |
|---|---|
| **Inject YAML frontmatter** | Wiki files have no frontmatter; Docusaurus requires `id`, `sidebar_position`, and `title` for each page. |
| **Rewrite internal links** | Wiki uses absolute GitHub wiki URLs (e.g. `https://github.com/dialob/dialob-parent/wiki/Dialob-composer:-01%E2%80%90Introduction`); Docusaurus uses relative slugs (e.g. `01-introduction`). |
| **Fix `<img>` self-closing** | MDX (used by Docusaurus) requires void elements to be self-closed (`<img ... />`); wiki pages use HTML5 `<img ...>`. |
| **Escape `<` operators** | MDX parses bare `<` as JSX. Comparison operators like `<=` in prose are escaped to `&lt;=` outside fenced code blocks. |

### Page mapping

| Wiki filename | Docusaurus doc |
|---|---|
| `Home.md` | `home.md` |
| `Dialob-composer:-01‐Introduction.md` | `01-introduction.md` |
| `Dialob-composer:-02‐Basic-operations.md` | `02-basic-operations.md` |
| `Dialob-composer:-03‐Advanced-operations.md` | `03-advanced-operations.md` |
| `Dialob-composer:-04‐Input-and-output-types.md` | `04-input-and-output-types.md` |
| `Dialob-composer:-05‐Dialob-Expression-Language-–-DEL.md` | `05-dialob-expression-language-del.md` |
| `Dialob-composer:-06‐Options-and-settings.md` | `06-options-and-settings.md` |
| `Dialob-composer:-07‐New-form-walkthrough.md` | `07-new-form-walkthrough.md` |
| `Dialob-composer:-08‐Customization.md` | `08-customization.md` |

---

## CI/CD workflows

| Workflow | Trigger | What it does |
|---|---|---|
| `Wiki - Sync to Docusaurus` | Wiki page saved (`gollum`) or manual dispatch | Syncs wiki → `docs/`, commits result |
| `Docusaurus - Build - Documentation` | Push/PR touching `dialob-docusaurus/**` | Runs `npm run build`, uploads artifact |

After the sync commit is pushed, the `Docusaurus - Build - Documentation` workflow triggers automatically because the commit touches `dialob-docusaurus/docs/`, which matches that workflow's path filter.

---

## Docusaurus configuration highlights

- **`routeBasePath: '/'`** — docs are served at the root URL (no `/docs` prefix).
- **`blog: false`** — blog feature is disabled; this is a pure docs site.
- **`@cmfcmf/docusaurus-search-local`** — offline full-text search, no external service needed.
- **`onBrokenMarkdownImages: 'warn'`** — wiki pages may reference images with wiki-relative paths that do not exist in the Docusaurus static directory; the build warns instead of failing.
- **`future.v4: true`** — opt-in to Docusaurus v4 compatibility improvements.

---

## Local development

```bash
cd dialob-docusaurus
npm install
npm start          # dev server at http://localhost:3000
```

### Running the sync script locally

```bash
# from repo root
git clone ../dialob-parent.wiki wiki   # or: git clone <wiki-url> wiki
bash scripts/sync-wiki.sh
git diff dialob-docusaurus/docs/
```

### Building for production

```bash
npm run build      # output in dialob-docusaurus/build/
npm run serve      # preview the production build locally
```

---

## Adding a new wiki page

1. Create the page in the GitHub wiki.
2. Add a `process_page` call in [`scripts/sync-wiki.sh`](../scripts/sync-wiki.sh) with the wiki filename, target doc filename, and YAML frontmatter string.
3. Add the new doc `id` to [`sidebars.ts`](sidebars.ts) in the desired position.
4. The next wiki save (or a manual workflow dispatch) will sync the new page.
