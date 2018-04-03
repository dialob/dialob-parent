import {combineReducers} from 'redux';
import {editorReducer} from './editorReducer';

const reducers = {
  form: editorReducer
};

export const reducer = combineReducers(reducers);
