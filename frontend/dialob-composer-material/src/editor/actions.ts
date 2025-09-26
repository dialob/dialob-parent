import { ConfirmationDialogType, EditorError, OptionsTabType, VariableTabType } from "./types";
import { DialobItem } from "../types";

export type EditorAction =
  | { type: 'setActivePage', page: DialobItem }
  | { type: 'setActiveFormLanguage', language: string }
  | { type: 'setErrors', errors: EditorError[] }
  | { type: 'clearErrors' }
  | { type: 'setActiveItem', item?: DialobItem }
  | { type: 'setConfirmationDialogType', dialogType?: ConfirmationDialogType }
  | { type: 'setItemOptionsActiveTab', tab?: OptionsTabType }
  | { type: 'setHighlightedItem', item?: DialobItem }
  | { type: 'setActiveList', listId?: string }
  | { type: 'setActiveVariableTab', tab?: VariableTabType }
  | { type: 'setConfirmationActiveItem', item?: DialobItem }
  | { type: 'toggleItemCollapsed', itemId: string }
  | { type: 'setMarkdownHelpDialogOpen', open: boolean }
