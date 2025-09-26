import React, { useRef, useState, useCallback } from "react";
import { Menu, MenuItem, ListItemText, ListItemIcon, Box, useTheme } from "@mui/material";
import { FormattedMessage } from 'react-intl';
import { ArrowRight, FormatColorText, FormatListBulleted, MenuOpen, Title } from "@mui/icons-material";
import CodeMirror from '@uiw/react-codemirror';
import { markdown } from '@codemirror/lang-markdown';
import { EditorView } from '@codemirror/view';
import Markdown from 'react-markdown';
import { markdownComponents } from "../../defaults/markdown";

type MarkdownAction = { type: "wrap"; before: string; after: string } | { type: "insert"; text: string };

type MenuAnchor = { mouseX: number; mouseY: number } | null;

type MenuState = {
  anchor: MenuAnchor;
  submenu: string | null;
  thirdLevelMenu: string | null;
  submenuAnchor: MenuAnchor;
  thirdLevelAnchor: MenuAnchor;
};

const INITIAL_MENU_STATE: MenuState = {
  anchor: null,
  submenu: null,
  thirdLevelMenu: null,
  submenuAnchor: null,
  thirdLevelAnchor: null,
};

const MAIN_MENU_ITEMS = [
  { id: 'format', icon: FormatColorText, labelKey: 'markdownEditor.format' },
  { id: 'list', icon: FormatListBulleted, labelKey: 'markdownEditor.list' },
  { id: 'paragraph', icon: Title, labelKey: 'markdownEditor.paragraph' },
  { id: 'insert', icon: MenuOpen, labelKey: 'markdownEditor.insert' },
];

const FORMAT_ACTIONS = [
  { labelKey: 'markdownEditor.bold', action: { type: 'wrap' as const, before: '**', after: '**' } },
  { labelKey: 'markdownEditor.italic', action: { type: 'wrap' as const, before: '*', after: '*' } },
  { labelKey: 'markdownEditor.code', action: { type: 'wrap' as const, before: '`', after: '`' } },
];

const LIST_ACTIONS = [
  { labelKey: 'markdownEditor.bulletList', text: '- ' },
  { labelKey: 'markdownEditor.numberedList', text: '1. ' },
];

const INSERT_ACTIONS = [
  { labelKey: 'markdownEditor.link', action: { type: 'wrap' as const, before: '[', after: '](url)' } },
  { labelKey: 'markdownEditor.codeBlock', text: '```\ncode block\n```' },
  { labelKey: 'markdownEditor.horizontalDivider', text: '---' },
  { labelKey: 'markdownEditor.table', text: '| Header 1 | Header 2 | Header 3 |\n|----------|----------|----------|\n| Row 1    | Data     | Data     |\n| Row 2    | Data     | Data     |' },
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

export const MarkdownEditor: React.FC<{ value: string, setValue: (value: string, language: string) => void, language: string }> = ({ value, setValue, language }) => {
  const theme = useTheme();
  const editorRef = useRef<any>(null);
  const [menuState, setMenuState] = useState<MenuState>(INITIAL_MENU_STATE);

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

    setMenuState(INITIAL_MENU_STATE);
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

  const handleContextMenu = (event: React.MouseEvent) => {
    event.preventDefault();
    const view = editorRef.current?.view;
    if (view) {
      const start = view.state.selection.main.from;
      const end = view.state.selection.main.to;

      requestAnimationFrame(() => {
        view.dispatch({
          selection: { anchor: start, head: end }
        });
        view.focus();
      });
    }
    setMenuState({
      ...INITIAL_MENU_STATE,
      anchor: { mouseX: event.clientX + 2, mouseY: event.clientY - 6 }
    });
  };

  const handleClose = () => {
    setMenuState(INITIAL_MENU_STATE);
  };

  const handleMouseEnter = (event: React.MouseEvent, type: 'submenu' | 'thirdLevel') => {
    const rect = event.currentTarget.getBoundingClientRect();
    const anchor = { mouseX: rect.right, mouseY: rect.top };
    
    if (type === 'submenu') {
      setMenuState(prev => ({ ...prev, submenuAnchor: anchor }));
    } else {
      setMenuState(prev => ({ ...prev, thirdLevelAnchor: anchor }));
    }
  };

  return (
    <Box sx={{ my: 1 }}>
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
        onContextMenu={handleContextMenu}
        onKeyDown={handleKeyDown}
      />

      <Menu
        open={menuState.anchor !== null}
        onClose={handleClose}
        anchorReference="anchorPosition"
        anchorPosition={
          menuState.anchor ? { top: menuState.anchor.mouseY, left: menuState.anchor.mouseX } : undefined
        }
        disableAutoFocus
      >
        {MAIN_MENU_ITEMS.map((item) => (
          <MenuItem 
            key={item.id}
            onClick={() => setMenuState(prev => ({ ...prev, submenu: item.id, thirdLevelMenu: null }))}
            onMouseEnter={(e) => handleMouseEnter(e, 'submenu')}
          >
            <ListItemIcon>
              <item.icon />
            </ListItemIcon>
            <ListItemText disableTypography>
              <FormattedMessage id={item.labelKey} />
            </ListItemText>
            <ListItemIcon>
              <ArrowRight />
            </ListItemIcon>
          </MenuItem>
        ))}
      </Menu>

      <Menu
        open={menuState.submenu === "format"}
        onClose={() => setMenuState(prev => ({ ...prev, submenu: null }))}
        anchorReference="anchorPosition"
        anchorPosition={menuState.submenuAnchor ? { 
          top: menuState.submenuAnchor.mouseY, 
          left: menuState.submenuAnchor.mouseX 
        } : undefined}
        disableAutoFocus
      >
        {FORMAT_ACTIONS.map((item) => (
          <MenuItem key={item.labelKey} onClick={() => applyMarkdown(item.action)}>
            <FormattedMessage id={item.labelKey} />
          </MenuItem>
        ))}
      </Menu>

      <Menu
        open={menuState.submenu === "list"}
        onClose={() => setMenuState(prev => ({ ...prev, submenu: null }))}
        anchorReference="anchorPosition"
        anchorPosition={menuState.submenuAnchor ? { 
          top: menuState.submenuAnchor.mouseY, 
          left: menuState.submenuAnchor.mouseX 
        } : undefined}
        disableAutoFocus
      >
        {LIST_ACTIONS.map((item) => (
          <MenuItem key={item.labelKey} onClick={() => insertOnNewLine(item.text)}>
            <FormattedMessage id={item.labelKey} />
          </MenuItem>
        ))}
      </Menu>

      <Menu
        open={menuState.submenu === "paragraph"}
        onClose={() => setMenuState(prev => ({ ...prev, submenu: null }))}
        anchorReference="anchorPosition"
        anchorPosition={menuState.submenuAnchor ? { 
          top: menuState.submenuAnchor.mouseY, 
          left: menuState.submenuAnchor.mouseX 
        } : undefined}
        disableAutoFocus
      >
        {[1, 2, 3, 4, 5, 6].map((lvl) => (
          <MenuItem
            key={lvl}
            onClick={() => insertOnNewLine("#".repeat(lvl) + " ")}
            sx={{ py: 1 }}
          >
            <Markdown components={markdownComponents}>
              {"#".repeat(lvl) + " Heading " + lvl}
            </Markdown>
          </MenuItem>
        ))}
        <MenuItem onClick={() => applyMarkdown({ type: "insert", text: "text" })}>
          <FormattedMessage id="markdownEditor.body" />
        </MenuItem>
      </Menu>

      <Menu
        open={menuState.submenu === "insert"}
        onClose={() => setMenuState(prev => ({ ...prev, submenu: null }))}
        anchorReference="anchorPosition"
        anchorPosition={menuState.submenuAnchor ? { 
          top: menuState.submenuAnchor.mouseY, 
          left: menuState.submenuAnchor.mouseX 
        } : undefined}
        disableAutoFocus
      >
        {INSERT_ACTIONS.map((item) => (
          <MenuItem 
            key={item.labelKey}
            onClick={() => item.action ? applyMarkdown(item.action) : insertOnNewLine(item.text!)}
          >
            <FormattedMessage id={item.labelKey} />
          </MenuItem>
        ))}
        <MenuItem 
          onClick={() => setMenuState(prev => ({ ...prev, thirdLevelMenu: "callouts" }))}
          onMouseEnter={(e) => handleMouseEnter(e, 'thirdLevel')}
        >
          <ListItemText disableTypography>
            <FormattedMessage id="markdownEditor.callouts" />
          </ListItemText>
          <ListItemIcon>
            <ArrowRight />
          </ListItemIcon>
        </MenuItem>
      </Menu>

      <Menu
        open={menuState.thirdLevelMenu === "callouts"}
        onClose={() => setMenuState(prev => ({ ...prev, thirdLevelMenu: null }))}
        anchorReference="anchorPosition"
        anchorPosition={menuState.thirdLevelAnchor ? { 
          top: menuState.thirdLevelAnchor.mouseY, 
          left: menuState.thirdLevelAnchor.mouseX 
        } : undefined}
        disableAutoFocus
      >
        {CALLOUT_ACTIONS.map((item) => (
          <MenuItem key={item.labelKey} onClick={() => insertOnNewLine(item.text)}>
            <FormattedMessage id={item.labelKey} />
          </MenuItem>
        ))}
      </Menu>
    </Box>
  );
};