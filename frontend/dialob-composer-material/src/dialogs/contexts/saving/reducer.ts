import { produce } from "immer";
import { ContextVariable, ContextVariableType, DialobItemTemplate, LocalizedString, ValidationRule, ValueSetEntry, Variable } from "../../../types";
import { cleanLocalizedString, cleanString } from "../../../utils/StringUtils";
import { SavingAction } from "./SavingAction";
import { SavingState } from "./SavingContext";
import { isContextVariable } from "../../../utils/ItemUtils";


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
    const cleanedValue = cleanString(value);
    if (state.item[attribute] === undefined) {
      state.item[attribute] = { [language]: cleanedValue };
    } else {
      state.item[attribute][language] = cleanedValue;
    }
  } else {
    if (value === '') {
      delete state.item[attribute];
    } else {
      state.item[attribute] = value;
    }
  }
}

const updateItemId = (state: SavingState, itemId: string): void => {
  if (state.item) {
    state.item.id = itemId;
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

const setValidationExpression = (state: SavingState, index: number, expression: string): void => {
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

const addValueSetEntry = (state: SavingState, valueSetId: string, entry?: ValueSetEntry): void => {
  if (state.valueSets) {
    const vsIdx = state.valueSets.findIndex(vs => vs.id === valueSetId);
    if (vsIdx > -1) {
      const cleanedEntry: ValueSetEntry = entry ? { ...entry, label: cleanLocalizedString(entry.label) } : { id: '', label: {} };
      if (state.valueSets[vsIdx].entries !== undefined) {
        state.valueSets[vsIdx].entries!.push(cleanedEntry);
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

const updateValueSetEntryLabel = (state: SavingState, valueSetId: string, index: number, text: string | null, language: string): void => {
  if (state.valueSets) {
    const vsIdx = state.valueSets.findIndex(vs => vs.id === valueSetId);
    if (vsIdx > -1 && text !== null && state.valueSets[vsIdx].entries !== undefined && state.valueSets[vsIdx].entries![index] !== undefined && state.valueSets[vsIdx].entries![index].label !== undefined) {
      const cleanedText = cleanString(text);
      state.valueSets[vsIdx].entries![index].label[language] = cleanedText;
    }
  }
}

const deleteValueSetEntry = (state: SavingState, valueSetId: string, index: number): void => {
  if (state.valueSets) {
    const vsIdx = state.valueSets.findIndex(vs => vs.id === valueSetId);
    if (vsIdx > -1 && state.valueSets[vsIdx].entries !== undefined) {
      state.valueSets[vsIdx].entries!.splice(index, 1);
    }
  }
}

const moveValueSetEntry = (state: SavingState, valueSetId: string, from: number, to: number): void => {
  if (state.valueSets) {
    const vsIdx = state.valueSets.findIndex(vs => vs.id === valueSetId);
    if (vsIdx > -1 && state.valueSets[vsIdx].entries !== undefined) {
      const newIndex = to > state.valueSets[vsIdx].entries!.length ? state.valueSets[vsIdx].entries!.length - 1 : to;
      state.valueSets[vsIdx].entries!.splice(newIndex, 0, state.valueSets[vsIdx].entries!.splice(from, 1)[0]);
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
  }
}

const deleteGlobalValueSet = (state: SavingState, valueSetId: string): void => {
  if (state.valueSets && state.valueSets?.find(vs => vs.id === valueSetId) !== undefined && state.composerMetadata?.globalValueSets !== undefined) {
    state.valueSets = state.valueSets.filter(vs => vs.id !== valueSetId);
    state.composerMetadata.globalValueSets = state.composerMetadata.globalValueSets.filter(gvs => gvs.valueSetId !== valueSetId);
  }
}

const createVariable = (state: SavingState, context: boolean): void => {
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

const updateExpressionVariable = (state: SavingState, variableId: string, expression: string): void => {
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

const updateVariableDescription = (state: SavingState, variableId: string, description: string): void => {
  if (state.variables) {
    const varIdx = state.variables.findIndex(v => v.name === variableId);
    if (varIdx > -1) {
      (state.variables[varIdx]).description = description;
    }
  }
}

const moveVariable = (state: SavingState, origin: ContextVariable | Variable, destination: ContextVariable | Variable): void => {
  const originIdx = state.variables?.findIndex(v => v.name === origin.name);
  const destinationIdx = state.variables?.findIndex(v => v.name === destination.name);
  if (originIdx !== undefined && destinationIdx !== undefined && originIdx > -1 && destinationIdx > -1 && state.variables) {
    [state.variables[originIdx], state.variables[destinationIdx]] = [state.variables[destinationIdx], state.variables[originIdx]];
  }
}

const changeVariableId = (state: SavingState, variables: (ContextVariable | Variable)[]): void => {
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


export const itemReducer = (state: SavingState, action: SavingAction): SavingState => {

  const newState = produce(state, state => {
    if (action.type === 'updateItem') {
      updateItem(state, action.attribute, action.value, action.language);
    } else if (action.type === 'updateItemId') {
      updateItemId(state, action.itemId);
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
      addValueSetEntry(state, action.valueSetId, action.entry);
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
      createVariable(state, action.context);
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
      moveVariable(state, action.origin, action.destination);
    } else if (action.type === 'changeVariableId') {
      changeVariableId(state, action.variables);
    } else if (action.type === 'setMetadataValue') {
      setMetadataValue(state, action.attr, action.value);
    }
  });

  return newState;
}