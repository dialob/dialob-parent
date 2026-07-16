import { useContext } from 'react';
import { SavingContext } from './SavingContext';
import { ComposerMetadata, ContextVariable, ContextVariableType, DialobItem, DialobItems, DialobItemTemplate, LocalizedString, ValidationRule, ValueSet, ValueSetEntry, Variable } from '../../../types';
import { TranslationResult } from '../../../backend/types';

export const useSave = () => {
  const { state, dispatch } = useContext(SavingContext);

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const updateItem = (itemId: string, attribute: string, value: any, language?: string) => {
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

  const setValidationExpression = (itemId: string, index: number, expression: string | undefined) => {
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

  const updateValueSetEntryLabel = (valueSetId: string, index: number, text: string | null | undefined, language: string) => {
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

  const createVariable = (context: boolean) => {
    dispatch({ type: 'createVariable', context });
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const updateContextVariable = (variableId: string, contextType?: ContextVariableType | string, defaultValue?: any) => {
    dispatch({ type: 'updateContextVariable', variableId, contextType, defaultValue });
  }

  const updateExpressionVariable = (variableId: string, expression: string | undefined) => {
    dispatch({ type: 'updateExpressionVariable', variableId, expression });
  }

  const updateVariablePublishing = (variableId: string, published: boolean) => {
    dispatch({ type: 'updateVariablePublishing', variableId, published });
  }

  const updateVariableDescription = (variableId: string, description: string | undefined) => {
    dispatch({ type: 'updateVariableDescription', variableId, description });
  }

  const deleteVariable = (variableId: string) => {
    dispatch({ type: 'deleteVariable', variableId });
  }

  const moveVariable = (origin: ContextVariable | Variable, destination: ContextVariable | Variable) => {
    dispatch({ type: 'moveVariable', origin, destination });
  }

  const changeVariableId = (variables: (ContextVariable | Variable)[]) => {
    dispatch({ type: 'changeVariableId', variables });
  }

  const updateVariableName = (currentName: string, originalName: string, to: string) => {
    dispatch({ type: 'updateVariableName', currentName, originalName, to });
  }

  const clearPendingRenames = () => {
    dispatch({ type: 'clearPendingRenames' });
  }

  const recordEntryRename = (valueSetId: string, from: string, to: string) => {
    dispatch({ type: 'recordEntryRename', valueSetId, from, to });
  }

  const clearPendingEntryRenames = () => {
    dispatch({ type: 'clearPendingEntryRenames' });
  }

  const syncAfterSave = (saved: {
    item?: DialobItem;
    valueSets?: ValueSet[];
    composerMetadata?: ComposerMetadata;
    variables?: (ContextVariable | Variable)[];
  }) => {
    dispatch({ type: 'syncAfterSave', ...saved });
  }

  const resetItems = (items: DialobItems) => {
    dispatch({ type: 'resetItems', items });
  }

  const resetVariables = (variables: (ContextVariable | Variable)[]) => {
    dispatch({ type: 'resetVariables', variables });
  }

  const applyIdRenameMerge = (merge: {
    mergedItem?: DialobItem;
    mergedValueSets?: ValueSet[];
    mergedItems?: DialobItems;
    mergedVariables?: (ContextVariable | Variable)[];
    mergedComposerMetadata?: ComposerMetadata;
  }) => {
    dispatch({ type: 'applyIdRenameMerge', ...merge });
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const setMetadataValue = (attr: string, value: any) => {
    dispatch({ type: 'setMetadataValue', attr, value });
  }

  const applyTranslations = (translations: TranslationResult[], sourceLanguage: string, targetLanguage: string) => {
    dispatch({ type: 'applyTranslations', translations, sourceLanguage, targetLanguage });
  }

  const addAITranslation = (entryId: string, sourceLanguage: string, targetLanguage: string) => {
    dispatch({ type: 'addAITranslation', entryId, sourceLanguage, targetLanguage });
  }

  const removeAITranslation = (entryId: string, targetLanguage: string) => {
    dispatch({ type: 'removeAITranslation', entryId, targetLanguage });
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
    createVariable,
    updateContextVariable,
    updateExpressionVariable,
    updateVariablePublishing,
    updateVariableDescription,
    deleteVariable,
    moveVariable,
    changeVariableId,
    updateVariableName,
    clearPendingRenames,
    recordEntryRename,
    clearPendingEntryRenames,
    syncAfterSave,
    resetItems,
    resetVariables,
    applyIdRenameMerge,
    setMetadataValue,
    applyTranslations,
    addAITranslation,
    removeAITranslation,
    savingState: state
  }
}