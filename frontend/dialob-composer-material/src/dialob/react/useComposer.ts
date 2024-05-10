import { useContext } from "react"
import { ComposerContext } from './ComposerContext';
import { DialobItemTemplate, ValueSetEntry, ContextVariableType, ValidationRule, LocalizedString, ContextVariable, Variable } from "../types";

export const useComposer = () => {
  const { state, dispatch } = useContext(ComposerContext);

  const addItem = (itemTemplate: DialobItemTemplate, parentItemId: string, afterItemId?: string): void => {
    dispatch({ type: 'addItem', config: itemTemplate, parentItemId, afterItemId });
  };

  const updateItem = (itemId: string, attribute: string, value: string, language?: string) => {
    dispatch({ type: 'updateItem', itemId, attribute, value, language });
  };

  const updateLocalizedString = (itemId: string, attribute: string, value: LocalizedString, index?: number) => {
    dispatch({ type: 'updateLocalizedString', itemId, attribute, value, index });
  }

  const changeItemType = (itemId: string, config: DialobItemTemplate) => {
    dispatch({ type: 'changeItemType', itemId, config });
  };

  const deleteItem = (itemId: string) => {
    dispatch({ type: 'deleteItem', itemId });
  };

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const setItemProp = (itemId: string, key: string, value: any) => {
    dispatch({ type: 'setItemProp', itemId, key, value });
  }

  const deleteItemProp = (itemId: string, key: string) => {
    dispatch({ type: 'deleteItemProp', itemId, key });
  }

  const moveItem = (itemId: string, fromIndex: number, toIndex: number, fromParent: string, toParent: string) => {
    dispatch({ type: 'moveItem', itemId, fromIndex, toIndex, fromParent, toParent });
  }

  const createValidation = (itemId: string, rule?: ValidationRule) => {
    dispatch({ type: 'createValidation', itemId, rule });
  }

  const setValidationMessage = (itemId: string, index: number, language: string, message: string) => {
    dispatch({ type: 'setValidationMessage', itemId, index, language, message });
  }

  const setValidationExpression = (itemId: string, index: number, expression: string) => {
    dispatch({ type: 'setValidationExpression', itemId, index, expression });
  }

  const deleteValidation = (itemId: string, index: number) => {
    dispatch({ type: 'deleteValidation', itemId, index });
  }

  const createValueSet = (itemId: string | null, entries?: ValueSetEntry[]) => {
    dispatch({ type: 'createValueSet', itemId, entries });
  };

  const setValueSetEntries = (valueSetId: string, entries: ValueSetEntry[]) => {
    dispatch({ type: 'setValueSetEntries', valueSetId, entries });
  }

  const addValueSetEntry = (valueSetId: string, entry?: ValueSetEntry) => {
    dispatch({ type: 'addValueSetEntry', valueSetId, entry });
  }

  const updateValueSetEntry = (valueSetId: string, index: number, entry: ValueSetEntry) => {
    dispatch({ type: 'updateValueSetEntry', valueSetId, index, entry });
  }

  const updateValueSetEntryLabel = (valueSetId: string, index: number, text: string | null, language: string) => {
    dispatch({ type: 'updateValueSetEntryLabel', valueSetId, index, text, language });
  }

  const deleteValueSetEntry = (valueSetId: string, index: number) => {
    dispatch({ type: 'deleteValueSetentry', valueSetId, index });
  }

  const moveValueSetEntry = (valueSetId: string, from: number, to: number) => {
    dispatch({ type: 'moveValueSetEntry', valueSetId, from, to });
  }

  const setGlobalValueSetName = (valueSetId: string, name: string) => {
    dispatch({ type: 'setGlobalValueSetName', valueSetId, name });
  }

  const deleteGlobalValueSet = (valueSetId: string) => {
    dispatch({ type: 'deleteGlobalValueSet', valueSetId });
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const setMetadataValue = (attr: string, value: any) => {
    dispatch({ type: 'setMetadataValue', attr, value });
  }

  const setContextValue = (name: string, value: string) => {
    dispatch({ type: 'setContextValue', name, value });
  }

  const createVariable = (context: boolean) => {
    dispatch({ type: 'createVariable', context });
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const updateContextVariable = (variableId: string, contextType: ContextVariableType | string, defaultValue?: any) => {
    dispatch({ type: 'updateContextVariable', variableId, defaultValue, contextType });
  }

  const updateExpressionVariable = (variableId: string, expression: string) => {
    dispatch({ type: 'updateExpressionVariable', variableId, expression });
  }

  const updateVariablePublishing = (variableId: string, published: boolean) => {
    dispatch({ type: 'updateVariablePublishing', variableId, published });
  }

  const deleteVariable = (variableId: string) => {
    dispatch({ type: 'deleteVariable', variableId });
  }

  const moveVariable = (origin: ContextVariable | Variable, destination: ContextVariable | Variable) => {
    dispatch({ type: 'moveVariable', origin, destination });
  }

  const addLanguage = (language: string, copyFrom?: string) => {
    dispatch({ type: 'addLanguage', language, copyFrom });
  }

  const deleteLanguage = (language: string) => {
    dispatch({ type: 'deleteLanguage', language });
  }

  const loadVersion = (tagName: string) => {
    dispatch({ type: 'loadVersion', tagName });
  }

  return {
    addItem,
    updateItem,
    updateLocalizedString,
    changeItemType,
    deleteItem,
    setItemProp,
    deleteItemProp,
    moveItem,
    createValidation,
    setValidationMessage,
    setValidationExpression,
    deleteValidation,
    createValueSet,
    setValueSetEntries,
    addValueSetEntry,
    updateValueSetEntry,
    updateValueSetEntryLabel,
    deleteValueSetEntry,
    moveValueSetEntry,
    setGlobalValueSetName,
    deleteGlobalValueSet,
    setMetadataValue,
    setContextValue,
    createVariable,
    updateContextVariable,
    updateExpressionVariable,
    updateVariablePublishing,
    deleteVariable,
    moveVariable,
    addLanguage,
    deleteLanguage,
    loadVersion,
    form: state
  };

}
