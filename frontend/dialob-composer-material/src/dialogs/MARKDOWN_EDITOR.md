# MarkdownEditor Documentation

---

The MarkdownEditor is a powerful, feature-rich markdown editing component built with CodeMirror that provides real-time syntax highlighting and intelligent editing features.

## Features

---

### **Syntax Highlighting**
- Real-time markdown syntax highlighting
- Support for all standard markdown elements
- Clean, readable color scheme

### **Smart Text Selection**
- Auto-expands to word boundaries when no text is selected
- Precise formatting application to selected text
- Intelligent cursor positioning after operations

### **Context Menus**
- Right-click context menus for formatting options
- Hierarchical menu structure for organized access
- Visual icons for each formatting type

### **List Management**
- List continuation on Enter
- Smart indentation with Tab/Shift+Tab
- Support for both bullet and numbered lists

### **Block Element Insertion**
- **New line placement**: Headings, lists, code blocks, and horizontal rules are automatically placed on new lines
- **Smart spacing**: Adds appropriate line breaks before and after

### Support for Tables and Callouts
- Using custom MUI rendering to show tables and callouts
- Support for multiple callouts - note (info), tip (success), error, warning

## Shortcuts

---

### **Text Formatting**
| Menu Shortcut | Keyboard Shortcut | Action | Result |
|---------------|------------------|--------|---------|
| Format → Bold | `Ctrl+B` | **Bold** | `**text**` |
| Format → Italic | `Ctrl+I` | *Italic* | `*text*` |
| Format → Code | `Ctrl+E` | `Code` | `` `text` `` |

---

### **Lists**
| Menu Shortcut | Keyboard Shortcut | Action | Result |
|---------------|------------------|--------|---------|
| List → Bullet List | `Ctrl+Shift+L` | Bullet List | `- item` |
| List → Numbered List | `Ctrl+Alt+L` | Numbered List | `1. item` |
| — | `Tab` | Indent List Item | Add 2 spaces |
| — | `Shift+Tab` | Unindent List Item | Remove 2 spaces |

---

### **Headings**
| Menu Shortcut | Keyboard Shortcut | Action | Result |
|---------------|------------------|--------|---------|
| Paragraph → Heading 1 | `Ctrl+1` | Heading 1 | `# text` |
| Paragraph → Heading 2 | `Ctrl+2` | Heading 2 | `## text` |
| Paragraph → Heading 3 | `Ctrl+3` | Heading 3 | `### text` |
| Paragraph → Heading 4 | `Ctrl+4` | Heading 4 | `#### text` |
| Paragraph → Heading 5 | `Ctrl+5` | Heading 5 | `##### text` |
| Paragraph → Heading 6 | `Ctrl+6` | Heading 6 | `###### text` |

---

### **Insert & Undo**
| Menu Shortcut | Keyboard Shortcut | Action | Result |
|---------------|------------------|--------|---------|
| Insert → Link | `Ctrl+K` | Link | `[text](url)` |
| Insert → Code Block | `Ctrl+Shift+C` | Code Block | ```\ncode block\n``` |
| Insert → Horizontal Divider | `Ctrl+-` | Horizontal Rule | `---` |
| Insert → Table | `Ctrl+T` | Table | See table section below |
| — | `Ctrl+Z` / `Cmd+Z` | Undo | Undo last change |
| — | `Ctrl+Y` / `Cmd+Y` | Redo | Redo last change |

---

### **Tables**
| Menu Shortcut | Keyboard Shortcut | Action | Result |
|---------------|------------------|--------|---------|
| Insert → Table | `Ctrl+T` | Insert Table | Creates a 3x3 table template |

**Table Template:**
```
| Header 1 | Header 2 | Header 3 |
|----------|----------|----------|
| Row 1    | Data     | Data     |
| Row 2    | Data     | Data     |
```

**Table Editing Tips:**
- Use `|` to separate columns
- The second row with dashes (`---`) defines the table structure
- Add `:` for alignment: `:---` (left), `:---:` (center), `---:` (right)
- Tables automatically format when rendered

---

### **Callouts (Alerts)**
| Menu Shortcut | Action | Result |
|---------------|--------|---------|
| Insert → Callouts → Note | Insert Note Callout | `> [!NOTE] > This is a note callout.` |
| Insert → Callouts → Tip | Insert Tip Callout | `> [!TIP] > This is a tip callout.` |
| Insert → Callouts → Warning | Insert Warning Callout | `> [!WARNING] > This is a warning callout.` |
| Insert → Callouts → Error | Insert Error Callout | `> [!ERROR] > This is an error callout.` |

  
**Callout Examples:**

> [!NOTE]
> This is a note callout. Use it for helpful information.

> [!TIP]
> This is a tip callout. Use it for useful suggestions.

> [!WARNING]
> This is a warning callout. Use it for important cautions.

> [!ERROR]
> This is an error callout. Use it for critical issues.


**Callout Customization:**
- Add multiple lines by continuing with `>` prefix
- Include other markdown formatting inside callouts
- Callouts support GitHub-flavored markdown syntax


