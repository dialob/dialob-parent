import { DialobItem } from "../dialob";

export type ErrorSeverity = 'ERROR' | 'WARNING' | 'INFO' | 'FATAL';
export type ConfirmationDialogType = 'duplicate' | 'delete';
export type TextEditDialogType = 'label' | 'description';
export type RuleEditDialogType = 'requirement' | 'visibility';

export type EditorError = {
  severity: ErrorSeverity;
  message: string;
  type: string;
  itemId?: string;
  expression?: string;
};

export type EditorState = {
  activePage?: DialobItem;
  activeFormLanguage: string;
  errors: EditorError[];
  confirmationDialogType?: ConfirmationDialogType;
  activeItem?: DialobItem;
  textEditDialogType?: TextEditDialogType;
  ruleEditDialogType?: RuleEditDialogType;
  validationRuleEditDialogOpen?: boolean;
};
