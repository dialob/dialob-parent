import { produce } from 'immer';
import { EditorAction } from './actions';
import { EditorState, RuleEditDialogType, TextEditDialogType} from './types';
import { DialobItem } from '../dialob';

const setActivePage = (state: EditorState, page: DialobItem): void => {
  state.activePage = page;
}

const setActiveFormLanguage = (state: EditorState, language: string): void => {
  state.activeFormLanguage = language;
}

const setTextEditDialogType = (state: EditorState, dialogType?: TextEditDialogType): void => {
  state.textEditDialogType = dialogType;
}

const setRuleEditDialogType = (state: EditorState, dialogType?: RuleEditDialogType): void => {
  state.ruleEditDialogType = dialogType;
}

const setValidationRuleEditDialogOpen = (state: EditorState, open: boolean): void => {
  state.validationRuleEditDialogOpen = open;
}

export const editorReducer = (state: EditorState, action: EditorAction): EditorState => {
  const newState = produce(state, state => {
    if (action.type === 'setActivePage') {
      setActivePage(state, action.page);
    } else if (action.type === 'setActiveFormLanguage') {
      setActiveFormLanguage(state, action.language);
    } else if (action.type === 'setTextEditDialogType') {
      setTextEditDialogType(state, action.dialogType);
    } else if (action.type === 'setRuleEditDialogType') {
      setRuleEditDialogType(state, action.dialogType);
    } else if (action.type === 'setValidationRuleEditDialogOpen') {
      setValidationRuleEditDialogOpen(state, action.open);
    }
  });
  return newState;
}
