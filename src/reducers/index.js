import {combineReducers} from 'redux';
import {editorReducer} from './editorReducer';
import {formReducer} from './formReducer';
import {configReducer} from './configReducer';

const reducers = {
  form: formReducer,
  editor: editorReducer,
  config: configReducer,
};

//export const reducer = combineReducers(reducers);

export default function createDialobComposerReducer() {
  return combineReducers(reducers);
}
