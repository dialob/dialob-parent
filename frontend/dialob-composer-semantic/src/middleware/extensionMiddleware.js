import * as Actions from '../actions/constants';
import {} from '../actions';

export const extensionMiddleware = store => {
  return next => action => {
    let config = store.getState().dialobComposer.config;

    if (action.type === Actions.CLOSE_EDITOR) {
      config.closeHandler && config.closeHandler();
    }

    // PRE callbacks
    if (action.type === Actions.ADD_ITEM && config.preAddItem) {
      action = config.preAddItem(store.dispatch, action);
    }

    let result = next(action);
    // POST callbacks
    if (action.type === Actions.ADD_ITEM && config.postAddItem) {
      config.postAddItem(store.dispatch, action, store.getState().dialobComposer.form.getIn(['metadata', 'composer', 'transient', 'lastItem']));
    }
    return result;
  }
}
