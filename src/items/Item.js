import React, {Component} from 'react';
import {itemFactory} from '.';
import {PropTypes} from 'prop-types';
import {connect} from 'react-redux';

class Item extends Component {

  createChildren(props, config) {
    return this.props.item.get('items')
      .map(itemId => this.props.items.get(itemId))
      .map(item => itemFactory(item, props, config));
  }
}

function connectItem(component) {
  return connect(
    state => ({
      items: state.form && state.form.get('data'),
      get findRootItem() { return () => findRoot(this.items); }
    }),
    {}
  )(component);
}

export {
  Item as default,
  connectItem
};