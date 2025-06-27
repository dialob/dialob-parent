import { useContext } from 'react';
import { SavingContext } from './SavingContext';
import { DialobItemTemplate, LocalizedString, ValidationRule, ValueSetEntry } from '../../../types';

export const useSave = () => {
  const { state, dispatch } = useContext(SavingContext);

  const updateItem = (itemId: string, attribute: string, value: string, language?: string) => {
    dispatch({ type: 'updateItem', itemId, attribute, value, language });
  };

  const updateLocalizedString = (itemId: string, attribute: string, value: LocalizedString, index?: number) => {
    dispatch({ type: 'updateLocalizedString', itemId, attribute, value, index });
  }

  const changeItemType = (itemId: string, config: DialobItemTemplate) => {
    dispatch({ type: 'changeItemType', itemId, config });
  };

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const setItemProp = (itemId: string, key: string, value: any) => {
    dispatch({ type: 'setItemProp', itemId, key, value });
  }

  const deleteItemProp = (itemId: string, key: string) => {
    dispatch({ type: 'deleteItemProp', itemId, key });
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

  const deleteLocalValueSet = (valueSetId: string) => {
    dispatch({ type: 'deleteLocalValueSet', valueSetId });
  }

  const deleteGlobalValueSet = (valueSetId: string) => {
    dispatch({ type: 'deleteGlobalValueSet', valueSetId });
  }

  return {
    updateItem,
    updateLocalizedString,
    changeItemType,
    setItemProp,
    deleteItemProp,
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
    deleteLocalValueSet,
    deleteGlobalValueSet,
    savingState: state
  }
}