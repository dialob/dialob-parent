import DEBUG_FORM from './debug_form_simple';
import Immutable from 'immutable';
import * as Actions from '../actions/constants';
import { createValueset } from '../actions';

const INITIAL_STATE = Immutable.fromJS(DEBUG_FORM);

function generateItemId(state, prefix) {
  let idx = 1;
  while (state && state.has(`${prefix}${idx}`)) {
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
  return state.set(itemId, Immutable.fromJS(Object.assign({id: itemId}, action.config)))
              .update(action.parentItemId, parent => {
                if (action.afterItemId) {
                  return parent.update('items', items => items ? items.insert(items.findIndex(i => i === action.afterItemId) + 1, itemId) : Immutable.List([itemId]));
                } else {
                  return parent.update('items', items => items ? items.push(itemId) : Immutable.List([itemId]));
                }
              });
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

export function formReducer(state = INITIAL_STATE, action) {
  switch (action.type) {
    case Actions.ADD_ITEM:
      return state.update('data', data => addItem(data, action));
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
    default:
      //NOP:
  }
  return state;
}
