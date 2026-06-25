import {
  ComposerMetadata,
  ContextVariable,
  DialobItem,
  DialobItems,
  LocalizedString,
  ValidationRule,
  ValueSet,
  ValueSetEntry,
  Variable,
} from '../types';
import { isContextVariable } from './ItemUtils';

export const applyRenamesToRule = (expression: string, from: string, to: string): string =>
  expression.replace(
    new RegExp(`\\b${from.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')}\\b`, 'g'),
    to
  );

export const mergeRuleField = (
  staged: string | undefined,
  baseline: string | undefined,
  api: string | undefined,
  from: string,
  to: string
): string | undefined => {
  const normalizedStaged = staged ?? '';
  const normalizedBaseline = baseline ?? '';
  if (normalizedStaged !== normalizedBaseline) {
    return normalizedStaged === '' ? undefined : applyRenamesToRule(normalizedStaged, from, to);
  }
  return api === '' ? undefined : api;
};

export const rewriteLabelPlaceholders = (
  label: LocalizedString | undefined,
  oldId: string,
  newId: string
): LocalizedString | undefined => {
  if (!label) {
    return label;
  }
  const result: LocalizedString = {};
  Object.entries(label).forEach(([lang, text]) => {
    if (text !== undefined) {
      result[lang] = text.replace(new RegExp(`\\{${oldId}\\}`, 'g'), `{${newId}}`);
    }
  });
  return result;
};

const mergeLocalizedStringField = (
  staged: LocalizedString | undefined,
  baseline: LocalizedString | undefined,
  oldId: string,
  newId: string
): LocalizedString | undefined => {
  if (JSON.stringify(staged ?? {}) !== JSON.stringify(baseline ?? {})) {
    return rewriteLabelPlaceholders(staged, oldId, newId);
  }
  return staged;
};

const mergeValidations = (
  staged: ValidationRule[] | undefined,
  baseline: ValidationRule[] | undefined,
  api: ValidationRule[] | undefined,
  oldId: string,
  newId: string
): ValidationRule[] | undefined => {
  if (!staged && !api) {
    return undefined;
  }
  const baselineRules = baseline ?? [];
  const apiRules = api ?? [];
  return (staged ?? apiRules).map((stagedRule, index) => {
    const baselineRule = baselineRules[index];
    const apiRule = apiRules[index];
    const mergedRule = mergeRuleField(stagedRule.rule, baselineRule?.rule, apiRule?.rule, oldId, newId);
    const messageEdited = JSON.stringify(stagedRule.message ?? {}) !== JSON.stringify(baselineRule?.message ?? {});
    return {
      ...stagedRule,
      rule: mergedRule,
      message: messageEdited
        ? rewriteLabelPlaceholders(stagedRule.message, oldId, newId)
        : stagedRule.message,
    };
  });
};

export const mergeItemAfterRename = (
  stagedItem: DialobItem | undefined,
  baselineItem: DialobItem | undefined,
  apiItem: DialobItem | undefined,
  oldId: string,
  newId: string
): DialobItem | undefined => {
  if (!stagedItem) {
    return apiItem ? { ...apiItem, id: apiItem.id === oldId ? newId : apiItem.id } : undefined;
  }

  const isRenamedItem = stagedItem.id === oldId;
  const merged: DialobItem = { ...stagedItem, id: isRenamedItem ? newId : stagedItem.id };

  const ruleFields = ['activeWhen', 'required', 'readOnlyWhen', 'canAddRowWhen', 'canRemoveRowWhen'] as const;
  ruleFields.forEach(field => {
    merged[field] = mergeRuleField(
      stagedItem[field],
      baselineItem?.[field],
      apiItem?.[field],
      oldId,
      newId
    );
  });

  merged.label = mergeLocalizedStringField(stagedItem.label, baselineItem?.label, oldId, newId);
  merged.description = mergeLocalizedStringField(stagedItem.description, baselineItem?.description, oldId, newId);
  merged.validations = mergeValidations(stagedItem.validations, baselineItem?.validations, apiItem?.validations, oldId, newId);

  if (stagedItem.items) {
    merged.items = stagedItem.items.map(childId => (childId === oldId ? newId : childId));
  }

  return merged;
};

const findApiEntry = (apiVS: ValueSet | undefined, baselineEntry: ValueSetEntry | undefined, stagedEntry: ValueSetEntry): ValueSetEntry | undefined =>
  apiVS?.entries?.find(e => e.id === (baselineEntry?.id ?? stagedEntry.id));

export const mergeValueSetsAfterRename = (
  stagedValueSets: ValueSet[] | undefined,
  baselineValueSets: ValueSet[] | undefined,
  apiValueSets: ValueSet[] | undefined,
  oldId: string,
  newId: string
): ValueSet[] | undefined => {
  if (!stagedValueSets) {
    return apiValueSets;
  }

  return stagedValueSets.map(stagedVS => {
    const baselineVS = baselineValueSets?.find(vs => vs.id === stagedVS.id);
    const apiVS = apiValueSets?.find(vs => vs.id === stagedVS.id);

    if (!stagedVS.entries) {
      return stagedVS;
    }

    const mergedEntries = stagedVS.entries.map((stagedEntry, index) => {
      const baselineEntry = baselineVS?.entries?.[index]?.id === stagedEntry.id
        ? baselineVS.entries[index]
        : baselineVS?.entries?.find(e => e.id === stagedEntry.id);
      const apiEntry = findApiEntry(apiVS, baselineEntry, stagedEntry);
      const mergedWhen = mergeRuleField(stagedEntry.when, baselineEntry?.when, apiEntry?.when, oldId, newId);
      const labelEdited = JSON.stringify(stagedEntry.label ?? {}) !== JSON.stringify(baselineEntry?.label ?? {});

      return {
        ...stagedEntry,
        when: mergedWhen,
        label: labelEdited
          ? (rewriteLabelPlaceholders(stagedEntry.label, oldId, newId) ?? stagedEntry.label ?? {})
          : stagedEntry.label,
      };
    });

    return { ...stagedVS, entries: mergedEntries };
  });
};

export const mergeAllItemsAfterRename = (
  stagedItems: DialobItems | undefined,
  baselineItems: DialobItems | undefined,
  apiItems: DialobItems | undefined,
  oldId: string,
  newId: string
): DialobItems | undefined => {
  if (!stagedItems) {
    return apiItems;
  }

  const result: DialobItems = {};
  Object.entries(stagedItems).forEach(([itemId, stagedItem]) => {
    const baselineItem = baselineItems?.[itemId];
    const apiItem = apiItems?.[itemId === oldId ? newId : itemId] ?? apiItems?.[itemId];
    const merged = mergeItemAfterRename(stagedItem, baselineItem, apiItem, oldId, newId);
    if (merged) {
      const resultKey = itemId === oldId ? newId : itemId;
      result[resultKey] = merged;
    }
  });
  return result;
};

export const mergeVariablesAfterRename = (
  stagedVariables: (Variable | ContextVariable)[] | undefined,
  baselineVariables: (Variable | ContextVariable)[] | undefined,
  apiVariables: (Variable | ContextVariable)[] | undefined,
  oldId: string,
  newId: string
): (Variable | ContextVariable)[] | undefined => {
  if (!stagedVariables) {
    return apiVariables;
  }

  return (apiVariables ?? []).map(apiVar => {
    const stagedVar = stagedVariables.find(v => v.name === apiVar.name)
      ?? (apiVar.name === newId ? stagedVariables.find(v => v.name === oldId) : undefined);
    if (!stagedVar) {
      return apiVar;
    }

    const baselineVar = baselineVariables?.find(v => v.name === stagedVar.name)
      ?? baselineVariables?.find(v => v.name === oldId);

    if (isContextVariable(apiVar)) {
      return { ...stagedVar, name: apiVar.name };
    }

    const apiExpressionVar = apiVar as Variable;
    const stagedExpressionVar = stagedVar as Variable;
    const baselineExpressionVar = baselineVar && !isContextVariable(baselineVar) ? baselineVar as Variable : undefined;
    const mergedExpression = mergeRuleField(
      stagedExpressionVar.expression,
      baselineExpressionVar?.expression,
      apiExpressionVar.expression,
      oldId,
      newId
    );

    return {
      ...stagedExpressionVar,
      name: apiVar.name,
      expression: mergedExpression,
    };
  });
};

export const rewriteAiTranslationEntryIds = (
  metadata: ComposerMetadata | undefined,
  oldId: string,
  newId: string
): ComposerMetadata | undefined => {
  if (!metadata?.aiTranslations?.length) {
    return metadata;
  }
  const prefix = `i:${oldId}:`;
  const newPrefix = `i:${newId}:`;
  return {
    ...metadata,
    aiTranslations: metadata.aiTranslations.map(t => ({
      ...t,
      entryId: t.entryId.startsWith(prefix) ? newPrefix + t.entryId.slice(prefix.length) : t.entryId,
    })),
  };
};
