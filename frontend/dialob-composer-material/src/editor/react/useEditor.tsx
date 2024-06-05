import { useContext } from "react"
import { EditorContext } from "./EditorContext";
import { DialobItem } from "../../dialob";
import { ConfirmationDialogType, EditorError, OptionsTabType, VariableTabType } from "../types";

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

  const setActiveItem = (item?: DialobItem): void => {
    dispatch({ type: 'setActiveItem', item });
  }

  const setConfirmationDialogType = (dialogType?: ConfirmationDialogType): void => {
    dispatch({ type: 'setConfirmationDialogType', dialogType });
  }

  const setItemOptionsActiveTab = (tab?: OptionsTabType): void => {
    dispatch({ type: 'setItemOptionsActiveTab', tab });
  }

  const setHighlightedItem = (item?: DialobItem): void => {
    dispatch({ type: 'setHighlightedItem', item });
  }

  const setActiveList = (listId?: string): void => {
    dispatch({ type: 'setActiveList', listId });
  }

  const setActiveVariableTab = (tab?: VariableTabType): void => {
    dispatch({ type: 'setActiveVariableTab', tab });
  }

  const addExpandedChoiceItem = (itemId: string): void => {
    dispatch({ type: 'addExpandedChoiceItem', itemId });
  }

  const removeExpandedChoiceItem = (itemId: string): void => {
    dispatch({ type: 'removeExpandedChoiceItem', itemId });
  }

  return {
    editor: state,
    setActivePage,
    setActiveFormLanguage,
    setErrors,
    clearErrors,
    setActiveItem,
    setConfirmationDialogType,
    setItemOptionsActiveTab,
    setHighlightedItem,
    setActiveList,
    setActiveVariableTab,
    addExpandedChoiceItem,
    removeExpandedChoiceItem,
  };
}
