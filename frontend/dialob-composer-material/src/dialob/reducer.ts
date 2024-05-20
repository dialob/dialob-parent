import { produce } from 'immer';
import camelCase from 'lodash.camelcase';
import { ComposerAction } from './actions';
import {
  ComposerState, DialobItemTemplate, ComposerCallbacks, ValueSetEntry, ContextVariableType, ContextVariable, Variable, isContextVariable,
  ValidationRule, LocalizedString, DialobItems
} from './types';

export const generateItemIdWithPrefix = (state: ComposerState, prefix: string): string => {
  const idList = Object.keys(state.data).concat(state.variables?.map(v => v.name) || []);
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

export const generateItemId = (state: ComposerState, itemTemplate: DialobItemTemplate): string => {
  const prefix = itemTemplate.view ? camelCase(itemTemplate.view) : itemTemplate.type;
  return generateItemIdWithPrefix(state, prefix);
};

export const generateValueSetId = (state: ComposerState, prefix = 'vs'): string => {
  let idx = 1;
  while (state.valueSets && state.valueSets.findIndex(vs => vs.id === `${prefix}${idx}`) > -1) {
    idx++;
  }
  return `${prefix}${idx}`;
}

const addItem = (state: ComposerState, itemTemplate: DialobItemTemplate, parentItemId: string, afterItemId?: string, callbacks?: ComposerCallbacks): void => {
  const id = generateItemId(state, itemTemplate);
  const newItem = {
    ...itemTemplate,
    id: generateItemId(state, itemTemplate),
  }
  state.data[id] = Object.assign(newItem, { id });
  // TODO: Sanity check if parentItemId exists in form
  const newIndex = state.data[parentItemId].items?.findIndex(i => i === afterItemId);
  if (newIndex === undefined) {
    state.data[parentItemId].items = [id];
  } else if (newIndex < 0) {
    state.data[parentItemId].items?.push(id);
  } else {
    state.data[parentItemId].items?.splice(newIndex + 1, 0, id);
  }

  const onAddItem = callbacks?.onAddItem;
  if (onAddItem) {
    onAddItem(state, state.data[id]);
  }
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const updateItem = (state: ComposerState, itemId: string, attribute: string, value: any, language?: string,): void => {
  // TODO: Sanity: item exists
  // TODO: Sanity: language exists in form level
  // TODO: Sanity: attribute is not an id or type
  if (language) {
    if (state.data[itemId][attribute] === undefined) {
      state.data[itemId][attribute] = { [language]: value };
    } else {
      state.data[itemId][attribute][language] = value;
    }
  } else {
    state.data[itemId][attribute] = value;
  }
}

const updateLocalizedString = (state: ComposerState, itemId: string, attribute: string, value: LocalizedString, index?: number): void => {
  const item = state.data[itemId];
  if (item && (attribute === 'label' || attribute === 'description')) {
    item[attribute] = value;
  } else if (attribute === 'validations' && index !== undefined) {
    const validations = state.data[itemId].validations;
    if (validations) {
      const rule = validations[index];
      if (!rule) {
        return;
      }
      rule.message = value;
    }
  } else {
    return;
  }
}

const convertItem = (state: ComposerState, itemId: string, itemTemplate: DialobItemTemplate): void => {
  const item = state.data[itemId];
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

const deleteItem = (state: ComposerState, itemId: string): void => {
  // TODO: Sanity: item exists

  // Collect item and children to delete
  const collect = (target: string): string[] => {
    const subItems = state.data[target].items;
    if (subItems) {
      return [target].concat(subItems.reduce((l, i) => l.concat(collect(i)), [] as string[]));
    } else {
      return [target];
    }
  }
  const toDelete = collect(itemId);

  // Collect non-global valuesets for to-be deleted items
  const globalValueSets = state.metadata?.composer?.globalValueSets || [];
  const valueSets = toDelete.map(iid => state.data[iid].valueSetId)
    .filter(vsId => vsId)
    .filter(vsId => globalValueSets.findIndex(gvs => gvs.valueSetId === vsId) === -1)

  // Delete items
  toDelete.forEach(itemId => {
    delete state.data[itemId];
  });

  // Delete valuesets
  valueSets.forEach(vsId => {
    const vsIdx = state.valueSets ? state.valueSets.findIndex(vs => vs.id === vsId) : -1;
    if (vsIdx > -1 && state.valueSets) {
      state.valueSets.splice(vsIdx, 1);
    }
  });

  // Delete parent ref
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  for (const [parentItemId, item] of Object.entries(state.data)) {
    const itemIndex = item.items ? item.items.findIndex(i => i === itemId) : -1;
    if (itemIndex > -1 && item.items) {
      item.items.splice(itemIndex, 1);
      break;
    }
  }
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const setItemProp = (state: ComposerState, itemId: string, key: string, value: any): void => {
  // TODO: Sanity: item exists
  if (state.data[itemId].props === undefined) {
    state.data[itemId].props = { [key]: value };
  } else {
    const props = state.data[itemId].props;
    if (props !== undefined) {
      props[key] = value;
    }
  }
}

const deleteItemProp = (state: ComposerState, itemId: string, key: string): void => {
  // TODO: Sanity item exists
  const props = state.data[itemId]?.props;
  if (props !== undefined) {
    delete props[key];

    if (Object.keys(props).length === 0) {
      delete state.data[itemId].props;
    }
  }
}

const createValidation = (state: ComposerState, itemId: string, rule?: ValidationRule): void => {
  const emptyRule: ValidationRule = { message: {}, rule: '' };

  // TODO: Sanity: item exists
  if (state.data[itemId].validations === undefined) {
    state.data[itemId].validations = [rule ? rule : emptyRule];
  } else {
    state.data[itemId].validations?.push(rule ? rule : emptyRule);
  }
}

const setValidationMessage = (state: ComposerState, itemId: string, index: number, language: string, message: string): void => {
  const validations = state.data[itemId].validations;
  if (validations) {
    const rule = validations[index];
    if (!rule) {
      return;
    }
    if (!rule.message) {
      rule.message = { [language]: message };
    } else {
      rule.message[language] = message;
    }
  }
}

const setValidationExpression = (state: ComposerState, itemId: string, index: number, expression: string): void => {
  const validations = state.data[itemId].validations;
  if (validations) {
    const rule = validations[index];
    if (!rule) {
      return;
    }
    rule.rule = expression;
  }
}

const deleteValidation = (state: ComposerState, itemId: string, index: number): void => {
  const validations = state.data[itemId].validations;
  if (validations) {
    const rule = validations[index];
    if (!rule) {
      return;
    }
    validations.splice(index, 1);
  }
}

const moveItem = (state: ComposerState, itemId: string, fromIndex: number, toIndex: number, fromParent: string, toParent: string): void => {
  // TODO: Sanity: item exists
  // TODO: Ergonomics: fromParent and fromIndex can be computed by itemId and not needed in function parameters
  state.data[fromParent]?.items?.splice(fromIndex, 1);
  if (state.data[toParent].items === undefined) {
    state.data[toParent].items = [itemId];
  } else {
    state.data[toParent]?.items?.splice(toIndex, 0, itemId);
  }
}

const createValueSet = (state: ComposerState, itemId: string | null, entries?: ValueSetEntry[]): void => {
  // TODO: Sanity: item exists if not null

  const valueSetId = generateValueSetId(state, 'vs');
  const valueSet = {
    id: valueSetId,
    entries: entries || []
  };

  if (!state.valueSets) {
    state.valueSets = [valueSet];
  } else {
    state.valueSets.push(valueSet);
  }

  if (itemId) {
    // Local valueset
    state.data[itemId].valueSetId = valueSetId;
  } else {
    // Global valueset
    if (!state.metadata.composer) {
      state.metadata.composer = {};
    }
    if (!state.metadata.composer.globalValueSets) {
      state.metadata.composer.globalValueSets = [];
    }
    state.metadata.composer.globalValueSets.push({ valueSetId, label: '' })
  }
}

const setValueSetEntries = (state: ComposerState, valueSetId: string, entries: ValueSetEntry[]): void => {
  // TODO: Sanity: valueSet exists

  if (state.valueSets) {
    const vsIdx = state.valueSets.findIndex(vs => vs.id === valueSetId);
    if (vsIdx > -1) {
      state.valueSets[vsIdx].entries = entries;
    }
  }
}

const addValueSetEntry = (state: ComposerState, valueSetId: string, entry?: ValueSetEntry): void => {
  // TODO Sanity: valueSet extists

  if (state.valueSets) {
    const vsIdx = state.valueSets.findIndex(vs => vs.id === valueSetId);
    if (vsIdx > -1) {
      const newEntry = entry ? entry : { id: '', label: {} };
      state.valueSets[vsIdx].entries.push(newEntry);
    }
  }
}

const updateValueSetEntry = (state: ComposerState, valueSetId: string, index: number, entry: ValueSetEntry): void => {
  // TODO Sanity: ValueSet exists
  // TODO Sanity: Entry index exists

  if (state.valueSets) {
    const vsIdx = state.valueSets.findIndex(vs => vs.id === valueSetId);
    if (vsIdx > -1) {
      state.valueSets[vsIdx].entries[index] = entry;
    }
  }
}

const updateValueSetEntryLabel = (state: ComposerState, valueSetId: string, index: number, text: string | null, language: string): void => {
  if (state.valueSets) {
    const vsIdx = state.valueSets.findIndex(vs => vs.id === valueSetId);
    if (vsIdx > -1 && text !== null) {

      state.valueSets[vsIdx].entries[index].label[language] = text;
    }
  }
}

const deleteValueSetEntry = (state: ComposerState, valueSetId: string, index: number): void => {
  // TODO Sanity: ValueSet exists
  // TODO Sanity: Entry index exists

  if (state.valueSets) {
    const vsIdx = state.valueSets.findIndex(vs => vs.id === valueSetId);
    if (vsIdx > -1) {
      state.valueSets[vsIdx].entries.splice(index, 1);
    }
  }
}

const moveValueSetEntry = (state: ComposerState, valueSetId: string, from: number, to: number): void => {
  // TODO Sanity: ValueSet exists
  // TODO Sanity: Entry index exists

  if (state.valueSets) {
    const vsIdx = state.valueSets.findIndex(vs => vs.id === valueSetId);
    if (vsIdx > -1) {
      const newIndex = to > state.valueSets[vsIdx].entries.length ? state.valueSets[vsIdx].entries.length - 1 : to;
      state.valueSets[vsIdx].entries.splice(newIndex, 0, state.valueSets[vsIdx].entries.splice(from, 1)[0]);
    }
  }
}

const setGlobalValueSetName = (state: ComposerState, valueSetId: string, name: string): void => {
  if (state.metadata?.composer?.globalValueSets) {
    const gvsIdx = state.metadata.composer.globalValueSets.findIndex(gvs => gvs.valueSetId === valueSetId);
    if (gvsIdx > -1) {
      state.metadata.composer.globalValueSets[gvsIdx].label = name;
    }
  }
}

const deleteGlobalValueSet = (state: ComposerState, valueSetId: string): void => {
  if (state.valueSets && state.valueSets?.find(vs => vs.id === valueSetId) !== undefined && state.metadata?.composer?.globalValueSets !== undefined) {
    state.valueSets = state.valueSets.filter(vs => vs.id !== valueSetId);
    state.metadata.composer.globalValueSets = state.metadata.composer.globalValueSets.filter(gvs => gvs.valueSetId !== valueSetId);
  }
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const setMetadataValue = (state: ComposerState, attr: string, value: any): void => {
  // TODO: Sanity: Prevent overwriting certain critical attributes

  state.metadata[attr] = value;
}

const setContextValue = (state: ComposerState, name: string, value: string): void => {
  if (state.metadata.composer) {
    if (!state.metadata.composer?.contextValues) {
      state.metadata.composer.contextValues = {};
    }
    state.metadata.composer.contextValues[name] = value;
  }
}

const createVariable = (state: ComposerState, context: boolean): void => {
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
const updateContextVariable = (state: ComposerState, variableId: string, contextType?: ContextVariableType | string, defaultValue?: any): void => {
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

const updateExpressionVariable = (state: ComposerState, variableId: string, expression: string): void => {
  if (state.variables) {
    const varIdx = state.variables.findIndex(v => !isContextVariable(v) && v.name === variableId);
    if (varIdx > -1) {
      (state.variables[varIdx] as Variable).expression = expression;
    }
  }
}

const deleteVariable = (state: ComposerState, variableId: string): void => {
  if (state.variables) {
    const varIdx = state.variables.findIndex(v => v.name === variableId);
    if (varIdx > -1) {
      state.variables.splice(varIdx, 1);
    }
  }
}

const updateVariablePublishing = (state: ComposerState, variableId: string, published: boolean): void => {
  if (state.variables) {
    const varIdx = state.variables.findIndex(v => v.name === variableId);
    if (varIdx > -1) {
      (state.variables[varIdx]).published = published;
    }
  }
}

const moveVariable = (state: ComposerState, origin: ContextVariable | Variable, destination: ContextVariable | Variable): void => {
  const originIdx = state.variables?.findIndex(v => v.name === origin.name);
  const destinationIdx = state.variables?.findIndex(v => v.name === destination.name);
  if (originIdx !== undefined && destinationIdx !== undefined && originIdx > -1 && destinationIdx > -1 && state.variables) {
    [state.variables[originIdx], state.variables[destinationIdx]] = [state.variables[destinationIdx], state.variables[originIdx]];
  }
}

const addLanguage = (state: ComposerState, language: string, copyFrom?: string): void => {
  if (state.metadata.languages && state.metadata.languages.indexOf(language) > -1) {
    // Already exists, NO-OP
    return;
  }

  // Copy language
  if (copyFrom && copyFrom !== language) {
    // Items
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    for (const [itemId, item] of Object.entries(state.data)) {
      // Item label
      if (item.label) {
        item.label[language] = item.label[copyFrom];
      }
      // Item description
      if (item.description) {
        item.description[language] = item.description[copyFrom];
      }
      // Item validation messages
      if (item.validations) {
        item.validations.forEach(validation => {
          if (validation.message) {
            validation.message[language] = validation.message[copyFrom];
          }
        })
      }
    }
    // Valueset entry labels
    if (state.valueSets) {
      state.valueSets.forEach(vs => {
        vs.entries.forEach(vse => {
          if (vse.label) {
            vse.label[language] = vse.label[copyFrom];
          }
        })
      })
    }
  }

  // Metadata 
  if (state.metadata.languages) {
    state.metadata.languages.push(language);
  } else {
    state.metadata.languages = [language];
  }
}

const deleteLanguage = (state: ComposerState, language: string): void => {
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  for (const [itemId, item] of Object.entries(state.data)) {
    if (item.label) {
      delete item.label[language];
    }
    if (item.description) {
      delete item.description[language];
    }
    if (item.validations) {
      item.validations.forEach(v => {
        if (v.message) {
          delete v.message[language];
        }
      })
    }
  }

  if (state.valueSets) {
    state.valueSets.forEach(vs => {
      vs.entries.forEach(vse => {
        if (vse.label[language]) {
          delete vse.label[language];
        }
      });
    });
  }

  if (state.metadata.languages) {
    const langIdx = state.metadata.languages.indexOf(language);
    if (langIdx > -1) {
      state.metadata.languages.splice(langIdx, 1);
    }
  }
}

const setRevision = (state: ComposerState, revision: string): void => {
  state._rev = revision;
}

const setForm = (state: ComposerState, form: ComposerState, tagName?: string): void => {
  Object.assign(state, form);
  if (tagName && tagName !== 'LATEST') {
    state._tag = tagName;
  } else {
    state._tag = undefined;
  }
  console.log('>> SET FORM', state);
}

const setFormData = (state: ComposerState, formData: DialobItems): void => {
  state.data = formData;
}

export const formReducer = (state: ComposerState, action: ComposerAction, callbacks?: ComposerCallbacks): ComposerState => {
  console.log('>> REDUCER', action, state)
  if (state._tag && (action.type === 'setForm' ? action.tagName !== 'LATEST' : true)) {
    // if a version tag is loaded, then it's in read-only mode
    return state;
  }

  const newState = produce(state, state => {
    if (action.type === 'addItem') {
      addItem(state, action.config, action.parentItemId, action.afterItemId, callbacks);
    } else if (action.type === 'updateItem') {
      updateItem(state, action.itemId, action.attribute, action.value, action.language);
    } else if (action.type === 'updateLocalizedString') {
      updateLocalizedString(state, action.itemId, action.attribute, action.value, action.index);
    } else if (action.type === 'changeItemType') {
      convertItem(state, action.itemId, action.config);
    } else if (action.type === 'deleteItem') {
      deleteItem(state, action.itemId);
    } else if (action.type === 'setItemProp') {
      setItemProp(state, action.itemId, action.key, action.value);
    } else if (action.type === 'deleteItemProp') {
      deleteItemProp(state, action.itemId, action.key);
    } else if (action.type === 'createValidation') {
      createValidation(state, action.itemId, action.rule);
    } else if (action.type === 'setValidationMessage') {
      setValidationMessage(state, action.itemId, action.index, action.language, action.message);
    } else if (action.type === 'setValidationExpression') {
      setValidationExpression(state, action.itemId, action.index, action.expression);
    } else if (action.type === 'deleteValidation') {
      deleteValidation(state, action.itemId, action.index);
    } else if (action.type === 'moveItem') {
      moveItem(state, action.itemId, action.fromIndex, action.toIndex, action.fromParent, action.toParent);
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
    } else if (action.type === 'deleteGlobalValueSet') {
      deleteGlobalValueSet(state, action.valueSetId);
    } else if (action.type === 'setMetadataValue') {
      setMetadataValue(state, action.attr, action.value);
    } else if (action.type === 'setContextValue') {
      setContextValue(state, action.name, action.value);
    } else if (action.type === 'createVariable') {
      createVariable(state, action.context);
    } else if (action.type === 'updateContextVariable') {
      updateContextVariable(state, action.variableId, action.contextType, action.defaultValue);
    } else if (action.type === 'updateExpressionVariable') {
      updateExpressionVariable(state, action.variableId, action.expression);
    } else if (action.type === 'updateVariablePublishing') {
      updateVariablePublishing(state, action.variableId, action.published);
    } else if (action.type === 'deleteVariable') {
      deleteVariable(state, action.variableId);
    } else if (action.type === 'moveVariable') {
      moveVariable(state, action.origin, action.destination);
    } else if (action.type === 'addLanguage') {
      addLanguage(state, action.language, action.copyFrom);
    } else if (action.type === 'deleteLanguage') {
      deleteLanguage(state, action.language);
    } else if (action.type === 'setRevision') {
      console.log('>> SET REV', action.revision);
      setRevision(state, action.revision);
    } else if (action.type === 'setForm') {
      setForm(state, action.form, action.tagName);
    } else if (action.type === 'setFormData') {
      setFormData(state, action.formData);
    }
  });

  return newState;
}
