import React, {Component} from 'react';
import {render} from 'react-dom';
import {DialobComposer, createDialobComposerReducer, createDialobComposerMiddleware, DEFAULT_ITEM_CONFIG, DEFAULT_ITEMTYPE_CONFIG} from '../../src';
import { combineReducers, createStore, applyMiddleware } from 'redux';
import {Provider} from 'react-redux';
import HTML5Backend from 'react-dnd-html5-backend'
import { DragDropContextProvider } from 'react-dnd'

const FORM_ID = window.location.href.substr(window.location.href.lastIndexOf('/') + 1);

const DIALOB_COMPOSER_CONFIG = {
  transport: {
    csrf: {
      headerName: null,
      token: null
    },
    apiUrl: 'http://localhost:8081/webapi',
    previewUrl: 'http://localhost:8081/webapi'
  },
  itemEditors: DEFAULT_ITEM_CONFIG,
  itemTypes: DEFAULT_ITEMTYPE_CONFIG,
  closeHandler: () => window.alert('CLOSE!')
};

const reducers = {
  dialobComposer: createDialobComposerReducer()
};

const reducer = combineReducers(reducers);

const store = createStore(reducer, applyMiddleware(...createDialobComposerMiddleware()));

class Demo extends Component {
  render () {
    return (
      <DialobComposer formId={FORM_ID} configuration={DIALOB_COMPOSER_CONFIG} />
    )
  }
}

render(<DragDropContextProvider backend={HTML5Backend}><Provider store={store}><Demo/></Provider></DragDropContextProvider>, document.querySelector('#demo'))
