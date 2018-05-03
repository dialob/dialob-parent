import * as Actions from '../actions/constants';
import FormService from '../services/FormService';
import {setForm} from '../actions';

export const backendMiddleware = store => {
  let config = store.getState().config;
  let formService = new FormService(config.get('apiUrl'), config.get('csrf'));

  return next => action => {
    if (action.type === Actions.LOAD_FORM) {
      formService.loadForm(action.formId)
        .then(json => store.dispatch(setForm(json)))
        .catch(error => console.error('Err', error)); // TODO: Error handling
    }
    return next(action);
  }
}