import React, {Component} from 'react';
import {render} from 'react-dom';
import {DialobComposer, Item, connectItem, createDialobComposerReducer, createDialobComposerMiddleware, DEFAULT_ITEM_CONFIG, DEFAULT_ITEMTYPE_CONFIG} from '../../src';
import { combineReducers, createStore, applyMiddleware } from 'redux';
import {Provider} from 'react-redux';
import HTML5Backend from 'react-dnd-html5-backend'
import { DragDropContextProvider } from 'react-dnd'
import {Segment, Grid} from 'semantic-ui-react';
import SurveyGroup from './SurveyGroup';
import {postSurveyGroupCreate} from './surveyGroupCreator';

const FORM_ID = window.location.href.substr(window.location.href.lastIndexOf('/') + 1);

const CUSTOM_ITEM_EDITORS = {
  items: [
    {
      matcher: item => item.get('type') === 'surveygroup',
      component: SurveyGroup,
      props: {
        icon: 'braille',
        placeholder: 'Survey group label'
      }
    }
  ].concat(DEFAULT_ITEM_CONFIG.items)
};

const CUSTOM_ITEM_TYPES = {
  categories: [
    {
      title: 'Nesa Survey',
      type: 'structure',
      items: [
        {
          title: 'Survey group',
          config: {
            type: 'surveygroup',
            props: {
              nesa: '1'
            }
          }
        }
      ]
    }
  ].concat(DEFAULT_ITEMTYPE_CONFIG)
};

const DIALOB_COMPOSER_CONFIG = {
  transport: {
    csrf: {
      headerName: null,
      token: null
    },
    apiUrl: 'http://localhost:8081/webapi',
    previewUrl: 'http://localhost:8081/webapi'
  },
  itemEditors: CUSTOM_ITEM_EDITORS,
  itemTypes: CUSTOM_ITEM_TYPES,
  postAddItem: (dispatch, action, lastItem) => {
    if (lastItem.get('type') === 'surveygroup' && lastItem.getIn(['props', 'nesa']) === '1') {
      postSurveyGroupCreate(dispatch, action, lastItem);
    }
  }
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
