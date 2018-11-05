import {DialobActions} from '../../src';

export const postSurveyGroupCreate = (dispatch, action, lastItem) => {
  dispatch(DialobActions.addItem({type: 'survey', label: {fi: 'Nykytila', en: 'Current'}}, lastItem.get('id')));
  dispatch(DialobActions.addItem({type: 'survey', label: {fi: 'Tavoitetila', en: 'Target'}}, lastItem.get('id')));
  dispatch(DialobActions.addItem({type: 'survey', label: {fi: 'Auditoija', en: 'Auditor'}}, lastItem.get('id')));
  dispatch(DialobActions.createValueset(lastItem.get('id'),
  [
    { id: 's1', label: { en: ''} },
    { id: 's2', label: { en: ''} },
    { id: 's3', label: { en: ''} },
    { id: 's4', label: { en: ''} },
    { id: 's5', label: { en: ''} },
  ]
  ));
};
