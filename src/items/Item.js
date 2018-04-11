import React, {Component} from 'react';
import {itemFactory} from '.';
import {PropTypes} from 'prop-types';
import {connect} from 'react-redux';
import {setActiveItem, addItem, changeItemType, updateItem} from '../actions';

class Item extends Component {

  createChildren(props, config) {
    return this.props.item.get('items') && this.props.item.get('items')
      .map(itemId => this.props.items.get(itemId))
      .map(item => itemFactory(item, props, config));
  }
}

function connectItem(component) {
  return connect(
    (state, props) => ({
      items: state.form && state.form.get('data'),
      active: props.item && state.editor && props.item.get('id') === state.editor.get('activeItemId'),
      get findRootItem() { return () => findRoot(this.items); },
    }),
    (dispatch, props) => ({
      setActive: () => dispatch(setActiveItem(props.item.get('id'))),
      newItem: (config, parentItemId, afterItemId) => dispatch(addItem(config, parentItemId, afterItemId)),
      setType: (config) => dispatch(changeItemType(config, props.item.get('id'))),
      setAttribute: (attribute, value) => dispatch(updateItem(props.item.get('id'), attribute, value))
    })
  )(component);
}

export {
  Item as default,
  connectItem
};