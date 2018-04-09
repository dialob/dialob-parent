import * as Actions from './constants';

export function setActiveItem(itemId) {
  return {
    type: Actions.SET_ACTIVE_ITEM,
    itemId
  };
}

export function setActivePage(itemId) {
  return {
    type: Actions.SET_ACTIVE_PAGE,
    itemId
  };
}