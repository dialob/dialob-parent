import { DialobItem } from "../dialob";

export type ErrorSeverity = 'ERROR' | 'WARNING' | 'INFO' | 'FATAL';
export type ComposerStatus = 'ERROR' | 'WARNING' | 'INFO' | 'FATAL' | 'OK';
export type ConfirmationDialogType = 'duplicate' | 'delete';
export type OptionsTabType = 'id' | 'label' | 'description' | 'rules' | 'validations' | 'choices' | 'properties';
export type VariableTabType = 'context' | 'expression';

export type EditorError = {
  level: ErrorSeverity;
  message: string;
  type?: string;
  itemId?: string;
  expression?: string;
  startIndex?: number;
  endIndex?: number;
  index?: number;
};

export type EditorState = {
  activePage?: DialobItem;
  activeFormLanguage: string;
  errors: EditorError[];
  activeItem?: DialobItem;
  confirmationDialogType?: ConfirmationDialogType;
  itemOptionsActiveTab?: OptionsTabType;
  highlightedItem?: DialobItem;
  activeList?: string;
  activeVariableTab?: VariableTabType;
};
