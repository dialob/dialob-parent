import React, { Dispatch } from 'react';
import { EditorAction } from '../actions';
import { EditorState } from '../types';


export const INITIAL_EDITOR: EditorState = {
  activeFormLanguage: 'en',
  errors: [],
  collapsedItems: {},
};

export const EditorContext = React.createContext<{ state: EditorState, dispatch: Dispatch<EditorAction> }>({
  state: INITIAL_EDITOR,
  dispatch: () => null
});

export type ComposerProviderProps = object
