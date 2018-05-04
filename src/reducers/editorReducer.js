import Immutable from 'immutable';
import * as Actions from '../actions/constants';
import * as Status from '../helpers/constants';

const INITIAL_STATE = Immutable.Map();

function setErrors(state, errors) {
  let newState = state.set('errors', Immutable.fromJS(errors));
  if (errors && errors.length > 0) {
    if (errors.findIndex(e => e.severity === 'FATAL') > -1) {
      return newState.set('status', Status.STATUS_FATAL);
    } else {
      return newState.set('status', Status.STATUS_ERRORS);
    }
    // TODO: Handle warnings only case
  } else {
    return newState.set('status', Status.STATUS_OK);
  }
}

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
    case Actions.SET_STATUS:
      return state.set('status', action.status);
    case Actions.SET_ERRORS:
      return setErrors(state, action.errors);
    default:
      // NOP
  }
  return state;
}