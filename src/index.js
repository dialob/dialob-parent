import React from 'react';
import {render} from 'react-dom';
import {Provider} from 'react-redux';
import {reducer} from './reducers';
import {middleware} from './middleware';
import {createStore, applyMiddleware} from 'redux';
import Immutable from 'immutable';
import App from './App';
import HTML5Backend from 'react-dnd-html5-backend'
import { DragDropContextProvider } from 'react-dnd'

const initialState = {
  config: Immutable.fromJS({
    csrf: {
      headerName: window.COMPOSER_CONFIG.csrfHeader,
      token: window.COMPOSER_CONFIG.csrf
    },
    apiUrl: window.COMPOSER_CONFIG.backend_api_url,
    formId: window.COMPOSER_CONFIG.formId,
    previewUrl: window.COMPOSER_CONFIG.filling_app_url
  })
};

const store = createStore(reducer, initialState, applyMiddleware(...middleware));

render(<DragDropContextProvider backend={HTML5Backend}><Provider store={store}><App/></Provider></DragDropContextProvider>, document.querySelector('#app'));
