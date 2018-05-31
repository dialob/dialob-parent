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
    case Actions.SET_FORM:
      return state.delete('changeId').set('activeLanguage', action.formData.metadata.languages[0]);
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
      return state.set('itemOptions', Immutable.fromJS({itemId: action.itemId, isPage: action.isPage}));
    case Actions.HIDE_ITEM_OPTIONS:
      return state.delete('itemOptions');
    case Actions.SET_STATUS:
      return state.set('status', action.status);
    case Actions.SET_ERRORS:
      return setErrors(state, action.errors);
    case Actions.SHOW_FORM_OPTIONS:
      return state.set('formOptions', true);
    case Actions.HIDE_FORM_OPTIONS:
      return state.delete('formOptions');
    case Actions.SHOW_VARIABLES:
      return state.set('variablesDialog', true)
    case Actions.HIDE_VARIABLES:
      return state.delete('variablesDialog');
    case Actions.SHOW_CHANGE_ID:
      return state.set('changeId', action.changeId);
    case Actions.HIDE_CHANGE_ID:
      return state.delete('changeId');
    case Actions.SHOW_PREVIEW_CONTEXT:
      return state.set('previewContextDialog', true);
    case Actions.HIDE_PREVIEW_CONTEXT:
      return state.delete('previewContextDialog');
    case Actions.SHOW_VALUESETS:
      return state.set('valueSetsOpen', true);
    case Actions.HIDE_VALUESETS:
      return state.delete('valueSetsOpen');
    case Actions.SHOW_TRANSLATION:
      return state.set('translationOpen', true)
    case Actions.HIDE_TRANSLATION:
      return state.delete('translationOpen');
    default:
      // NOP
  }
  return state;
}