import * as Actions from '../actions/constants';
import FileSaver from 'file-saver';

export const downloadMiddleware = store => next => action => {
	if (action.type === Actions.DOWNLOAD_FORM) {
    const form = store.getState().dialobComposer.form.toJS();
    const json = JSON.stringify(form, null,  2);
    const blob = new Blob([json], {type: 'application/json;charset=utf-8'});
    FileSaver.saveAs(blob, `${form._id}.json`);
  }
	return next(action);
}
