import React from 'react';
import {render} from 'react-dom';
import {Provider} from 'react-redux';
import {reducer} from './reducers';
import {middleware} from './middleware';
import {createStore, applyMiddleware} from 'redux';
import Immutable from 'immutable';
import App from './App';

const initialState = Immutable.Map();

const store = createStore(reducer, initialState, applyMiddleware(...middleware));

render(<Provider store={store}><App/></Provider>, document.querySelector('#app'));
