import { DialobItem } from "../dialob";

export type ErrorSeverity = 'ERROR' | 'WARNING' | 'INFO' | 'FATAL';
export type ConfirmationDialogType = 'duplicate' | 'delete';
export type OptionsTabType = 'id' | 'label' | 'description' | 'rules' | 'validations' | 'choices' | 'defaults' | 'properties';

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
  activeItem?: DialobItem;
  confirmationDialogType?: ConfirmationDialogType;
  itemOptionsActiveTab?: OptionsTabType;
  highlightedItem?: DialobItem;
};
