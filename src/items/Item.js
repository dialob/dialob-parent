import React, {Component} from 'react';
import {itemFactory} from '.';
import {PropTypes} from 'prop-types';
import {connect} from 'react-redux';
import {setActiveItem} from '../actions';

class Item extends Component {

  createChildren(props, config) {
    return this.props.item.get('items')
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
      setActive: () => dispatch(setActiveItem(props.item.get('id')))
    })
  )(component);
}

export {
  Item as default,
  connectItem
};