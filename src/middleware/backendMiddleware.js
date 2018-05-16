import * as Actions from '../actions/constants';
import * as Status from '../helpers/constants';
import FormService from '../services/FormService';
import {setForm, saveForm, setFormRevision, setStatus, setErrors, redirectPreview} from '../actions';

const SAVE_DELAY = 500;
var saveTimer;

export const backendMiddleware = store => {
  let config = store.getState().config;
  let formService = new FormService(config.get('apiUrl'), config.get('csrf'));

  return next => action => {
    if (action.type === Actions.LOAD_FORM) {
      formService.loadForm(action.formId)
        .then(json => {
          store.dispatch(setForm(json));
          store.dispatch(setStatus(Status.STATUS_OK)); // TODO: Check status
        })
        .catch(error => console.error('Err', error)); // TODO: Error handling
    } else if (action.type === Actions.SAVE_FORM) {
      formService.saveForm(store.getState().form.toJS())
        .then(json => {
          store.dispatch(setFormRevision(json.rev));
          store.dispatch(setErrors(json.errors));
        })
        .catch(error => console.error('Err', error)); // TODO: Error handling
    } else if (action.type === Actions.PERFORM_CHANGE_ID) {
      formService.changeItemId(store.getState().form.toJS(), action.oldId, action.newId)
        .then(json => {
          store.dispatch(setForm(json.form));
          store.dispatch(setErrors(json.errors));
        })
        .catch(error => console.error('Err', error)); // TODO: Error handling
    } else if (action.type === Actions.CREATE_PREVIEW_SESSION) {
      let context = null;
      if (action.context) {
        context = store.getState().form.getIn(['metadata', 'composer', 'contextValues']).entrySeq().map(e => ({id: e[0], value: e[1]})).toJS();
      }
      formService.createSession(store.getState().form.get('_id'), action.language, context)
        .then(json => {
          store.dispatch(redirectPreview(json._id));
        })
        .catch(error => console.error('Err', error)); // TODO: Error handling
    }
    let result = next(action);
    if (action.saveNeeded === true) {
      store.dispatch(setStatus(Status.STATUS_BUSY));
      clearTimeout(saveTimer);
      // TODO: show dirty indicator
      saveTimer = setTimeout(() => store.dispatch(saveForm()), SAVE_DELAY);
    }
    return result;
  }
}
