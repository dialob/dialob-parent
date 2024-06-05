import { produce } from 'immer';
import { EditorAction } from './actions';
import { ConfirmationDialogType, EditorError, EditorState, OptionsTabType, VariableTabType } from './types';
import { DialobItem } from '../dialob';

const setActivePage = (state: EditorState, page: DialobItem): void => {
  state.activePage = page;
}

const setActiveFormLanguage = (state: EditorState, language: string): void => {
  state.activeFormLanguage = language;
}

const setErrors = (state: EditorState, errors: EditorError[]): void => {
  state.errors = errors;
}

const clearErrors = (state: EditorState): void => {
  state.errors = [];
}

const setActiveItem = (state: EditorState, item?: DialobItem): void => {
  state.activeItem = item;
}

const setConfirmationDialogType = (state: EditorState, dialogType?: ConfirmationDialogType): void => {
  state.confirmationDialogType = dialogType;
}

const setItemOptionsActiveTab = (state: EditorState, tab?: OptionsTabType): void => {
  state.itemOptionsActiveTab = tab;
}

const setHighlightedItem = (state: EditorState, item?: DialobItem): void => {
  state.highlightedItem = item;
}

const setActiveList = (state: EditorState, listId?: string): void => {
  state.activeList = listId;
}

const setActiveVariableTab = (state: EditorState, tab?: VariableTabType): void => {
  state.activeVariableTab = tab;
}

const addExpandedChoiceItem = (state: EditorState, itemId: string): void => {
  state.expandedChoiceItems.push(itemId);
}

const removeExpandedChoiceItem = (state: EditorState, itemId: string): void => {
  state.expandedChoiceItems = state.expandedChoiceItems.filter(id => id !== itemId);
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
    } else if (action.type === 'setActiveItem') {
      setActiveItem(state, action.item);
    } else if (action.type === 'setConfirmationDialogType') {
      setConfirmationDialogType(state, action.dialogType);
    } else if (action.type === 'setItemOptionsActiveTab') {
      setItemOptionsActiveTab(state, action.tab);
    } else if (action.type === 'setHighlightedItem') {
      setHighlightedItem(state, action.item);
    } else if (action.type === 'setActiveList') {
      setActiveList(state, action.listId);
    } else if (action.type === 'setActiveVariableTab') {
      setActiveVariableTab(state, action.tab);
    } else if (action.type === 'addExpandedChoiceItem') {
      addExpandedChoiceItem(state, action.itemId);
    } else if (action.type === 'removeExpandedChoiceItem') {
      removeExpandedChoiceItem(state, action.itemId);
    }
  });
  return newState;
}
