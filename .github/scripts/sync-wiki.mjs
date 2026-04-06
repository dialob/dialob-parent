/**
 * Syncs GitHub wiki pages into the Docusaurus docs directory.
 *
 * Reads from ./wiki/ (the cloned wiki repo, placed at repo root by the CI workflow).
 * Writes to ./dialob-docusaurus/docs/.
 *
 * For each page it:
 *   1. Reads the wiki Markdown file
 *   2. Prepends the Docusaurus YAML frontmatter block
 *   3. Rewrites absolute GitHub wiki URLs to Docusaurus-relative slugs
 *   4. Writes the result to the corresponding docs file
 *
 * Run from the repo root:
 *   node .github/scripts/sync-wiki.mjs
 */

import { readFileSync, writeFileSync, existsSync } from 'fs';
import { join, dirname } from 'path';
import { fileURLToPath } from 'url';

const __dirname = dirname(fileURLToPath(import.meta.url));
const REPO_ROOT = join(__dirname, '..', '..');
const WIKI_DIR = join(REPO_ROOT, 'wiki');
const DOCS_DIR = join(REPO_ROOT, 'dialob-docusaurus', 'docs');

const BASE_WIKI_URL = 'https://github.com/dialob/dialob-parent/wiki/';

// \u2010 = NON-BREAKING HYPHEN (‐) used in wiki filenames between number and title
// \u2013 = EN DASH (–) used in the DEL page filename
const PAGES = [
  {
    wiki: 'Home.md',
    doc: 'home.md',
    frontmatter: '---\nid: home\nslug: /\nsidebar_position: 0\ntitle: Home\n---',
    wikiUrlPath: null,
  },
  {
    wiki: 'Dialob-composer:-01\u2010Introduction.md',
    doc: '01-introduction.md',
    frontmatter: '---\nid: 01-introduction\nsidebar_position: 1\ntitle: Introduction\n---',
    wikiUrlPath: 'Dialob-composer:-01%E2%80%90Introduction',
  },
  {
    wiki: 'Dialob-composer:-02\u2010Basic-operations.md',
    doc: '02-basic-operations.md',
    frontmatter: '---\nid: 02-basic-operations\nsidebar_position: 2\ntitle: Basic operations\n---',
    wikiUrlPath: 'Dialob-composer:-02%E2%80%90Basic-operations',
  },
  {
    wiki: 'Dialob-composer:-03\u2010Advanced-operations.md',
    doc: '03-advanced-operations.md',
    frontmatter: '---\nid: 03-advanced-operations\nsidebar_position: 3\ntitle: Advanced operations\n---',
    wikiUrlPath: 'Dialob-composer:-03%E2%80%90Advanced-operations',
  },
  {
    wiki: 'Dialob-composer:-04\u2010Input-and-output-types.md',
    doc: '04-input-and-output-types.md',
    frontmatter: '---\nid: 04-input-and-output-types\nsidebar_position: 4\ntitle: Input and output types\n---',
    wikiUrlPath: 'Dialob-composer:-04%E2%80%90Input-and-output-types',
  },
  {
    wiki: 'Dialob-composer:-05\u2010Dialob-Expression-Language-\u2013-DEL.md',
    doc: '05-dialob-expression-language-del.md',
    frontmatter: '---\nid: 05-dialob-expression-language-del\nsidebar_position: 5\ntitle: "Dialob Expression Language (DEL)"\n---',
    wikiUrlPath: 'Dialob-composer:-05%E2%80%90Dialob-Expression-Language-%E2%80%93-DEL',
  },
  {
    wiki: 'Dialob-composer:-06\u2010Options-and-settings.md',
    doc: '06-options-and-settings.md',
    frontmatter: '---\nid: 06-options-and-settings\nsidebar_position: 6\ntitle: Options and settings\n---',
    wikiUrlPath: 'Dialob-composer:-06%E2%80%90Options-and-settings',
  },
  {
    wiki: 'Dialob-composer:-07\u2010New-form-walkthrough.md',
    doc: '07-new-form-walkthrough.md',
    frontmatter: '---\nid: 07-new-form-walkthrough\nsidebar_position: 7\ntitle: New form walkthrough\n---',
    wikiUrlPath: 'Dialob-composer:-07%E2%80%90New-form-walkthrough',
  },
  {
    wiki: 'Dialob-composer:-08\u2010Customization.md',
    doc: '08-customization.md',
    frontmatter: '---\nid: 08-customization\nsidebar_position: 8\ntitle: Customization\n---',
    wikiUrlPath: 'Dialob-composer:-08%E2%80%90Customization',
  },
];

// Build a flat list of { pattern, slug } for link rewriting.
// Each wiki URL (with or without a #anchor suffix) is replaced with the Docusaurus slug.
const linkReplacements = PAGES
  .filter(p => p.wikiUrlPath !== null)
  .map(p => ({
    pattern: BASE_WIKI_URL + p.wikiUrlPath,
    slug: p.doc.replace(/\.md$/, ''),
  }));

function rewriteLinks(content) {
  let result = content;
  for (const { pattern, slug } of linkReplacements) {
    // replaceAll handles both bare links and links with #anchor appended
    result = result.replaceAll(pattern, slug);
  }
  return result;
}

/**
 * Converts non-self-closing <img ...> tags to JSX-compatible <img ... />.
 * MDX (used by Docusaurus) requires all void HTML elements to be self-closed.
 * Wiki content often uses HTML5 <img src="..."> without the trailing slash.
 */
function fixImgSelfClosing(content) {
  return content.replace(/<img\b([^>]*)>/g, (match, attrs) => {
    const trimmed = attrs.trimEnd();
    if (trimmed.endsWith('/')) return match;
    return `<img${trimmed} />`;
  });
}

/**
 * Escapes bare `<` characters that are NOT the start of an HTML/JSX tag.
 * MDX parses `<` as JSX, so comparison operators like `<=` must be escaped
 * as `&lt;=` to avoid parse errors.
 *
 * Only processes non-fenced-code-block lines so code examples are unchanged.
 */
function escapeMdxOperators(content) {
  const lines = content.split('\n');
  let inFencedBlock = false;
  const result = [];

  for (const line of lines) {
    if (/^\s*```/.test(line)) {
      inFencedBlock = !inFencedBlock;
      result.push(line);
      continue;
    }
    if (inFencedBlock) {
      result.push(line);
    } else {
      // Escape < that is NOT the start of a valid HTML/JSX tag (letter, /, !)
      result.push(line.replace(/<(?![a-zA-Z/!])/g, '&lt;'));
    }
  }

  return result.join('\n');
}

let warnings = 0;

for (const page of PAGES) {
  const wikiPath = join(WIKI_DIR, page.wiki);
  const docPath = join(DOCS_DIR, page.doc);

  if (!existsSync(wikiPath)) {
    console.warn(`WARN: wiki file not found, skipping: ${page.wiki}`);
    warnings++;
    continue;
  }

  const wikiContent = readFileSync(wikiPath, 'utf8');
  const withFixedImgs = fixImgSelfClosing(wikiContent);
  const withEscapedOps = escapeMdxOperators(withFixedImgs);
  const rewritten = rewriteLinks(withEscapedOps);
  const output = `${page.frontmatter}\n\n${rewritten}`;

  writeFileSync(docPath, output, 'utf8');
  console.log(`synced: ${page.wiki} → ${page.doc}`);
}

if (warnings > 0) {
  console.error(`\nSync completed with ${warnings} missing wiki file(s). Check the mapping in sync-wiki.mjs.`);
  process.exit(1);
} else {
  console.log('\nAll pages synced successfully.');
}
