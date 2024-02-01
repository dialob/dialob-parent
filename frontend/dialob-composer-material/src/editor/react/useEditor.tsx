import { useContext } from "react"
import { EditorContext } from "./EditorContext";
import { DialobItem } from "../../dialob";
import { RuleEditDialogType, TextEditDialogType } from "..";

export const useEditor = () => {
  const { state, dispatch } = useContext(EditorContext);

  const setActivePage = (page: DialobItem): void => {
    dispatch({ type: 'setActivePage', page });
  };

  const setActiveFormLanguage = (language: string): void => {
    dispatch({ type: 'setActiveFormLanguage', language });
  };

  const setTextEditDialogType = (dialogType?: TextEditDialogType): void => {
    dispatch({ type: 'setTextEditDialogType', dialogType });
  };

  const setRuleEditDialogType = (dialogType?: RuleEditDialogType): void => {
    dispatch({ type: 'setRuleEditDialogType', dialogType });
  }

  const setValidationRuleEditDialogOpen = (open: boolean): void => {
    dispatch({ type: 'setValidationRuleEditDialogOpen', open });
  }

  return {
    editor: state,
    setActivePage,
    setActiveFormLanguage,
    setTextEditDialogType,
    setRuleEditDialogType,
    setValidationRuleEditDialogOpen,
  };
}
