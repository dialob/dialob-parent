import { ConfirmationDialogType, TextEditDialogType, RuleEditDialogType, EditorError, OptionsTabType } from "./types";
import { DialobItem } from "../dialob";

export type EditorAction =
  | { type: 'setActivePage', page: DialobItem }
  | { type: 'setActiveFormLanguage', language: string }
  | { type: 'setErrors', errors: EditorError[] }
  | { type: 'clearErrors' }
  | { type: 'setActiveItem', item?: DialobItem }
  | { type: 'setConfirmationDialogType', dialogType?: ConfirmationDialogType }
  | { type: 'setTextEditDialogType', dialogType?: TextEditDialogType }
  | { type: 'setRuleEditDialogType', dialogType?: RuleEditDialogType }
  | { type: 'setValidationRuleEditDialogOpen', open: boolean }
  | { type: 'setItemOptionsActiveTab', tab?: OptionsTabType }
  | { type: 'setHighlightedItem', item?: DialobItem };