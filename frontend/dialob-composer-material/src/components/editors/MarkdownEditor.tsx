import React, { useRef, useState, useCallback } from 'react';
import { Box, useTheme, IconButton, Tooltip, Button, Menu, MenuItem, Divider } from '@mui/material';
import { FormatBold, FormatItalic, Code, FormatListBulleted, FormatListNumbered, Title, Link, DataObject, Remove, TableChart, FormatQuote, ArrowDropDown } from '@mui/icons-material';
import { FormattedMessage, useIntl } from 'react-intl';
import CodeMirror from '@uiw/react-codemirror';
import { markdown } from '@codemirror/lang-markdown';
import { EditorView } from '@codemirror/view';
import Markdown from 'react-markdown';
import { markdownComponents } from '../../defaults/markdown';
import { CodeEditorWithClear } from '../code/CodeEditorWithClear';

type MarkdownAction = { type: 'wrap'; before: string; after: string } | { type: 'insert'; text: string };

const FORMAT_ACTIONS = [
  { labelKey: 'markdownEditor.bold', icon: FormatBold, action: { type: 'wrap' as const, before: '**', after: '**' } },
  { labelKey: 'markdownEditor.italic', icon: FormatItalic, action: { type: 'wrap' as const, before: '*', after: '*' } },
  { labelKey: 'markdownEditor.code', icon: Code, action: { type: 'wrap' as const, before: '`', after: '`' } },
];

const LIST_ACTIONS = [
  { labelKey: 'markdownEditor.bulletList', icon: FormatListBulleted, text: '- ' },
  { labelKey: 'markdownEditor.numberedList', icon: FormatListNumbered, text: '1. ' },
];

const INSERT_LINK_ACTION = {
  labelKey: 'markdownEditor.link',
  action: { type: 'wrap' as const, before: '[', after: '](url)' },
};

const INSERT_ACTIONS = [
  { labelKey: 'markdownEditor.codeBlock', icon: DataObject, text: '```\ncode block\n```' },
  { labelKey: 'markdownEditor.horizontalDivider', icon: Remove, text: '---' },
  { labelKey: 'markdownEditor.table', icon: TableChart, text: '| Header 1 | Header 2 | Header 3 |\n|----------|----------|----------|\n| Row 1    | Data     | Data     |\n| Row 2    | Data     | Data     |' },
];

const CALLOUT_ACTIONS = [
  { labelKey: 'markdownEditor.note', text: '> [!NOTE]\n> This is a note callout.' },
  { labelKey: 'markdownEditor.tip', text: '> [!TIP]\n> This is a tip callout.' },
  { labelKey: 'markdownEditor.warning', text: '> [!WARNING]\n> This is a warning callout.' },
  { labelKey: 'markdownEditor.error', text: '> [!ERROR]\n> This is an error callout.' },
];

type KeyboardShortcut = {
  key: string;
  ctrl: boolean;
  shift?: boolean;
  alt?: boolean;
  action?: MarkdownAction;
  text?: string;
};

const KEYBOARD_SHORTCUTS: KeyboardShortcut[] = [
  { key: 'b', ctrl: true, action: { type: 'wrap', before: '**', after: '**' } },
  { key: 'i', ctrl: true, action: { type: 'wrap', before: '*', after: '*' } },
  { key: 'e', ctrl: true, action: { type: 'wrap', before: '`', after: '`' } },
  { key: 'k', ctrl: true, action: { type: 'wrap', before: '[', after: '](url)' } },
  { key: 'C', ctrl: true, shift: true, text: '```\ncode block\n```' },
  { key: 'L', ctrl: true, shift: true, text: '- ' },
  { key: 'l', ctrl: true, alt: true, text: '1. ' },
  { key: '-', ctrl: true, text: '---' },
  { key: 't', ctrl: true, text: '| Header 1 | Header 2 | Header 3 |\n|----------|----------|----------|\n| Row 1    | Data     | Data     |\n| Row 2    | Data     | Data     |' },
  { key: '1', ctrl: true, text: '# ' },
  { key: '2', ctrl: true, text: '## ' },
  { key: '3', ctrl: true, text: '### ' },
  { key: '4', ctrl: true, text: '#### ' },
  { key: '5', ctrl: true, text: '##### ' },
  { key: '6', ctrl: true, text: '###### ' },
];

export const MarkdownEditor: React.FC<{ 
  value: string | undefined, 
  setValue: (value: string, language: string) => void, 
  language: string,
  onClear: (language: string) => void
}> = ({ value, setValue, language, onClear }) => {
  const theme = useTheme();
  const intl = useIntl();
  const editorRef = useRef<{ view?: EditorView }>(null);
  const [paragraphAnchor, setParagraphAnchor] = useState<null | HTMLElement>(null);
  const [calloutsAnchor, setCalloutsAnchor] = useState<null | HTMLElement>(null);

  const applyMarkdown = useCallback((action: MarkdownAction) => {
    const view = editorRef.current?.view;
    if (!view) return;

    let start = view.state.selection.main.from;
    let end = view.state.selection.main.to;
    let alreadyWrapped = false;
    const text = value || '';

    // Auto-expand word selection if no text is selected
    if (action.type === "wrap" && start === end) {
      // Find word boundaries around cursor using Unicode-aware pattern
      const wordPattern = /[\p{L}\p{M}\p{N}\p{Pc}]/u;
      
      let left = start;
      while (left > 0 && wordPattern.test(text[left - 1])) left--;

      let right = end;
      while (right < text.length && wordPattern.test(text[right])) right++;

      const foundWord = left < right && wordPattern.test(text.slice(left, right));
      const cursorInWord = start >= left && start <= right;
      
      if (foundWord && cursorInWord) {
        // Check if the word is already wrapped with the same markup
        const { before, after } = action;
        const beforeLeft = Math.max(0, left - before.length);
        const afterRight = Math.min(text.length, right + after.length);
        
        if (text.slice(beforeLeft, left) === before && text.slice(right, afterRight) === after) {
          // Word is already wrapped, expand selection to include the markup for removal
          alreadyWrapped = true;
          start = beforeLeft;
          end = afterRight;
        } else {
          // Word is not wrapped, select just the word for wrapping
          start = left;
          end = right;
        }
      }
    }

    const beforeText = text.slice(0, start);
    const selected = text.slice(start, end);
    const afterText = text.slice(end);

    let newValue = text;
    let newCursor = start;

    if (action.type === "wrap") {
      const { before, after } = action;

      let wrappedText;
      if (alreadyWrapped) {
        wrappedText = selected.slice(before.length, selected.length - after.length);
      } else {
        wrappedText = before + (selected || "text") + after;
      }
      newValue = beforeText + wrappedText + afterText;
      newCursor = start + wrappedText.length;

    } else if (action.type === "insert") {
      newValue = beforeText + action.text + afterText;
      newCursor = start + action.text.length;
    }

    setValue(newValue, language);

    requestAnimationFrame(() => {
      if (action.type === "wrap") {
        const { before } = action;
        let cursorPos;
        
        if (selected === "" || (selected === "text" && action.type === "wrap")) {
          // No real text was selected or placeholder was used, position cursor between the markup
          cursorPos = start + before.length;
        } else {
          // Real text was selected, position cursor at the end of text, before markup
          cursorPos = newCursor - (alreadyWrapped ? 0 : before.length)
        }

        view.dispatch({
          selection: { anchor: cursorPos, head: cursorPos }
        });
      
      } else {
        // For insert actions, position cursor at the end
        view.dispatch({
          selection: { anchor: newCursor, head: newCursor }
        });
      }
      view.focus();
    });

    setParagraphAnchor(null);
    setCalloutsAnchor(null);
  }, [value, setValue, language]);

  const insertOnNewLine = useCallback((text: string) => {
    const view = editorRef.current?.view;
    if (!view) return;

    const start = view.state.selection.main.from;
    const currentText = value || '';
    
    // Check if newlines need to be added
    const beforeCursor = currentText.slice(0, start);
    const afterCursor = currentText.slice(start);
    
    let prefix = "";
    let suffix = "";
    
    // Add newline before if not at start of line
    if (beforeCursor.length > 0 && !beforeCursor.endsWith('\n\n')) {
      prefix = "\n\n";
    }
    
    // Add newline after if there's content after and it doesn't start with newline
    if (afterCursor.length > 0 && !afterCursor.startsWith('\n\n')) {
      suffix = "\n\n";
    }
    
    const fullText = prefix + text + suffix;
    applyMarkdown({ type: "insert", text: fullText });
  }, [value, applyMarkdown]);

  const handleKeyDown = useCallback((event: React.KeyboardEvent) => {
    const { ctrlKey, altKey, shiftKey, key } = event;

    // Check predefined shortcuts
    for (const shortcut of KEYBOARD_SHORTCUTS) {
      const matchesModifiers = 
        (shortcut.ctrl === ctrlKey) &&
        (shortcut.shift === !!shiftKey) &&
        (shortcut.alt === !!altKey);
      
      if (matchesModifiers && shortcut.key === key) {
        event.preventDefault();
        if (shortcut.action) {
          applyMarkdown(shortcut.action);
        } else if (shortcut.text) {
          insertOnNewLine(shortcut.text);
        }
        return;
      }
    }
  }, [applyMarkdown, insertOnNewLine]);

  return (
    <Box>
      <Box sx={{
        display: 'flex',
        flexWrap: 'wrap',
        alignItems: 'center',
        gap: 0.5,
        py: 0.5,
        px: 0.5,
        borderBottom: 1,
        borderColor: 'divider',
        backgroundColor: 'action.hover',
      }}>
        {FORMAT_ACTIONS.map((item) => (
          <Tooltip key={item.labelKey} title={intl.formatMessage({ id: item.labelKey })}>
            <IconButton size="small" onClick={() => applyMarkdown(item.action)} aria-label={intl.formatMessage({ id: item.labelKey })}>
              <item.icon fontSize="small" />
            </IconButton>
          </Tooltip>
        ))}
        <Divider orientation="vertical" flexItem sx={{ mx: 0.5 }} />
        {LIST_ACTIONS.map((item) => (
          <Tooltip key={item.labelKey} title={intl.formatMessage({ id: item.labelKey })}>
            <IconButton size="small" onClick={() => insertOnNewLine(item.text)} aria-label={intl.formatMessage({ id: item.labelKey })}>
              <item.icon fontSize="small" />
            </IconButton>
          </Tooltip>
        ))}
        <Divider orientation="vertical" flexItem sx={{ mx: 0.5 }} />
        <Tooltip title={<FormattedMessage id="markdownEditor.paragraph" />}>
          <Button size="small" startIcon={<Title />} endIcon={<ArrowDropDown />} onClick={(e) => setParagraphAnchor(e.currentTarget)} aria-haspopup="true" aria-controls={paragraphAnchor ? 'paragraph-menu' : undefined}>
            <FormattedMessage id="markdownEditor.paragraph" />
          </Button>
        </Tooltip>
        <Menu id="paragraph-menu" anchorEl={paragraphAnchor} open={Boolean(paragraphAnchor)} onClose={() => setParagraphAnchor(null)} anchorOrigin={{ vertical: 'bottom', horizontal: 'left' }} transformOrigin={{ vertical: 'top', horizontal: 'left' }}>
          {[1, 2, 3, 4, 5, 6].map((lvl) => (
            <MenuItem key={lvl} onClick={() => { insertOnNewLine('#'.repeat(lvl) + ' '); setParagraphAnchor(null); }}>
              <Markdown components={markdownComponents}>{'#'.repeat(lvl) + ' Heading ' + lvl}</Markdown>
            </MenuItem>
          ))}
          <MenuItem onClick={() => { applyMarkdown({ type: 'insert', text: 'text' }); setParagraphAnchor(null); }}>
            <FormattedMessage id="markdownEditor.body" />
          </MenuItem>
        </Menu>
        <Divider orientation="vertical" flexItem sx={{ mx: 0.5 }} />
        <Tooltip title={intl.formatMessage({ id: INSERT_LINK_ACTION.labelKey })}>
          <IconButton size="small" onClick={() => applyMarkdown(INSERT_LINK_ACTION.action)} aria-label={intl.formatMessage({ id: INSERT_LINK_ACTION.labelKey })}>
            <Link fontSize="small" />
          </IconButton>
        </Tooltip>
        {INSERT_ACTIONS.map((item) => (
          <Tooltip key={item.labelKey} title={intl.formatMessage({ id: item.labelKey })}>
            <IconButton size="small" onClick={() => insertOnNewLine(item.text)} aria-label={intl.formatMessage({ id: item.labelKey })}>
              <item.icon fontSize="small" />
            </IconButton>
          </Tooltip>
        ))}
        <Button size="small" startIcon={<FormatQuote />} endIcon={<ArrowDropDown />} onClick={(e) => setCalloutsAnchor(e.currentTarget)} aria-haspopup="true" aria-controls={calloutsAnchor ? 'callouts-menu' : undefined}>
          <FormattedMessage id="markdownEditor.callouts" />
        </Button>
        <Menu id="callouts-menu" anchorEl={calloutsAnchor} open={Boolean(calloutsAnchor)} onClose={() => setCalloutsAnchor(null)} anchorOrigin={{ vertical: 'bottom', horizontal: 'left' }} transformOrigin={{ vertical: 'top', horizontal: 'left' }}>
          {CALLOUT_ACTIONS.map((item) => (
            <MenuItem key={item.labelKey} onClick={() => { insertOnNewLine(item.text); setCalloutsAnchor(null); }}>
              <FormattedMessage id={item.labelKey} />
            </MenuItem>
          ))}
        </Menu>
      </Box>
      <CodeEditorWithClear value={value} onClear={handleClear}>
        <CodeMirror
          ref={editorRef}
          value={value}
          onChange={(val: string) => setValue(val || '', language)}
          extensions={[
            markdown({
              completeHTMLTags: false
            }),
            EditorView.lineWrapping,
            EditorView.theme({
              '&': {
                fontSize: theme.typography.body1.fontSize || '15px',
              },
              '.cm-activeLine': {
                backgroundColor: 'transparent',
              },
              '.cm-activeLineGutter': {
                backgroundColor: 'transparent',
              }
            })
          ]}
          basicSetup={{
            lineNumbers: false,
            foldGutter: false,
            dropCursor: false,
            allowMultipleSelections: false,
            indentOnInput: true,
            bracketMatching: true,
            closeBrackets: true,
            autocompletion: false,
            highlightSelectionMatches: false,
            searchKeymap: false,
          }}
          onKeyDown={handleKeyDown}
        />
      </CodeEditorWithClear>
    </Box>
  );
};
