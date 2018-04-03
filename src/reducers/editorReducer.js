import DEBUG_FORM from './debug_form';
import Immutable from 'immutable';

const INITIAL_STATE = Immutable.fromJS(DEBUG_FORM);

export function editorReducer(state = INITIAL_STATE, action) {
  return state;
}