import { useContext } from "react"
import { EditorContext } from "./EditorContext";
import { DialobItem } from "../../dialob";
import { ConfirmationDialogType, TextEditDialogType, RuleEditDialogType, EditorError } from "../types";

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

  const setTextEditDialogType = (dialogType?: TextEditDialogType): void => {
    dispatch({ type: 'setTextEditDialogType', dialogType });
  };

  const setRuleEditDialogType = (dialogType?: RuleEditDialogType): void => {
    dispatch({ type: 'setRuleEditDialogType', dialogType });
  }

  const setValidationRuleEditDialogOpen = (open: boolean): void => {
    dispatch({ type: 'setValidationRuleEditDialogOpen', open });
  }

  const setItemOptionsDialogOpen = (open: boolean): void => {
    dispatch({ type: 'setItemOptionsDialogOpen', open });
  }

  const setHighlightedItem = (item?: DialobItem): void => {
    dispatch({ type: 'setHighlightedItem', item });
  }

  return {
    editor: state,
    setActivePage,
    setActiveFormLanguage,
    setErrors,
    clearErrors,
    setActiveItem,
    setConfirmationDialogType,
    setTextEditDialogType,
    setRuleEditDialogType,
    setValidationRuleEditDialogOpen,
    setItemOptionsDialogOpen,
    setHighlightedItem,
  };
}
