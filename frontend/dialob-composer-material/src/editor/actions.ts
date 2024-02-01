import { ConfirmationDialogType, RuleEditDialogType, TextEditDialogType, EditorError } from "./types";
import { DialobItem } from "../dialob";

export type EditorAction =
  | { type: 'setActivePage', page: DialobItem }
  | { type: 'setActiveFormLanguage', language: string }
  | { type: 'setErrors', errors: EditorError[] }
  | { type: 'clearErrors' }
  | { type: 'setConfirmationDialogType', dialogType?: ConfirmationDialogType }
  | { type: 'setActiveItem', item?: DialobItem }
  | { type: 'setTextEditDialogType', dialogType?: TextEditDialogType }
  | { type: 'setRuleEditDialogType', dialogType?: RuleEditDialogType }
  | { type: 'setValidationRuleEditDialogOpen', open: boolean }
