import React, { Dispatch } from 'react';
import { ComposerAction } from '../actions';
import { ComposerState, ComposerCallbacks, INIT_STATE } from '../../types';

export const ComposerContext = React.createContext<{ state: ComposerState, dispatch: Dispatch<ComposerAction>, callbacks?: ComposerCallbacks }>({
  state: INIT_STATE,
  dispatch: () => null,
  callbacks: {}
});

export type Middleware = (action: ComposerAction | undefined, state: ComposerState, dispatch: Dispatch<ComposerAction>) => void;

export interface ComposerProviderProps {
  formData: ComposerState;
  preMiddleware: Middleware[];
  postMiddleware: Middleware[];
}

