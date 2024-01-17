import { useContext } from "react"
import { EditorContext } from "./EditorContext";
import { DialobItem } from "../../dialob";

export const useEditor = () => {
  const { state, dispatch } = useContext(EditorContext);

  const setActivePage = (page: DialobItem): void => {
    dispatch({ type: 'setActivePage', page });
  };

  const setActiveFormLanguage = (language: string): void => {
    dispatch({ type: 'setActiveFormLanguage', language });
  };

  return {
    editor: state,
    setActivePage,
    setActiveFormLanguage
  };
}
