import { produce } from 'immer';
import { EditorAction } from './actions';
import { EditorState} from './types';
import { DialobItem } from '../dialob';

const setActivePage = (state: EditorState, page: DialobItem): void => {
  state.activePage = page;
}

const setActiveFormLanguage = (state: EditorState, language: string): void => {
  state.activeFormLanguage = language;
}

export const editorReducer = (state: EditorState, action: EditorAction): EditorState => {
  const newState = produce(state, state => {
    if (action.type === 'setActivePage') {
      setActivePage(state, action.page);
    } else if (action.type === 'setActiveFormLanguage') {
      setActiveFormLanguage(state, action.language);
    }
  });
  return newState;
}
