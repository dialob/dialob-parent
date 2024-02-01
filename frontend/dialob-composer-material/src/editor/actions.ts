import { RuleEditDialogType, TextEditDialogType } from ".";
import { DialobItem } from "../dialob";

export type EditorAction =
  | { type: 'setActivePage', page: DialobItem }
  | { type: 'setActiveFormLanguage', language: string }
  | { type: 'setTextEditDialogType', dialogType?: TextEditDialogType }
  | { type: 'setRuleEditDialogType', dialogType?: RuleEditDialogType }
  | { type: 'setValidationRuleEditDialogOpen', open: boolean }
