import React, { useReducer, Dispatch, useCallback, useEffect } from 'react';
import { formReducer } from '../reducer';
import { ComposerAction } from '../actions';
import { ComposerState, ComposerCallbacks } from '../types';

const INITIAL_FORM: ComposerState = {
  _id: '',
  _rev: '',
  name: '',
  data: {},
  metadata: {},
};

export const ComposerContext = React.createContext<{ state: ComposerState, dispatch: Dispatch<ComposerAction>, callbacks?: ComposerCallbacks }>({
  state: INITIAL_FORM,
  dispatch: () => null,
  callbacks: {}
});

export type Middleware = (action: ComposerAction | undefined, state: ComposerState, dispatch: Dispatch<ComposerAction>) => void;

export interface ComposerProviderProps {
  children: React.ReactNode;
  formData: ComposerState;
  //callbacks?: ComposerCallbacks;
  preMiddleware: Middleware[];
  postMiddleware: Middleware[];
}

export const useReducerWithMiddleware = (reducer: React.Reducer<ComposerState, ComposerAction>, initialState: ComposerState, preMiddlewares: Middleware[], postMiddlewares: Middleware[]):
  [ComposerState, React.Dispatch<ComposerAction>] => {
  const [state, dispatch] = useReducer(reducer, initialState);
  const actionRef = React.useRef<ComposerAction>();

  useEffect(() => {
    if (actionRef.current !== undefined) {
      postMiddlewares.forEach(mw => mw(actionRef.current, state, dispatch)); 
    } 
  }, [postMiddlewares, state]);

  const dispatchUsingMiddleware = (action: ComposerAction) => {
    preMiddlewares.forEach(mw => mw(action, state, dispatch));
    actionRef.current = action;
    dispatch(action);
  }

  return [state, dispatchUsingMiddleware];
}

/*
export const ComposerProvider: React.FC<ComposerProviderProps> = ({ children, formData, callbacks }) => {
  const red = useCallback((state: ComposerState, action: ComposerAction) => formReducer(state, action, callbacks), [callbacks]);
  const [state, dispatch] = useReducer(red, formData);

  return (
    <ComposerContext.Provider value={{ state, dispatch, callbacks }}>
      {children}
    </ComposerContext.Provider>
  );

}
*/
export const ComposerProvider: React.FC<ComposerProviderProps> = ({ children, formData, preMiddleware, postMiddleware}) => {
  const [state, dispatch] = useReducerWithMiddleware(formReducer, formData, preMiddleware, postMiddleware);

  return (
    <ComposerContext.Provider value={{ state, dispatch }}>
      {children}
    </ComposerContext.Provider>
  );

}