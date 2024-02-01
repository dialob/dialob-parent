import { produce } from 'immer';
import { EditorAction } from './actions';
import { ConfirmationDialogType, EditorError, EditorState} from './types';
import { DialobItem } from '../dialob';

const setActivePage = (state: EditorState, page: DialobItem): void => {
  state.activePage = page;
}

const setActiveFormLanguage = (state: EditorState, language: string): void => {
  state.activeFormLanguage = language;
}

const setErrors = (state: EditorState, errors: EditorError[]): void => {
  state.errors.push(...errors);
}

const clearErrors = (state: EditorState): void => {
  state.errors = [];
}

const setConfirmationDialogType = (state: EditorState, dialogType?: ConfirmationDialogType): void => {
  state.confirmationDialogType = dialogType;
}

const setActiveItem = (state: EditorState, item?: DialobItem): void => {
  state.activeItem = item;
}

export const editorReducer = (state: EditorState, action: EditorAction): EditorState => {
  const newState = produce(state, state => {
    if (action.type === 'setActivePage') {
      setActivePage(state, action.page);
    } else if (action.type === 'setActiveFormLanguage') {
      setActiveFormLanguage(state, action.language);
    } else if (action.type === 'setErrors') {
      setErrors(state, action.errors);
    } else if (action.type === 'clearErrors') {
      clearErrors(state);
    } else if (action.type === 'setConfirmationDialogType') {
      setConfirmationDialogType(state, action.dialogType);
    } else if (action.type === 'setActiveItem') {
      setActiveItem(state, action.item);
    }
  });
  return newState;
}
