import { PropsWithChildren, useReducer, useEffect } from "react";
import { editorReducer } from "../reducer";
import { ComposerProviderProps, EditorContext, INITIAL_EDITOR } from "./EditorContext";
import { useBackend } from "../../backend/useBackend";

export const EditorProvider: React.FC<PropsWithChildren<ComposerProviderProps>> = ({ children }) => {
  const [state, dispatch] = useReducer(editorReducer, INITIAL_EDITOR);
  const { form } = useBackend();

  useEffect(() => {
    const formLanguages = form?.metadata?.languages;
    const defaultActiveLanguage = form?.metadata?.defaultActiveLanguage;
    if (formLanguages && formLanguages.length > 0 && ((defaultActiveLanguage && INITIAL_EDITOR.activeFormLanguage !== defaultActiveLanguage) || !formLanguages.includes(INITIAL_EDITOR.activeFormLanguage))) {
      if (defaultActiveLanguage && formLanguages.includes(defaultActiveLanguage)) {
        dispatch({ type: 'setActiveFormLanguage', language: defaultActiveLanguage });
        return;
      }
      dispatch({ type: 'setActiveFormLanguage', language: formLanguages[0] });
    }
  }, [form?.metadata.languages]);
  
  return (
    <EditorContext.Provider value={{ state, dispatch }}>
      {children}
    </EditorContext.Provider>
  );
}
