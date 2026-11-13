#!/usr/bin/env bash
# Syncs GitHub wiki pages into the Docusaurus docs directory.
#
# Reads from ./wiki/ (the cloned wiki repo, placed at repo root by the CI workflow).
# Writes to ./dialob-docusaurus/docs/.
#
# For each page it:
#   1. Reads the wiki Markdown file
#   2. Prepends the Docusaurus YAML frontmatter block
#   3. Fixes non-self-closing <img> tags for MDX compatibility
#   4. Escapes bare < comparison operators outside fenced code blocks
#   5. Rewrites absolute GitHub wiki URLs to Docusaurus-relative slugs
#
# Run from the repo root:
#   bash scripts/sync-wiki.sh
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
WIKI_DIR="$REPO_ROOT/wiki"
DOCS_DIR="$REPO_ROOT/dialob-docusaurus/docs"

# U+2010 NON-BREAKING HYPHEN (‐) — used in wiki filenames between number and title
NBHYPHEN=$'\xe2\x80\x90'
# U+2013 EN DASH (–) — used in the DEL page filename
ENDASH=$'\xe2\x80\x93'

BASE_WIKI_URL='https://github.com/dialob/dialob-parent/wiki'

WARNINGS=0

# ---------------------------------------------------------------------------
# Transformation filters (read stdin, write stdout)
# ---------------------------------------------------------------------------

fix_img_self_closing() {
  # Converts <img ...> to <img ... />. Only fires when the last char before >
  # is not already /, so already-self-closing tags are left unchanged.
  sed -E 's|(<img[^>]*[^/])>|\1 />|g'
}

escape_mdx_operators() {
  # Escapes bare < that is NOT the start of an HTML/JSX tag (letter, /, !).
  # MDX parses < as JSX, so comparison operators like <= cause parse errors.
  #
  # Two regions are left untouched:
  #   - Fenced code blocks  (``` ... ```)  — content is not parsed as MDX
  #   - Inline code spans   (`...`)        — content is not parsed as MDX;
  #     escaping < inside them makes the browser render "&lt;" literally
  #
  # Uses perl for the inline-code-span awareness (available on macOS and
  # GitHub Actions Ubuntu runners without any extra installation).
  perl -pe '
    if (/^\s*```/) { $in_fence = !$in_fence }
    if (!$in_fence) {
      # Alternation: prefer an inline code span (keep it); otherwise escape <
      s{(`[^`]*`)|(<(?![a-zA-Z\/!]))}{$1 // "&lt;"}ge
    }
  '
}

rewrite_links() {
  # Replaces absolute GitHub wiki URLs with Docusaurus-relative slugs.
  # %E2%80%90 = U+2010 NON-BREAKING HYPHEN, %E2%80%93 = U+2013 EN DASH.
  sed \
    -e "s|${BASE_WIKI_URL}/Dialob-composer:-01%E2%80%90Introduction|01-introduction|g" \
    -e "s|${BASE_WIKI_URL}/Dialob-composer:-02%E2%80%90Basic-operations|02-basic-operations|g" \
    -e "s|${BASE_WIKI_URL}/Dialob-composer:-03%E2%80%90Advanced-operations|03-advanced-operations|g" \
    -e "s|${BASE_WIKI_URL}/Dialob-composer:-04%E2%80%90Input-and-output-types|04-input-and-output-types|g" \
    -e "s|${BASE_WIKI_URL}/Dialob-composer:-05%E2%80%90Dialob-Expression-Language-%E2%80%93-DEL|05-dialob-expression-language-del|g" \
    -e "s|${BASE_WIKI_URL}/Dialob-composer:-06%E2%80%90Options-and-settings|06-options-and-settings|g" \
    -e "s|${BASE_WIKI_URL}/Dialob-composer:-07%E2%80%90New-form-walkthrough|07-new-form-walkthrough|g" \
    -e "s|${BASE_WIKI_URL}/Dialob-composer:-08%E2%80%90Customization|08-customization|g"
}

# ---------------------------------------------------------------------------
# Page processing
# ---------------------------------------------------------------------------

process_page() {
  local wiki_file="$1"
  local doc_file="$2"
  local frontmatter="$3"
  local wiki_path="$WIKI_DIR/$wiki_file"
  local doc_path="$DOCS_DIR/$doc_file"

  if [[ ! -f "$wiki_path" ]]; then
    echo "WARN: wiki file not found, skipping: $wiki_file" >&2
    WARNINGS=$(( WARNINGS + 1 ))
    return
  fi

  {
    printf '%s\n\n' "$frontmatter"
    fix_img_self_closing < "$wiki_path" | escape_mdx_operators | rewrite_links
  } > "$doc_path"

  echo "synced: $wiki_file → $doc_file"
}

# ---------------------------------------------------------------------------
# Pages (wiki filename, doc filename, YAML frontmatter)
# ---------------------------------------------------------------------------

process_page \
  'Home.md' \
  'home.md' \
  $'---\nid: home\nslug: /\nsidebar_position: 0\ntitle: Home\n---'

process_page \
  "Dialob-composer:-01${NBHYPHEN}Introduction.md" \
  '01-introduction.md' \
  $'---\nid: 01-introduction\nsidebar_position: 1\ntitle: Introduction\n---'

process_page \
  "Dialob-composer:-02${NBHYPHEN}Basic-operations.md" \
  '02-basic-operations.md' \
  $'---\nid: 02-basic-operations\nsidebar_position: 2\ntitle: Basic operations\n---'

process_page \
  "Dialob-composer:-03${NBHYPHEN}Advanced-operations.md" \
  '03-advanced-operations.md' \
  $'---\nid: 03-advanced-operations\nsidebar_position: 3\ntitle: Advanced operations\n---'

process_page \
  "Dialob-composer:-04${NBHYPHEN}Input-and-output-types.md" \
  '04-input-and-output-types.md' \
  $'---\nid: 04-input-and-output-types\nsidebar_position: 4\ntitle: Input and output types\n---'

process_page \
  "Dialob-composer:-05${NBHYPHEN}Dialob-Expression-Language-${ENDASH}-DEL.md" \
  '05-dialob-expression-language-del.md' \
  $'---\nid: 05-dialob-expression-language-del\nsidebar_position: 5\ntitle: "Dialob Expression Language (DEL)"\n---'

process_page \
  "Dialob-composer:-06${NBHYPHEN}Options-and-settings.md" \
  '06-options-and-settings.md' \
  $'---\nid: 06-options-and-settings\nsidebar_position: 6\ntitle: Options and settings\n---'

process_page \
  "Dialob-composer:-07${NBHYPHEN}New-form-walkthrough.md" \
  '07-new-form-walkthrough.md' \
  $'---\nid: 07-new-form-walkthrough\nsidebar_position: 7\ntitle: New form walkthrough\n---'

process_page \
  "Dialob-composer:-08${NBHYPHEN}Customization.md" \
  '08-customization.md' \
  $'---\nid: 08-customization\nsidebar_position: 8\ntitle: Customization\n---'

# ---------------------------------------------------------------------------
# Exit
# ---------------------------------------------------------------------------

if (( WARNINGS > 0 )); then
  echo "" >&2
  echo "Sync completed with $WARNINGS missing wiki file(s). Check the mapping in scripts/sync-wiki.sh." >&2
  exit 1
else
  echo ""
  echo "All pages synced successfully."
fi
