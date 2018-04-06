import {combineReducers} from 'redux';
import {editorReducer} from './editorReducer';
import {formReducer} from './formReducer';

const reducers = {
  form: formReducer,
  editor: editorReducer
};

export const reducer = combineReducers(reducers);
