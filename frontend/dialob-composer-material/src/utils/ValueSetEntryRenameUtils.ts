import {
  ContextVariable,
  DialobItem,
  DialobItems,
  ValidationRule,
  ValueSet,
  Variable,
} from '../types';
import { SavingState } from '../dialogs/contexts/saving/SavingContext';
import { isContextVariable } from './ItemUtils';

export type ValueSetEntryRename = {
  valueSetId: string;
  from: string;
  to: string;
};

// a new entry reusing one of these would inherit the pending rename on save
export const getPendingRenameSourceIds = (
  renames: ValueSetEntryRename[] | undefined,
  valueSetId: string
): string[] =>
  (renames ?? []).filter(r => r.valueSetId === valueSetId).map(r => r.from);

const LIST_ITEM_TYPES = new Set(['list', 'multichoice', 'survey', 'surveygroup', 'verticalSurveygroup']);

const RULE_FIELDS = ['activeWhen', 'required', 'readOnlyWhen', 'canAddRowWhen', 'canRemoveRowWhen'] as const;

const escapeRegex = (value: string): string => value.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');

export const buildItemsUsingValueSet = (data: DialobItems, valueSetId: string): string[] =>
  Object.entries(data)
    .filter(([, item]) => item.valueSetId === valueSetId && LIST_ITEM_TYPES.has(item.type))
    .map(([id]) => id);

export const applyEntryRenameToRule = (
  rule: string | undefined,
  listItemIds: string[],
  from: string,
  to: string
): string | undefined => {
  if (!rule || listItemIds.length === 0) {
    return rule;
  }

  const fromEsc = escapeRegex(from);
  let result = rule;

  listItemIds.forEach(listItemId => {
    const idEsc = escapeRegex(listItemId);
    result = result.replace(
      new RegExp(`(${idEsc})\\s*=\\s*(['"])${fromEsc}\\2`, 'g'),
      `$1 = $2${to}$2`
    );
    result = result.replace(
      new RegExp(`(['"])${fromEsc}\\1\\s+in\\s+(${idEsc})\\b`, 'g'),
      `$1${to}$1 in $2`
    );
  });

  return result;
};

const applyValidations = (
  validations: ValidationRule[] | undefined,
  listItemIds: string[],
  from: string,
  to: string
): ValidationRule[] | undefined => {
  if (!validations) {
    return validations;
  }
  return validations.map(validation => ({
    ...validation,
    rule: applyEntryRenameToRule(validation.rule, listItemIds, from, to),
  }));
};

export const applyEntryRenameToItem = (
  item: DialobItem,
  listItemIds: string[],
  from: string,
  to: string
): DialobItem => {
  const result: DialobItem = { ...item };

  if (listItemIds.includes(item.id) && result.defaultValue === from) {
    result.defaultValue = to;
  }

  RULE_FIELDS.forEach(field => {
    result[field] = applyEntryRenameToRule(result[field], listItemIds, from, to);
  });

  result.validations = applyValidations(result.validations, listItemIds, from, to);

  return result;
};

const applyEntryRenameToVariable = (
  variable: ContextVariable | Variable,
  listItemIds: string[],
  from: string,
  to: string
): ContextVariable | Variable => {
  if (isContextVariable(variable)) {
    return variable;
  }
  return {
    ...variable,
    expression: applyEntryRenameToRule(variable.expression, listItemIds, from, to),
  };
};

export const applyEntryRenames = (
  data: DialobItems,
  variables: (ContextVariable | Variable)[] | undefined,
  renames: ValueSetEntryRename[]
): { data: DialobItems; variables: (ContextVariable | Variable)[] | undefined } => {
  if (!renames.length) {
    return { data, variables };
  }

  let updatedData = { ...data };
  let updatedVariables = variables ? [...variables] : variables;

  renames.forEach(({ valueSetId, from, to }) => {
    const listItemIds = buildItemsUsingValueSet(updatedData, valueSetId);
    const nextData: DialobItems = {};
    Object.entries(updatedData).forEach(([id, item]) => {
      nextData[id] = applyEntryRenameToItem(item, listItemIds, from, to);
    });
    updatedData = nextData;

    if (updatedVariables) {
      updatedVariables = updatedVariables.map(variable =>
        applyEntryRenameToVariable(variable, listItemIds, from, to)
      );
    }
  });

  return { data: updatedData, variables: updatedVariables };
};

const collectChangedItems = (original: DialobItems, updated: DialobItems): DialobItems | undefined => {
  const changed: DialobItems = {};
  Object.entries(updated).forEach(([id, item]) => {
    if (JSON.stringify(item) !== JSON.stringify(original[id])) {
      changed[id] = item;
    }
  });
  return Object.keys(changed).length > 0 ? changed : undefined;
};

export const applyEntryRenamesToSavingState = (
  savingState: SavingState,
  formData: DialobItems,
  formVariables: (ContextVariable | Variable)[] | undefined,
  formValueSets: ValueSet[] | undefined
): SavingState => {
  const renames = savingState.pendingEntryRenames ?? [];
  if (!renames.length) {
    return savingState;
  }

  const mergedData: DialobItems = {
    ...formData,
    ...(savingState.items ?? {}),
    ...(savingState.item ? { [savingState.item.id]: savingState.item } : {}),
  };

  const mergedVariables = savingState.variables ?? formVariables;

  const { data: propagatedData, variables: propagatedVariables } = applyEntryRenames(
    mergedData,
    mergedVariables,
    renames
  );

  const changedItems = collectChangedItems(formData, propagatedData);
  const variablesChanged = JSON.stringify(propagatedVariables) !== JSON.stringify(formVariables);

  return {
    ...savingState,
    item: savingState.item ? propagatedData[savingState.item.id] : savingState.item,
    items: changedItems ?? savingState.items,
    variables: variablesChanged ? propagatedVariables : savingState.variables,
    valueSets: savingState.valueSets ?? formValueSets,
    pendingEntryRenames: [],
  };
};
