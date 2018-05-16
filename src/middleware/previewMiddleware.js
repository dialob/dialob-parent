import * as Actions from '../actions/constants';
import {showPreviewContext, hidePreviewContext, createPreviewSession} from '../actions';

export const previewMiddleware = store => next => action => {
  if (action.type === Actions.REQUEST_FORM_PREVIEW) {
    const variables = store.getState().form.get('variables');
    if (variables && variables.findIndex(v => v.get('context') === true) > -1) {
      return store.dispatch(showPreviewContext());
    } else {
      let language = store.getState().editor.get('activeLanguage');
      let formId = store.getState().form.get('_id');
      return store.dispatch(createPreviewSession(formId, language));
    }
  } else if (action.type === Actions.REDIRECT_PREVIEW) {
    let previewUrl = store.getState().config.get('previewUrl');
    let win = window.open(`${previewUrl}/${action.sessionId}`);
    win.focus();
    return store.dispatch(hidePreviewContext());
  }
  return next(action);
}
