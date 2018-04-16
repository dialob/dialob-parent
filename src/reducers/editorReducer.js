import Immutable from 'immutable';
import * as Actions from '../actions/constants';

const INITIAL_STATE = Immutable.Map();

export function editorReducer(state = INITIAL_STATE, action) {
  switch (action.type) {
    case Actions.SET_ACTIVE_ITEM:
      return state.set('activeItemId', action.itemId);
    case Actions.SET_ACTIVE_PAGE:
      return state.set('activeItemId', action.itemId).set('activePageId', action.itemId);
    case Actions.SET_ACTIVE_LANGUAGE:
      return state.set('activeLanguage', action.language);
    case Actions.ASK_CONFIRMATION:
      return state.set('confirmableAction', action.action);
    case Actions.CANCEL_CONFIRMATION:
      return state.delete('confirmableAction');
    case Actions.SHOW_ITEM_OPTIONS:
      return state.set('itemOptions', action.itemId);
    case Actions.HIDE_ITEM_OPTIONS:
      return state.delete('itemOptions');
    default:
      // NOP
  }
  return state;
}