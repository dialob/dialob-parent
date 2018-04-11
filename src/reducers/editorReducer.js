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
    default:
      // NOP
  }
  return state;
}