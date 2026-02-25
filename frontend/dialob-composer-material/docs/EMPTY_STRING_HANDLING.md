# Empty String and Undefined Handling

This document describes how the Dialob Composer distinguishes between empty strings and undefined values.

## Core Principle

- **Empty string (`''`)**: Valid value that can be stored in the data model
- **Undefined**: Missing value, triggers key deletion from the data model
- **Clear button**: Explicitly sets value to `undefined` to delete it

## Clear Button Behavior

All optional input fields show a clear button (X icon) when `value !== undefined`. Clicking it sets the value to `undefined`, which triggers deletion of the key from the data object.

## Missing Value Detection

**Translation detection**: Both `undefined` and `''` are considered missing translations.

**Backend validation**: Both `undefined` and `''` should be treated as missing for required fields such as variable expressions.

## UI Components

### `TextEditorWithClear`
Wraps MUI `TextField` with inline clear button. Used for: descriptions, default values.

### `CodeEditorWithClear`
Wraps CodeMirror editors with overlaid clear button. Used for: expressions, rules, validation rules, markdown.

### Localized Fields
Each language has its own clear button that removes that specific language key. Used in: labels, descriptions, validation messages, valueset entry labels.

### Component State
Components initialize state as `string | undefined` and handle both empty strings and undefined values. The clear button always sets state to `undefined`.

## Type Signatures

All clearable fields accept `string | undefined`:
- `updateExpressionVariable(variableId, expression)`
- `updateVariableDescription(variableId, description)`
- `setValidationExpression(itemId, index, expression)`
- `updateValueSetEntryLabel(valueSetId, index, text, language)`
- `updateItem(itemId, attribute, value, language?)`

## Fields With Clear Buttons

**Text fields**: Item default values, variable descriptions, variable default values
**Code fields**: Variable expressions, rules (visibility, requirement, readonly, canaddrow, canremoverow), validation expressions
**Localized fields**: Item labels, descriptions, validation messages, valueset entry labels, markdown content

## Quick Reference

| Component | Clear Action | State Type |
|-----------|--------------|------------|
| TextEditorWithClear | Sets `undefined` | `string \| undefined` |
| CodeEditorWithClear | Sets `undefined` | `string \| undefined` |
| Localized clear buttons | Deletes language key | Per-language |
| Missing detection | Checks both `undefined` and `''` | Translation/validation |
