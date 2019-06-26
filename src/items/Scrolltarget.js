import React from 'react';
import {scrollToWhen} from 'react-redux-scroll';
import * as Actions from '../actions/constants';

const isItemActivated = (action, props) =>
  action.type === Actions.SET_ACTIVE_ITEM && action.itemId === props.itemId && !action.noScroll;

const Scrolltarget = scrollToWhen({
  pattern: isItemActivated,
  scrollOptions: {
    yMargin: 50
  },
  excludeProps: ['itemId']
})('div');

export {
  Scrolltarget as default
};
