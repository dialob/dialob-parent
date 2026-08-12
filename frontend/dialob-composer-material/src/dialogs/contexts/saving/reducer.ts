import { produce } from "immer";
import { ComposerMetadata, ContextVariable, ContextVariableType, DialobItem, DialobItems, DialobItemTemplate, LocalizedString, TranslationMetadata, ValidationRule, ValueSet, ValueSetEntry, Variable } from "../../../types";
import { cleanLocalizedString, cleanString } from "../../../utils/StringUtils";
import { SavingAction } from "./SavingAction";
import { SavingState } from "./SavingContext";
import { isContextVariable } from "../../../utils/ItemUtils";
import { shiftValueSetTranslationIndices, syncValueSetTranslationIndices } from "../../../utils/ValueSetUtils";
import { reorderVariableSubset } from "../../../utils/VariableUtils";
import { TranslationResult } from "../../../backend/types";


export const generateItemIdWithPrefix = (state: SavingState, prefix: string): string => {
  const idList = state.variables?.map(v => v.name) || [];
  const matcher = `^(${prefix})(\\d*)$`;
  const existing = idList.filter(id => {
    const r = RegExp(matcher);
    return r.test(id);
  });
  let idx = 1;
  while (existing.findIndex(v => v === `${prefix}${idx}`) > -1) {
    idx++;
  }
  return `${prefix}${idx}`;
}

export const generateValueSetId = (state: SavingState, prefix = 'vs'): string => {
  let idx = 1;
  while (state.valueSets && state.valueSets.findIndex(vs => vs.id === `${prefix}${idx}`) > -1) {
    idx++;
  }
  return `${prefix}${idx}`;
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const updateItem = (state: SavingState, attribute: string, value: any, language?: string): void => {
  if (state.item === undefined || attribute === 'id' || attribute === 'type') {
    return;
  }

  if (language) {
    const cleanedValue = value ? cleanString(value) : undefined;
    if (state.item[attribute] === undefined) {
      state.item[attribute] = { [language]: cleanedValue };
    } else {
      state.item[attribute][language] = cleanedValue;
    }
  } else {
    if (value === '' || value === undefined) {
      delete state.item[attribute];
    } else {
      state.item[attribute] = value;
    }
  }
}

const updateLocalizedString = (state: SavingState, attribute: string, value: LocalizedString, index?: number): void => {
  if (!state.item) {
    return;
  }
  const item = state.item;
  const cleanedValue = cleanLocalizedString(value);
  if (item && (attribute === 'label' || attribute === 'description')) {
    item[attribute] = cleanedValue;
  } else if (attribute === 'validations' && index !== undefined) {
    const validations = item.validations;
    if (validations) {
      const rule = validations[index];
      if (!rule) {
        return;
      }
      rule.message = cleanedValue;
    }
  } else {
    return;
  }
}

const convertItem = (state: SavingState, itemTemplate: DialobItemTemplate): void => {
  if (!state.item) {
    return;
  }
  const item = state.item;
  item.type = itemTemplate.type;
  item.view = itemTemplate.view;
  if (item.props) {
    Object.assign(item.props, itemTemplate.props);
  } else {
    item.props = itemTemplate.props;
  }
  if (itemTemplate.className) {
    if (Array.isArray(item.className)) {
      item.className = item.className.concat(itemTemplate.className.filter(className => item.className && item.className.indexOf(className) < 0));
    } else {
      item.className = itemTemplate.className;
    }
  }
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const setItemProp = (state: SavingState, key: string, value: any): void => {
  if (state.item === undefined) {
    return;
  }
  if (state.item.props === undefined) {
    state.item.props = { [key]: value };
  } else {
    const props = state.item.props;
    if (props !== undefined) {
      props[key] = value;
    }
  }
}

const deleteItemProp = (state: SavingState, key: string): void => {
  if (state.item === undefined) {
    return;
  }

  const props = state.item.props;
  if (props !== undefined) {
    delete props[key];

    if (Object.keys(props).length === 0) {
      delete state.item.props;
    }
  }
}

const createValidation = (state: SavingState, rule?: ValidationRule): void => {
  if (state.item === undefined) {
    return;
  }

  const cleanedRule: ValidationRule = {
    message: rule?.message ? cleanLocalizedString(rule.message) : {},
    rule: rule?.rule ? rule.rule : ''
  }

  if (state.item.validations === undefined) {
    state.item.validations = [cleanedRule];
  } else {
    state.item.validations?.push(cleanedRule);
  }
}

const setValidationMessage = (state: SavingState, index: number, language: string, message: string): void => {
  if (!state.item) {
    return;
  }
  const validations = state.item.validations;
  if (validations) {
    const rule = validations[index];
    if (!rule) {
      return;
    }
    const cleanedMessage = cleanString(message);
    if (!rule.message) {
      rule.message = { [language]: cleanedMessage };
    } else {
      rule.message[language] = cleanedMessage;
    }
  }
}

const setValidationExpression = (state: SavingState, index: number, expression: string | undefined): void => {
  if (!state.item) {
    return;
  }
  const validations = state.item.validations;
  if (validations) {
    const rule = validations[index];
    if (!rule) {
      return;
    }
    rule.rule = expression;
  }
}

const deleteValidation = (state: SavingState, index: number): void => {
  if (!state.item) {
    return;
  }
  const validations = state.item.validations;
  if (validations) {
    const rule = validations[index];
    if (!rule) {
      return;
    }
    validations.splice(index, 1);

    // Clean up AI translation metadata for deleted validation
    if (state.composerMetadata?.aiTranslations) {
      const itemId = state.item.id;
      const entryIdToDelete = `i:${itemId}:v:${index}`;
      state.composerMetadata.aiTranslations = state.composerMetadata.aiTranslations.filter(
        t => t.entryId !== entryIdToDelete
      );

      // Update indices for validations that come after the deleted one
      state.composerMetadata.aiTranslations.forEach(t => {
        if (t.entryId.startsWith(`i:${itemId}:v:`)) {
          const parts = t.entryId.split(':');
          const validationIndex = parseInt(parts[3], 10);
          if (validationIndex > index) {
            t.entryId = `i:${itemId}:v:${validationIndex - 1}`;
          }
        }
      });
    }
  }
}

const createValueSet = (state: SavingState, itemId: string | null, entries?: ValueSetEntry[]): void => {
  if (itemId && state.item === undefined) {
    return;
  }

  const cleanedEntries: ValueSetEntry[] = entries ? entries.map(e => ({ ...e, label: cleanLocalizedString(e.label) })) : [];

  const valueSetId = generateValueSetId(state, 'vs');
  const valueSet = {
    id: valueSetId,
    entries: cleanedEntries
  };

  if (!state.valueSets) {
    state.valueSets = [valueSet];
  } else {
    state.valueSets.push(valueSet);
  }

  if (itemId && state.item) {
    // Local valueset
    state.item.valueSetId = valueSetId;
  } else {
    // Global valueset
    if (!state.composerMetadata) {
      state.composerMetadata = {};
    }
    if (!state.composerMetadata.globalValueSets) {
      state.composerMetadata.globalValueSets = [];
    }
    state.composerMetadata.globalValueSets.push({ valueSetId, label: '' })
  }
}

const setValueSetEntries = (state: SavingState, valueSetId: string, entries: ValueSetEntry[]): void => {
  if (state.valueSets) {
    const vsIdx = state.valueSets.findIndex(vs => vs.id === valueSetId);
    if (vsIdx > -1) {
      const cleanedEntries: ValueSetEntry[] = entries ? entries.map(e => ({ ...e, label: cleanLocalizedString(e.label) })) : [];
      state.valueSets[vsIdx].entries = cleanedEntries;
    }
  }
}

const addValueSetEntry = (state: SavingState, valueSetId: string, entry?: ValueSetEntry, insertAfterIndex?: number): void => {
  if (state.valueSets) {
    const vsIdx = state.valueSets.findIndex(vs => vs.id === valueSetId);
    if (vsIdx > -1) {
      const cleanedEntry: ValueSetEntry = entry ? { ...entry, label: cleanLocalizedString(entry.label) } : { id: '', label: {} };
      if (state.valueSets[vsIdx].entries !== undefined) {
        if (insertAfterIndex !== undefined && insertAfterIndex >= -1) {
          const insertIndex = insertAfterIndex + 1;
          shiftValueSetTranslationIndices(state.composerMetadata?.aiTranslations, valueSetId, insertIndex, 1);
          state.valueSets[vsIdx].entries!.splice(insertIndex, 0, cleanedEntry);
        } else {
          state.valueSets[vsIdx].entries!.push(cleanedEntry);
        }
      } else {
        state.valueSets[vsIdx].entries = [cleanedEntry];
      }
    }
  }
}

const updateValueSetEntry = (state: SavingState, valueSetId: string, index: number, entry: ValueSetEntry): void => {
  if (state.valueSets) {
    const vsIdx = state.valueSets.findIndex(vs => vs.id === valueSetId);
    if (vsIdx > -1 && state.valueSets[vsIdx].entries !== undefined && state.valueSets[vsIdx].entries![index] !== undefined) {
      state.valueSets[vsIdx].entries![index] = entry;
    }
  }
}

const updateValueSetEntryLabel = (state: SavingState, valueSetId: string, index: number, text: string | null | undefined, language: string): void => {
  if (state.valueSets) {
    const vsIdx = state.valueSets.findIndex(vs => vs.id === valueSetId);
    if (vsIdx > -1 && text !== null && state.valueSets[vsIdx].entries !== undefined && state.valueSets[vsIdx].entries![index] !== undefined && state.valueSets[vsIdx].entries![index].label !== undefined) {
      const cleanedText = text ? cleanString(text) : undefined;
      state.valueSets[vsIdx].entries![index].label[language] = cleanedText;
    }
  }
}

const deleteValueSetEntry = (state: SavingState, valueSetId: string, index: number): void => {
  if (state.valueSets) {
    const vsIdx = state.valueSets.findIndex(vs => vs.id === valueSetId);
    if (vsIdx > -1 && state.valueSets[vsIdx].entries !== undefined) {
      const deletedEntry = state.valueSets[vsIdx].entries![index];
      state.valueSets[vsIdx].entries!.splice(index, 1);

      // Rename whose entry no longer exists must not rewrite form references, or an entry later reusing the old id would inherit the rename
      if (state.pendingEntryRenames && deletedEntry) {
        state.pendingEntryRenames = state.pendingEntryRenames.filter(
          r => !(r.valueSetId === valueSetId && r.to === deletedEntry.id)
        );
      }

      // Clean up AI translation metadata for deleted valueset entry
      if (state.composerMetadata?.aiTranslations && deletedEntry) {
        const entryIdToDelete = `v:${valueSetId}:${index}:${deletedEntry.id}`;
        state.composerMetadata.aiTranslations = state.composerMetadata.aiTranslations.filter(
          t => t.entryId !== entryIdToDelete
        );

        // Update indices for entries that come after the deleted one
        state.composerMetadata.aiTranslations.forEach(t => {
          if (t.entryId.startsWith(`v:${valueSetId}:`)) {
            const parts = t.entryId.split(':');
            const entryIndex = parseInt(parts[2], 10);
            if (entryIndex > index) {
              // Reconstruct entry ID with decremented index
              t.entryId = `v:${valueSetId}:${entryIndex - 1}:${parts[3]}`;
            }
          }
        });
      }
    }
  }
}

const moveValueSetEntry = (state: SavingState, valueSetId: string, from: number, to: number): void => {
  if (state.valueSets) {
    const vsIdx = state.valueSets.findIndex(vs => vs.id === valueSetId);
    if (vsIdx > -1 && state.valueSets[vsIdx].entries !== undefined) {
      const entries = state.valueSets[vsIdx].entries!;
      const newIndex = to > entries.length ? entries.length - 1 : to;
      entries.splice(newIndex, 0, entries.splice(from, 1)[0]);
      syncValueSetTranslationIndices(state.composerMetadata?.aiTranslations, valueSetId, entries);
    }
  }
}

const setGlobalValueSetName = (state: SavingState, valueSetId: string, name: string): void => {
  if (state.composerMetadata?.globalValueSets) {
    const gvsIdx = state.composerMetadata.globalValueSets.findIndex(gvs => gvs.valueSetId === valueSetId);
    if (gvsIdx > -1) {
      state.composerMetadata.globalValueSets[gvsIdx].label = name;
    }
  }
}

const deleteLocalValueSet = (state: SavingState, valueSetId: string): void => {
  if (state.valueSets && state.valueSets?.find(vs => vs.id === valueSetId) !== undefined) {
    state.valueSets = state.valueSets.filter(vs => vs.id !== valueSetId);

    // Clean up AI translation metadata for deleted valueset
    if (state.composerMetadata?.aiTranslations) {
      state.composerMetadata.aiTranslations = state.composerMetadata.aiTranslations.filter(
        t => !t.entryId.startsWith(`v:${valueSetId}:`)
      );
    }
  }
}

const deleteGlobalValueSet = (state: SavingState, valueSetId: string): void => {
  if (state.valueSets && state.valueSets?.find(vs => vs.id === valueSetId) !== undefined && state.composerMetadata?.globalValueSets !== undefined) {
    state.valueSets = state.valueSets.filter(vs => vs.id !== valueSetId);
    state.composerMetadata.globalValueSets = state.composerMetadata.globalValueSets.filter(gvs => gvs.valueSetId !== valueSetId);

    // Clean up AI translation metadata for deleted valueset
    if (state.composerMetadata?.aiTranslations) {
      state.composerMetadata.aiTranslations = state.composerMetadata.aiTranslations.filter(
        t => !t.entryId.startsWith(`v:${valueSetId}:`)
      );
    }
  }
}

const createVariable = (state: SavingState, context: boolean, insertAfterIndex?: number): void => {
  const variableId = generateItemIdWithPrefix(state, context ? 'context' : 'var');

  const variable: ContextVariable | Variable = context ? {
    name: variableId,
    context: true,
    contextType: 'text'
  } : {
    name: variableId,
    expression: ''
  };

  if (!Array.isArray(state.variables)) {
    state.variables = [variable];
  } else if (insertAfterIndex !== undefined && insertAfterIndex >= -1) {
    state.variables.splice(insertAfterIndex + 1, 0, variable);
  } else {
    state.variables.push(variable);
  }
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const updateContextVariable = (state: SavingState, variableId: string, contextType?: ContextVariableType | string, defaultValue?: any): void => {
  if (state.variables) {
    const varIdx = state.variables.findIndex(v => isContextVariable(v) && v.name === variableId);
    if (varIdx > -1) {
      if (contextType !== undefined) {
        (state.variables[varIdx] as ContextVariable).contextType = contextType;
      }
      if (defaultValue !== undefined) {
        (state.variables[varIdx] as ContextVariable).defaultValue = defaultValue;
      }
    }
  }
}

const updateExpressionVariable = (state: SavingState, variableId: string, expression: string | undefined): void => {
  if (state.variables) {
    const varIdx = state.variables.findIndex(v => !isContextVariable(v) && v.name === variableId);
    if (varIdx > -1) {
      (state.variables[varIdx] as Variable).expression = expression;
    }
  }
}

const deleteVariable = (state: SavingState, variableId: string): void => {
  if (state.variables) {
    const varIdx = state.variables.findIndex(v => v.name === variableId);
    if (varIdx > -1) {
      state.variables.splice(varIdx, 1);
    }
  }
}

const updateVariablePublishing = (state: SavingState, variableId: string, published: boolean): void => {
  if (state.variables) {
    const varIdx = state.variables.findIndex(v => v.name === variableId);
    if (varIdx > -1) {
      (state.variables[varIdx]).published = published;
    }
  }
}

const updateVariableDescription = (state: SavingState, variableId: string, description: string | undefined): void => {
  if (state.variables) {
    const varIdx = state.variables.findIndex(v => v.name === variableId);
    if (varIdx > -1) {
      (state.variables[varIdx]).description = description;
    }
  }
}

const moveVariable = (state: SavingState, name: string, toFilteredIndex: number, context: boolean): void => {
  if (state.variables) {
    reorderVariableSubset(state.variables, name, toFilteredIndex, context);
  }
}

const updateVariableName = (state: SavingState, currentName: string, originalName: string, to: string): void => {
  if (!state.variables) return;

  const varIdx = state.variables.findIndex(v => v.name === currentName);
  if (varIdx === -1) return;

  state.variables[varIdx].name = to;

  if (state.items) {
    Object.values(state.items).forEach(item => {
      if (item.type === 'rowgroup' && item.items?.includes(currentName)) {
        const idx = item.items.indexOf(currentName);
        if (idx > -1) {
          item.items[idx] = to;
        }
      }
    });
  }

  if (!state.pendingVariableRenames) {
    state.pendingVariableRenames = [];
  }
  const existingIdx = state.pendingVariableRenames.findIndex(r => r.from === originalName);
  if (existingIdx > -1) {
    if (to === originalName) {
      state.pendingVariableRenames.splice(existingIdx, 1);
    } else {
      state.pendingVariableRenames[existingIdx].to = to;
    }
  } else if (to !== originalName) {
    state.pendingVariableRenames.push({ from: originalName, to });
  }
}

const clearPendingRenames = (state: SavingState): void => {
  state.pendingVariableRenames = [];
}

const recordEntryRename = (state: SavingState, valueSetId: string, from: string, to: string): void => {
  if (from === to) {
    return;
  }
  if (!state.pendingEntryRenames) {
    state.pendingEntryRenames = [];
  }

  const chainIdx = state.pendingEntryRenames.findIndex(r => r.valueSetId === valueSetId && r.to === from);
  if (chainIdx > -1) {
    if (to === state.pendingEntryRenames[chainIdx].from) {
      state.pendingEntryRenames.splice(chainIdx, 1);
    } else {
      state.pendingEntryRenames[chainIdx].to = to;
    }
    return;
  }

  const existingIdx = state.pendingEntryRenames.findIndex(r => r.valueSetId === valueSetId && r.from === from);
  if (existingIdx > -1) {
    if (to === from) {
      state.pendingEntryRenames.splice(existingIdx, 1);
    } else {
      state.pendingEntryRenames[existingIdx].to = to;
    }
  } else {
    state.pendingEntryRenames.push({ valueSetId, from, to });
  }
}

const clearPendingEntryRenames = (state: SavingState): void => {
  state.pendingEntryRenames = [];
}

const syncAfterSave = (
  state: SavingState,
  item?: DialobItem,
  valueSets?: ValueSet[],
  composerMetadata?: ComposerMetadata,
  variables?: (ContextVariable | Variable)[]
): void => {
  if (item !== undefined) {
    state.item = item;
  }
  if (valueSets !== undefined) {
    state.valueSets = valueSets;
  }
  if (composerMetadata !== undefined) {
    state.composerMetadata = composerMetadata;
  }
  if (variables !== undefined) {
    state.variables = variables;
  }
  state.pendingEntryRenames = [];
  state.items = undefined;
}

const resetItems = (state: SavingState, items: DialobItems): void => {
  state.items = items;
}

const resetVariables = (state: SavingState, variables: (ContextVariable | Variable)[]): void => {
  state.variables = variables;
}

const changeVariableId = (state: SavingState, variables: (ContextVariable | Variable)[]): void => {
  if (state.variables && state.items) {
    // Build a map of old variable names to new variable names
    const oldNames = new Set(state.variables.map(v => v.name));
    const newNames = new Set(variables.map(v => v.name));

    // Find names that exist in old but not in new (removed/renamed)
    const removedNames = Array.from(oldNames).filter(name => !newNames.has(name));

    // Find names that exist in new but not in old (added/renamed to)
    const addedNames = Array.from(newNames).filter(name => !oldNames.has(name));

    // If we have exactly one removed and one added, it's a rename
    if (removedNames.length === 1 && addedNames.length === 1) {
      const oldName = removedNames[0];
      const newName = addedNames[0];

      // Update any rowgroup that contains the old name
      Object.values(state.items).forEach(item => {
        if (item.type === 'rowgroup' && item.items?.includes(oldName)) {
          const idx = item.items.indexOf(oldName);
          if (idx > -1) {
            item.items[idx] = newName;
          }
        }
      });
    }
  }

  if (state.variables) {
    state.variables = variables;
  }
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const setMetadataValue = (state: SavingState, attr: string, value: any): void => {
  if (attr === 'tenantId' || attr === 'created' || attr === 'creator' || state.formMetadata === undefined) {
    return;
  }
  state.formMetadata[attr] = value;
}

const applyTranslations = (state: SavingState, translations: TranslationResult[], sourceLanguage: string, targetLanguage: string): void => {
  // Initialize composer metadata if needed
  if (!state.composerMetadata) {
    state.composerMetadata = {};
  }
  if (!state.composerMetadata.aiTranslations) {
    state.composerMetadata.aiTranslations = [];
  }

  const timestamp = new Date().toISOString();

  translations.forEach(translation => {
    const parts = translation.id.split(':');

    if (parts[0] === 'i' && state.item) {
      // Item translations: labels, descriptions, validations
      const itemId = parts[1];
      if (state.item.id !== itemId) return;

      if (parts[2] === 'l') {
        // Label
        state.item.label = {
          ...(state.item.label || {}),
          [targetLanguage]: translation.translatedText
        };
      } else if (parts[2] === 'd') {
        // Description
        state.item.description = {
          ...(state.item.description || {}),
          [targetLanguage]: translation.translatedText
        };
      } else if (parts[2] === 'v') {
        // Validation message
        const validationIndex = parseInt(parts[3], 10);
        if (state.item.validations?.[validationIndex]) {
          state.item.validations[validationIndex].message = {
            ...(state.item.validations[validationIndex].message || {}),
            [targetLanguage]: translation.translatedText
          };
        }
      }
    } else if (parts[0] === 'v' && state.valueSets) {
      // ValueSet translations
      const valueSetId = parts[1];
      const entryIndex = parseInt(parts[2], 10);
      const valueSet = state.valueSets.find(vs => vs.id === valueSetId);

      if (valueSet?.entries?.[entryIndex]) {
        valueSet.entries[entryIndex].label = {
          ...(valueSet.entries[entryIndex].label || {}),
          [targetLanguage]: translation.translatedText
        };
      }
    }

    // Add AI metadata
    const metadata: TranslationMetadata = {
      entryId: translation.id,
      sourceLanguage,
      targetLanguage,
      timestamp
    };
    state.composerMetadata!.aiTranslations!.push(metadata);
  });
}

const addAITranslation = (state: SavingState, entryId: string, sourceLanguage: string, targetLanguage: string): void => {
  if (!state.composerMetadata) {
    state.composerMetadata = {};
  }
  if (!state.composerMetadata.aiTranslations) {
    state.composerMetadata.aiTranslations = [];
  }

  // Remove existing translation for this entry+target language if any
  state.composerMetadata.aiTranslations = state.composerMetadata.aiTranslations.filter(
    t => !(t.entryId === entryId && t.targetLanguage === targetLanguage)
  );

  // Add new translation metadata
  state.composerMetadata.aiTranslations.push({
    entryId,
    sourceLanguage,
    targetLanguage,
    timestamp: new Date().toISOString()
  });
}

const removeAITranslation = (state: SavingState, entryId: string, targetLanguage: string): void => {
  if (!state.composerMetadata?.aiTranslations) {
    return;
  }

  state.composerMetadata.aiTranslations = state.composerMetadata.aiTranslations.filter(
    t => !(t.entryId === entryId && t.targetLanguage === targetLanguage)
  );
}

const applyIdRenameMerge = (
  state: SavingState,
  mergedItem?: DialobItem,
  mergedValueSets?: ValueSet[],
  mergedItems?: DialobItems,
  mergedVariables?: (ContextVariable | Variable)[],
  mergedComposerMetadata?: ComposerMetadata
): void => {
  if (mergedItem !== undefined) {
    state.item = mergedItem;
  }
  if (mergedValueSets !== undefined) {
    state.valueSets = mergedValueSets;
  }
  if (mergedItems !== undefined) {
    state.items = mergedItems;
  }
  if (mergedVariables !== undefined) {
    state.variables = mergedVariables;
  }
  if (mergedComposerMetadata !== undefined) {
    state.composerMetadata = mergedComposerMetadata;
  }
}


export const itemReducer = (state: SavingState, action: SavingAction): SavingState => {

  const newState = produce(state, state => {
    if (action.type === 'updateItem') {
      updateItem(state, action.attribute, action.value, action.language);
    } else if (action.type === 'updateLocalizedString') {
      updateLocalizedString(state, action.attribute, action.value, action.index);
    } else if (action.type === 'changeItemType') {
      convertItem(state, action.config);
    } else if (action.type === 'setItemProp') {
      setItemProp(state, action.key, action.value);
    } else if (action.type === 'deleteItemProp') {
      deleteItemProp(state, action.key);
    } else if (action.type === 'createValidation') {
      createValidation(state, action.rule);
    } else if (action.type === 'setValidationMessage') {
      setValidationMessage(state, action.index, action.language, action.message);
    } else if (action.type === 'setValidationExpression') {
      setValidationExpression(state, action.index, action.expression);
    } else if (action.type === 'deleteValidation') {
      deleteValidation(state, action.index);
    } else if (action.type === 'createValueSet') {
      createValueSet(state, action.itemId, action.entries);
    } else if (action.type === 'setValueSetEntries') {
      setValueSetEntries(state, action.valueSetId, action.entries);
    } else if (action.type === 'addValueSetEntry') {
      addValueSetEntry(state, action.valueSetId, action.entry, action.insertAfterIndex);
    } else if (action.type === 'updateValueSetEntry') {
      updateValueSetEntry(state, action.valueSetId, action.index, action.entry);
    } else if (action.type === 'updateValueSetEntryLabel') {
      updateValueSetEntryLabel(state, action.valueSetId, action.index, action.text, action.language);
    } else if (action.type === 'deleteValueSetentry') {
      deleteValueSetEntry(state, action.valueSetId, action.index);
    } else if (action.type === 'moveValueSetEntry') {
      moveValueSetEntry(state, action.valueSetId, action.from, action.to);
    } else if (action.type === 'setGlobalValueSetName') {
      setGlobalValueSetName(state, action.valueSetId, action.name);
    } else if (action.type === 'deleteLocalValueSet') {
      deleteLocalValueSet(state, action.valueSetId);
    } else if (action.type === 'deleteGlobalValueSet') {
      deleteGlobalValueSet(state, action.valueSetId);
    } else if (action.type === 'createVariable') {
      createVariable(state, action.context, action.insertAfterIndex);
    } else if (action.type === 'updateContextVariable') {
      updateContextVariable(state, action.variableId, action.contextType, action.defaultValue);
    } else if (action.type === 'updateExpressionVariable') {
      updateExpressionVariable(state, action.variableId, action.expression);
    } else if (action.type === 'updateVariablePublishing') {
      updateVariablePublishing(state, action.variableId, action.published);
    } else if (action.type === 'updateVariableDescription') {
      updateVariableDescription(state, action.variableId, action.description);
    } else if (action.type === 'deleteVariable') {
      deleteVariable(state, action.variableId);
    } else if (action.type === 'moveVariable') {
      moveVariable(state, action.name, action.toFilteredIndex, action.context);
    } else if (action.type === 'changeVariableId') {
      changeVariableId(state, action.variables);
    } else if (action.type === 'updateVariableName') {
      updateVariableName(state, action.currentName, action.originalName, action.to);
    } else if (action.type === 'clearPendingRenames') {
      clearPendingRenames(state);
    } else if (action.type === 'recordEntryRename') {
      recordEntryRename(state, action.valueSetId, action.from, action.to);
    } else if (action.type === 'clearPendingEntryRenames') {
      clearPendingEntryRenames(state);
    } else if (action.type === 'syncAfterSave') {
      syncAfterSave(state, action.item, action.valueSets, action.composerMetadata, action.variables);
    } else if (action.type === 'resetItems') {
      resetItems(state, action.items);
    } else if (action.type === 'resetVariables') {
      resetVariables(state, action.variables);
    } else if (action.type === 'applyIdRenameMerge') {
      applyIdRenameMerge(state, action.mergedItem, action.mergedValueSets, action.mergedItems, action.mergedVariables, action.mergedComposerMetadata);
    } else if (action.type === 'setMetadataValue') {
      setMetadataValue(state, action.attr, action.value);
    } else if (action.type === 'applyTranslations') {
      applyTranslations(state, action.translations, action.sourceLanguage, action.targetLanguage);
    } else if (action.type === 'addAITranslation') {
      addAITranslation(state, action.entryId, action.sourceLanguage, action.targetLanguage);
    } else if (action.type === 'removeAITranslation') {
      removeAITranslation(state, action.entryId, action.targetLanguage);
    }
  });

  return newState;
}