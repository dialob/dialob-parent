import React, { PropsWithChildren, useEffect, useReducer } from "react";
import { formReducer } from "../reducer";
import { ComposerContext, ComposerProviderProps, Middleware } from "./ComposerContext";
import { ComposerState } from "../../types";
import { ComposerAction } from "../actions";

const useReducerWithMiddleware = (reducer: React.Reducer<ComposerState, ComposerAction>, initialState: ComposerState, preMiddlewares: Middleware[], postMiddlewares: Middleware[]):
  [ComposerState, React.Dispatch<ComposerAction>] => {
  const [state, dispatch] = useReducer(reducer, initialState);
  const actionRef = React.useRef<ComposerAction>();

  useEffect(() => {
    if (actionRef.current !== undefined) {
      postMiddlewares.forEach(mw => mw(actionRef.current, state, dispatch));
      actionRef.current = undefined;
    }
  }, [postMiddlewares, state]);

  const dispatchUsingMiddleware = (action: ComposerAction) => {
    preMiddlewares.forEach(mw => mw(action, state, dispatch));
    actionRef.current = action;
    dispatch(action);
  }

  return [state, dispatchUsingMiddleware];
}


export const ComposerProvider: React.FC<PropsWithChildren<ComposerProviderProps>> = ({ children, formData, preMiddleware, postMiddleware }) => {
  const [state, dispatch] = useReducerWithMiddleware(formReducer, formData, preMiddleware, postMiddleware);

  return (
    <ComposerContext.Provider value={{ state, dispatch }}>
      {children}
    </ComposerContext.Provider>
  );
}
