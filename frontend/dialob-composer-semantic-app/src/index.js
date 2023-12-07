import React from 'react';
import ReactDOM from 'react-dom';
import HTML5Backend from 'react-dnd-html5-backend';
import { DndProvider } from 'react-dnd';
import {combineReducers, applyMiddleware, createStore} from 'redux';
import {Provider} from 'react-redux';
import {DialobComposer, createDialobComposerReducer, createDialobComposerMiddleware, DEFAULT_ITEM_CONFIG, DEFAULT_ITEMTYPE_CONFIG} from '@resys/dialob-composer';

const renderDialobComposer = (target, appConfig) => {
  console.log('Render', appConfig);

  const FORM_ID = appConfig.formId;

  const DIALOB_COMPOSER_CONFIG = {
  transport: {
    csrf: {
      headerName: appConfig.csrfHeader,
      token: appConfig.csrf
    },
    apiUrl: appConfig.backend_api_url,
    previewUrl: appConfig.filling_app_url,
    tenantId: appConfig.tenantId,
  },
  itemEditors: DEFAULT_ITEM_CONFIG,
  itemTypes: DEFAULT_ITEMTYPE_CONFIG,
  closeHandler : () => window.location.href = window.appConfig.adminAppUrl
};

const reducers = {
  dialobComposer: createDialobComposerReducer()
};

const reducer = combineReducers(reducers);

const store = createStore(reducer, applyMiddleware(...createDialobComposerMiddleware()));

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
