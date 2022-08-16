import React from 'react';
import HTML5Backend from 'react-dnd-html5-backend';
import { DndProvider } from 'react-dnd';
import {combineReducers, applyMiddleware, createStore} from 'redux';
import {Provider} from 'react-redux';
import {DialobComposer, createDialobComposerReducer, createDialobComposerMiddleware, DEFAULT_ITEM_CONFIG, DEFAULT_ITEMTYPE_CONFIG} from '@resys/dialob-composer';

console.log('XYZ');

const FORM_ID = window.COMPOSER_CONFIG.formId;

const DIALOB_COMPOSER_CONFIG = {
  transport: {
    csrf: {
      headerName: window.COMPOSER_CONFIG.csrfHeader,
      token: window.COMPOSER_CONFIG.csrf
    },
    apiUrl: window.COMPOSER_CONFIG.backend_api_url,
    previewUrl: window.COMPOSER_CONFIG.filling_app_url,
    tenantId: window.COMPOSER_CONFIG.tenantId,
  },
  itemEditors: DEFAULT_ITEM_CONFIG,
  itemTypes: DEFAULT_ITEMTYPE_CONFIG,
  closeHandler : () => window.location.href = window.COMPOSER_CONFIG.adminAppUrl
};

const reducers = {
  dialobComposer: createDialobComposerReducer()
};

const reducer = combineReducers(reducers);

const store = createStore(reducer, applyMiddleware(...createDialobComposerMiddleware()));

console.log('PReRender');


const renderDialobComposer = (target) => {
  ReactDOM.render(
    <DndProvider backend={HTML5Backend}>
        <Provider store={store}>
        <DialobComposer formId={FORM_ID} configuration={DIALOB_COMPOSER_CONFIG}/>
        </Provider>
      </DndProvider>
  , target);
};

// @ts-ignore
window.renderDialobComposer = renderDialobComposer;


const root = ReactDOM.createRoot(document.getElementById('app'));
