import React, { useRef, useState, useCallback } from "react";
import { Menu, MenuItem, ListItemText, ListItemIcon, Box, useTheme } from "@mui/material";
import { ArrowRight, FormatColorText, FormatListBulleted, MenuOpen, Title } from "@mui/icons-material";
import CodeMirror from '@uiw/react-codemirror';
import { markdown } from '@codemirror/lang-markdown';
import { EditorView } from '@codemirror/view';
import Markdown from 'react-markdown';
import { markdownComponents } from "../../defaults/markdown";

type MarkdownAction = { type: "wrap"; before: string; after: string } | { type: "insert"; text: string };

export const MarkdownEditor: React.FC<{ value: string, setValue: (value: string, language: string) => void, language: string }> = ({ value, setValue, language }) => {
  const theme = useTheme();
  const editorRef = useRef<any>(null);
  const [anchor, setAnchor] = useState<{ mouseX: number; mouseY: number } | null>(null);
  const [submenu, setSubmenu] = useState<null | string>(null);
  const [thirdLevelMenu, setThirdLevelMenu] = useState<null | string>(null);
  const [submenuAnchor, setSubmenuAnchor] = useState<{ mouseX: number; mouseY: number } | null>(null);
  const [thirdLevelAnchor, setThirdLevelAnchor] = useState<{ mouseX: number; mouseY: number } | null>(null);

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

    handleClose();
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
    const isCtrl = event.ctrlKey;

    // Bold shortcut
    if (isCtrl && event.key === 'b') {
      event.preventDefault();
      applyMarkdown({ type: "wrap", before: "**", after: "**" });
      return;
    }
    
    // Italic shortcut
    if (isCtrl && event.key === 'i') {
      event.preventDefault();
      applyMarkdown({ type: "wrap", before: "*", after: "*" });
      return;
    }
    
    // Code formatting shortcut
    if (isCtrl && event.key === 'e') {
      event.preventDefault();
      applyMarkdown({ type: "wrap", before: "`", after: "`" });
      return;
    }
    
    // Link shortcut
    if (isCtrl && event.key === 'k') {
      event.preventDefault();
      applyMarkdown({ type: "wrap", before: "[", after: "](url)" });
      return;
    }
    
    // Code block shortcut
    if (isCtrl && event.shiftKey && event.key === 'C') {
      event.preventDefault();
      insertOnNewLine("```\ncode block\n```");
      return;
    }
    
    // Bullet list shortcut
    if (isCtrl && event.shiftKey && event.key === 'L') {
      event.preventDefault();
      insertOnNewLine("- ");
      return;
    }
    
    // Numbered list shortcut
    if (isCtrl && event.altKey && event.key === 'l') {
      event.preventDefault();
      insertOnNewLine("1. ");
      return;
    }
    
    // Heading shortcuts (Ctrl+1 through Ctrl+6)
    if (isCtrl && /^[1-6]$/.test(event.key)) {
      event.preventDefault();
      const level = parseInt(event.key);
      insertOnNewLine("#".repeat(level) + " ");
      return;
    }
    
    // Horizontal rule shortcut
    if (isCtrl && event.key === '-') {
      event.preventDefault();
      insertOnNewLine("---");
      return;
    }
    
    // Table shortcut
    if (isCtrl && event.key === 't') {
      event.preventDefault();
      insertOnNewLine("| Header 1 | Header 2 | Header 3 |\n|----------|----------|----------|\n| Row 1    | Data     | Data     |\n| Row 2    | Data     | Data     |");
      return;
    }
  }, [applyMarkdown, insertOnNewLine, value, setValue, language]);

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
    setAnchor({ mouseX: event.clientX + 2, mouseY: event.clientY - 6 });
    setSubmenu(null);
  };

  const handleClose = () => {
    setAnchor(null);
    setSubmenu(null);
    setThirdLevelMenu(null);
    setSubmenuAnchor(null);
    setThirdLevelAnchor(null);
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
        open={anchor !== null}
        onClose={handleClose}
        anchorReference="anchorPosition"
        anchorPosition={
          anchor ? { top: anchor.mouseY, left: anchor.mouseX } : undefined
        }
        disableAutoFocus
      >
        <MenuItem 
          onClick={() => setSubmenu("format")}
          onMouseEnter={(e) => {
            const rect = e.currentTarget.getBoundingClientRect();
            setSubmenuAnchor({ mouseX: rect.right, mouseY: rect.top });
          }}
        >
          <ListItemIcon>
            <FormatColorText />
          </ListItemIcon>
          <ListItemText disableTypography>Format</ListItemText>
          <ListItemIcon>
            <ArrowRight />
          </ListItemIcon>
        </MenuItem>
        <MenuItem 
          onClick={() => setSubmenu("list")}
          onMouseEnter={(e) => {
            const rect = e.currentTarget.getBoundingClientRect();
            setSubmenuAnchor({ mouseX: rect.right, mouseY: rect.top });
          }}
        >
          <ListItemIcon>
            <FormatListBulleted />
          </ListItemIcon>
          <ListItemText disableTypography>List</ListItemText>
          <ListItemIcon>
            <ArrowRight />
          </ListItemIcon>
        </MenuItem>
        <MenuItem 
          onClick={() => setSubmenu("paragraph")}
          onMouseEnter={(e) => {
            const rect = e.currentTarget.getBoundingClientRect();
            setSubmenuAnchor({ mouseX: rect.right, mouseY: rect.top });
          }}
        >
          <ListItemIcon>
            <Title />
          </ListItemIcon>
          <ListItemText disableTypography>Paragraph</ListItemText>
          <ListItemIcon>
            <ArrowRight />
          </ListItemIcon>
        </MenuItem>
        <MenuItem 
          onClick={() => setSubmenu("insert")}
          onMouseEnter={(e) => {
            const rect = e.currentTarget.getBoundingClientRect();
            setSubmenuAnchor({ mouseX: rect.right, mouseY: rect.top });
          }}
        >
          <ListItemIcon>
            <MenuOpen />
          </ListItemIcon>
          <ListItemText disableTypography>Insert</ListItemText>
          <ListItemIcon>
            <ArrowRight />
          </ListItemIcon>
        </MenuItem>
      </Menu>

      <Menu
        open={submenu === "format"}
        onClose={() => setSubmenu(null)}
        anchorReference="anchorPosition"
        anchorPosition={submenuAnchor ? { top: submenuAnchor.mouseY, left: submenuAnchor.mouseX } : undefined}
        disableAutoFocus
      >
        <MenuItem onClick={() => applyMarkdown({ type: "wrap", before: "**", after: "**" })}>Bold</MenuItem>
        <MenuItem onClick={() => applyMarkdown({ type: "wrap", before: "*", after: "*" })}>Italic</MenuItem>
        <MenuItem onClick={() => applyMarkdown({ type: "wrap", before: "`", after: "`" })}>Code</MenuItem>
      </Menu>

      <Menu
        open={submenu === "list"}
        onClose={() => setSubmenu(null)}
        anchorReference="anchorPosition"
        anchorPosition={submenuAnchor ? { top: submenuAnchor.mouseY, left: submenuAnchor.mouseX } : undefined}
        disableAutoFocus
      >
        <MenuItem onClick={() => insertOnNewLine("- ")}>Bullet List</MenuItem>
        <MenuItem onClick={() => insertOnNewLine("1. ")}>Numbered List</MenuItem>
      </Menu>

      <Menu
        open={submenu === "paragraph"}
        onClose={() => setSubmenu(null)}
        anchorReference="anchorPosition"
        anchorPosition={submenuAnchor ? { top: submenuAnchor.mouseY, left: submenuAnchor.mouseX } : undefined}
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
        <MenuItem onClick={() => applyMarkdown({ type: "insert", text: "text" })}>Body</MenuItem>
      </Menu>

      <Menu
        open={submenu === "insert"}
        onClose={() => setSubmenu(null)}
        anchorReference="anchorPosition"
        anchorPosition={submenuAnchor ? { top: submenuAnchor.mouseY, left: submenuAnchor.mouseX } : undefined}
        disableAutoFocus
      >
        <MenuItem
          onClick={() => applyMarkdown({ type: "wrap", before: "[", after: "](url)" })}
        >
          Link
        </MenuItem>
        <MenuItem onClick={() => insertOnNewLine("```\ncode block\n```")}>
          Code Block
        </MenuItem>
        <MenuItem onClick={() => insertOnNewLine("---")}>
          Horizontal Divider
        </MenuItem>
        <MenuItem onClick={() => insertOnNewLine("| Header 1 | Header 2 | Header 3 |\n|----------|----------|----------|\n| Row 1    | Data     | Data     |\n| Row 2    | Data     | Data     |")}>          
          Table
        </MenuItem>
        <MenuItem 
          onClick={() => setThirdLevelMenu("callouts")}
          onMouseEnter={(e) => {
            const rect = e.currentTarget.getBoundingClientRect();
            setThirdLevelAnchor({ mouseX: rect.right, mouseY: rect.top });
          }}
        >
          <ListItemText disableTypography>Callouts</ListItemText>
          <ListItemIcon>
            <ArrowRight />
          </ListItemIcon>
        </MenuItem>
      </Menu>

      <Menu
        open={thirdLevelMenu === "callouts"}
        onClose={() => setThirdLevelMenu(null)}
        anchorReference="anchorPosition"
        anchorPosition={thirdLevelAnchor ? { top: thirdLevelAnchor.mouseY, left: thirdLevelAnchor.mouseX } : undefined}
        disableAutoFocus
      >
        <MenuItem onClick={() => insertOnNewLine("> [!NOTE]\n> This is a note callout.")}>Note</MenuItem>
        <MenuItem onClick={() => insertOnNewLine("> [!TIP]\n> This is a tip callout.")}>Tip</MenuItem>
        <MenuItem onClick={() => insertOnNewLine("> [!WARNING]\n> This is a warning callout.")}>Warning</MenuItem>
        <MenuItem onClick={() => insertOnNewLine("> [!ERROR]\n> This is an error callout.")}>Error</MenuItem>
      </Menu>
    </Box>
  );
};
