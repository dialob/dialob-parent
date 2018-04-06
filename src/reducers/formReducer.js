import DEBUG_FORM from './debug_form';
import Immutable from 'immutable';
import * as Actions from '../actions/constants';

const INITIAL_STATE = Immutable.fromJS(DEBUG_FORM);

export function formReducer(state = INITIAL_STATE, action) {
  return state;
}