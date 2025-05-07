import { PropsWithChildren, useReducer } from "react";
import { editorReducer } from "../reducer";
import { ComposerProviderProps, EditorContext, INITIAL_EDITOR } from "./EditorContext";

export const EditorProvider: React.FC<PropsWithChildren<ComposerProviderProps>> = ({ children }) => {
  const [state, dispatch] = useReducer(editorReducer, INITIAL_EDITOR);

  return (
    <EditorContext.Provider value={{ state, dispatch }}>
      {children}
    </EditorContext.Provider>
  );
}
