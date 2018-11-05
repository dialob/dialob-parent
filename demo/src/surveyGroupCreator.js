import {DialobActions} from '../../src';

export const postSurveyGroupCreate = (dispatch, action, lastItem) => {
  dispatch(DialobActions.addItem({type: 'survey', label: {en: 'One'}}, lastItem.get('id')));
  dispatch(DialobActions.addItem({type: 'survey', label: {en: 'Two'}}, lastItem.get('id')));
  dispatch(DialobActions.addItem({type: 'survey', label: {en: 'Three'}}, lastItem.get('id')));
  dispatch(DialobActions.createValueset(lastItem.get('id'),
  [
    { id: 's1', label: { en: 'a'} },
    { id: 's2', label: { en: 'b'} },
    { id: 's3', label: { en: 'c'} },
    { id: 's4', label: { en: 'd'} },
    { id: 's5', label: { en: 'e'} },
  ]
  ));
};
