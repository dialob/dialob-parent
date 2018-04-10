import Immutable from 'immutable';
import * as Actions from '../actions/constants';

const INITIAL_STATE = Immutable.Map();

export function editorReducer(state = INITIAL_STATE, action) {
  switch (action.type) {
    case Actions.SET_ACTIVE_ITEM:
      return state.set('activeItemId', action.itemId);
    case Actions.SET_ACTIVE_PAGE:
      return state.set('activeItemId', action.itemId).set('activePageId', action.itemId);
    case Actions.ADD_ITEM:
      console.log('ADD_ITEM', action);
      return state;
    case Actions.CHANGE_ITEM_TYPE:
      console.log('CHANGE_ITEM_TYPE', action);
      return state;
    default:
      // NOP
  }
  return state;
}