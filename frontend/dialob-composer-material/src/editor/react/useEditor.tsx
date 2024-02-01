import { useContext } from "react"
import { EditorContext } from "./EditorContext";
import { DialobItem } from "../../dialob";
import { ConfirmationDialogType, EditorError } from "..";

export const useEditor = () => {
  const { state, dispatch } = useContext(EditorContext);

  const setActivePage = (page: DialobItem): void => {
    dispatch({ type: 'setActivePage', page });
  };

  const setActiveFormLanguage = (language: string): void => {
    dispatch({ type: 'setActiveFormLanguage', language });
  };

  const setErrors = (errors: EditorError[]): void => {
    dispatch({ type: 'setErrors', errors });
  }

  const clearErrors = (): void => {
    dispatch({ type: 'clearErrors' });
  }

  const setConfirmationDialogType = (dialogType?: ConfirmationDialogType): void => {
    dispatch({ type: 'setConfirmationDialogType', dialogType });
  }

  const setActiveItem = (item?: DialobItem): void => {
    dispatch({ type: 'setActiveItem', item });
  }

  return {
    editor: state,
    setActivePage,
    setActiveFormLanguage,
    setErrors,
    clearErrors,
    setConfirmationDialogType,
    setActiveItem,
  };
}
