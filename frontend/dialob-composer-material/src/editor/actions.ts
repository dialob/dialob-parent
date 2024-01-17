import { ConfirmationDialogType, EditorError } from "./types";
import { DialobItem } from "../dialob";

export type EditorAction =
  | { type: 'setActivePage', page: DialobItem }
  | { type: 'setActiveFormLanguage', language: string }
  | { type: 'setErrors', errors: EditorError[] }
  | { type: 'clearErrors' }
  | { type: 'setConfirmationDialogType', dialogType?: ConfirmationDialogType }
  | { type: 'setActiveItem', item?: DialobItem }
