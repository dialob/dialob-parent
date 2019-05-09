import React, {Component} from 'react';
import {render} from 'react-dom';
import {DialobComposer, createDialobComposerReducer, createDialobComposerMiddleware, DEFAULT_ITEM_CONFIG, DEFAULT_ITEMTYPE_CONFIG} from '../../src';
import { combineReducers, createStore, applyMiddleware } from 'redux';
import {Provider} from 'react-redux';
import HTML5Backend from 'react-dnd-html5-backend'
import { DragDropContextProvider } from 'react-dnd'
import { Button } from 'semantic-ui-react';

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

class Demo extends Component {

  constructor(props) {
    super(props);
    this.state = {
      formId: FORM_ID
    };
  }

  render () {
    return (
      <React.Fragment>
        <DialobComposer formId={this.state.formId} configuration={DIALOB_COMPOSER_CONFIG} />
      </React.Fragment>
    )
  }
  //   <Button style={{position: 'absolute', zIndex: 9999999}} onClick={() => this.setState({formId: 'test2'})} >TEST2</Button>
}

render(<DragDropContextProvider backend={HTML5Backend}><Provider store={store}><Demo/></Provider></DragDropContextProvider>, document.querySelector('#app'))
