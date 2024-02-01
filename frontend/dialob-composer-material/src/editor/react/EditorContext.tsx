import React, { useReducer, Dispatch } from 'react';
import { editorReducer } from '../reducer';
import { EditorAction } from '../actions';
import { EditorError, EditorState, ErrorSeverity } from '../types';

const DEMO_ERRORS: EditorError[] = [
  {
    severity: 'WARNING' as ErrorSeverity,
    message: 'UNKNOWN_FUNCTION',
    type: 'VISIBILITY',
    itemId: 'annualSurvey',
  },
  {
    severity: 'ERROR' as ErrorSeverity,
    message: 'UNKNOWN_FUNCTION',
    type: 'VISIBILITY',
    itemId: 'group9',
  },
  {
    severity: 'INFO' as ErrorSeverity,
    message: 'VALUESET_DUPLICATE_KEY',
    type: 'VALUESET',
    itemId: 'usedChannel',
    expression: 'phone',
  }
];

const INITIAL_EDITOR: EditorState = {
  activeFormLanguage: 'en',
  errors: DEMO_ERRORS,
  textEditDialogType: undefined,
  ruleEditDialogType: undefined,
  validationRuleEditDialogOpen: true
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
