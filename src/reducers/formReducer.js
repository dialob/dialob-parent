import DEBUG_FORM from './debug_form_simple';
import Immutable from 'immutable';
import * as Actions from '../actions/constants';

const INITIAL_STATE = Immutable.fromJS(DEBUG_FORM);

function generateItemId(state, prefix) {
  let idx = 1;
  while (state.has(`${prefix}${idx}`)) {
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
      console.log('DELETE_ITEM', action);
      return state;
    default:
      //NOP:
  }
  return state;
}
