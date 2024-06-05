import React, { useReducer, Dispatch } from 'react';
import { editorReducer } from '../reducer';
import { EditorAction } from '../actions';
import { EditorState } from '../types';


const INITIAL_EDITOR: EditorState = {
  activeFormLanguage: 'en',
  errors: [],
  expandedChoiceItems: [],
};

export const EditorContext = React.createContext<{ state: EditorState, dispatch: Dispatch<EditorAction> }>({
  state: INITIAL_EDITOR,
  dispatch: () => null
});

export interface ComposerProviderProps {
  children: React.ReactNode;
}

export const EditorProvider: React.FC<ComposerProviderProps> = ({ children }) => {
  const [state, dispatch] = useReducer(editorReducer, INITIAL_EDITOR);

  return (
    <EditorContext.Provider value={{ state, dispatch }}>
      {children}
    </EditorContext.Provider>
  );
}
