import DEBUG_FORM from './debug_form_simple';
import Immutable from 'immutable';
import * as Actions from '../actions/constants';

//const INITIAL_STATE = Immutable.fromJS(DEBUG_FORM);
const INITIAL_STATE = Immutable.Map();

function generateItemId(state, prefix) {
  let idx = 1;
  let data = state.get('data') || Immutable.Map();
  let variables = state.get('variables') || Immutable.List();
  while (state && (state.get('data').has(`${prefix}${idx}`) || variables.findIndex(v => v.get('name') === `${prefix}${idx}`) > -1)) {
    idx++;
  }
  return `${prefix}${idx}`;
}

function generateValueSetId(state, prefix) {
  let idx = 1;
  while (state && state.find(vs => vs.get('id') === `${prefix}${idx}`)) {
    idx++;
  }
  return `${prefix}${idx}`;
}

function addItem(state, action) {
  const itemId = generateItemId(state, action.config.idPrefix || action.config.type);
  return state.update('data', data => data.set(itemId, Immutable.fromJS(Object.assign({id: itemId}, action.config)))
              .update(action.parentItemId, parent => {
                if (action.afterItemId) {
                  return parent.update('items', items => items ? items.insert(items.findIndex(i => i === action.afterItemId) + 1, itemId) : Immutable.List([itemId]));
                } else {
                  return parent.update('items', items => items ? items.push(itemId) : Immutable.List([itemId]));
                }
              }));
}

function deleteItem(state, itemId) {
  // Find children and eliminate them
  const collect = target => {
    let subItems = state.getIn(['data', target, 'items']);
    if (subItems) {
      return [target].concat(subItems.toJS().reduce((l, i) => l.concat(collect(i)), []));
    } else {
      return [target];
    }
  };

  let toDelete = collect(itemId);

  // Collect && delete valueSets
  // TODO: Check for global vsets
  let valueSets = toDelete.map(itemId => state.getIn(['data', itemId, 'valueSetId'])).filter(vsId => vsId);
  let newState = state;
  valueSets.forEach(vsId => newState = newState.update('valueSets', v => v.delete(v.findIndex(i => i.get('id') === vsId))));

  newState = newState.update('data', s => toDelete.reduce((i, k) => i.delete(k), s));
  // return state.deleteAll(toDelete); // Immutable 4.0

  // Remove parent ref also
  const parent = newState.get('data').find(v => v.get('items') && v.get('items').contains(itemId));
  return parent ? newState.updateIn(['data', parent.get('id'), 'items'], items => items.delete(items.indexOf(itemId)))
     : newState;
}

function newValueSet(state, itemId = null) {
  const valueSetId = generateValueSetId(state.get('valueSets'), 'vs');
  const valueSet = Immutable.fromJS({
    id: valueSetId,
    entries: []
  });
  let newState = state.update('valueSets', valueSets => {
    if (!valueSets) {
      return new Immutable.List([valueSet])
    } else {
      return valueSets.push(valueSet);
    }
  });

  if (itemId) {
    newState = newState.setIn(['data', itemId, 'valueSetId'], valueSetId);
  }

  return newState;
}

function findValuesetIndex(state, valueSetId) {
  const valueSets = state.get('valueSets');
  return valueSets ? valueSets.findIndex(s => s.get('id') === valueSetId) : -1;
}

function newValueSetEntry(state, valueSetId) {
  const index = findValuesetIndex(state, valueSetId);
  if (index === -1) {
    return state;
  } else {
    return state.updateIn(['valueSets', index, 'entries'], entries => {
      let entry = Immutable.fromJS({
        id: '',
        label: {}
      });
      if (!entries) {
        return new Immutable.List([entry])
      } else {
        return entries.push(entry)
      }
    })
  }
}

function updateValuesetEntry(state, valueSetId, index, id, label, language) {
  const vsIndex = findValuesetIndex(state, valueSetId);
  if (vsIndex === -1) {
    return state;
  }
  if (id) {
    return state.setIn(['valueSets', vsIndex, 'entries', index, 'id'], id);
  } else if (label) {
    return state.setIn(['valueSets', vsIndex, 'entries', index, 'label', language], label);
  }
  return state;
}

function deleteValuesetEntry(state, valueSetId, index) {
  const vsIndex = findValuesetIndex(state, valueSetId);
  if (vsIndex === -1) {
    return state;
  }
  return state.deleteIn(['valueSets', vsIndex, 'entries', index]);
}

function newVariable(state, context = false) {
  const variableId = generateItemId(state, context ? 'context' : 'var');
  const variable = Immutable.fromJS(context ?
    {
      name: variableId,
      context: true,
      contextType: 'text'
    } :
    {
      name: variableId,
      expression: ''
    }
  );
  return state.update('variables', variables => {
    if (!variables) {
      return new Immutable.List([variable]);
    } else {
      return variables.push(variable);
    }
  });
}

function updateVariable(state, id, attribute, value) {
  const variableIndex = state.get('variables').findIndex(v => v.get('name') === id);
  if (variableIndex === -1) {
    return state;
  }
  return state.setIn(['variables', variableIndex, attribute], value);
}

export function formReducer(state = INITIAL_STATE, action) {
  switch (action.type) {
    case Actions.SET_FORM:
      return Immutable.fromJS(action.formData);
    case Actions.SET_FORM_REVISION:
      return state.set('_rev', action.revision);
    case Actions.ADD_ITEM:
      return addItem(state, action);
    case Actions.UPDATE_ITEM:
      return action.language ? state.setIn(['data', action.itemId, action.attribute, action.language], action.value)
                             : state.setIn(['data', action.itemId, action.attribute], action.value);
    case Actions.CHANGE_ITEM_TYPE:
      console.log('CHANGE_ITEM_TYPE', action);
      return state;
    case Actions.DELETE_ITEM:
     // return state.update('data', data => deleteItem(data, action.itemId));
      return deleteItem(state, action.itemId);
    case Actions.CREATE_VALUESET:
      return newValueSet(state, action.forItem);
    case Actions.CREATE_VALUESET_ENTRY:
      return newValueSetEntry(state, action.valueSetId);
    case Actions.UPDATE_VALUESET_ENTRY:
      return updateValuesetEntry(state, action.valueSetId, action.index, action.id, action.label, action.language);
    case Actions.DELETE_VALUESET_ENTRY:
      return deleteValuesetEntry(state, action.valueSetId, action.index);
    case Actions.SET_METADATA_VALUE:
      if (action.value) {
        return state.setIn(['metadata', action.attribute], Immutable.fromJS(action.value));
      } else {
        return state.deleteIn(['metadata', action.attribute]);
      }
    case Actions.CREATE_CONTEXT_VARIABLE:
      return newVariable(state, true);
    case Actions.CREATE_EXPRESSION_VARIABLE:
      return newVariable(state);
    case Actions.UPDATE_VARIABLE:
      return updateVariable(state, action.id, action.attribute, action.value);
    case Actions.DELETE_VARIABLE:
      return state; // TODO
    default:
      //NOP:
  }
  return state;
}
