import React, {Component} from 'react';
import {List} from 'semantic-ui-react';
import Item, {connectItem} from './Item';
import {treeItemFactory} from '.';

const MAX_LENGTH = 55;

class TreeItem extends Item {

  createChildren(props, config) {
    return this.props.item.get('items')
      .map(itemId => this.props.items.get(itemId))
      .map(item => treeItemFactory(item, props, config));
  }

  getSubList() {
    const children = this.createChildren();
    if (children && children.size > 0) {
      return (
        <List.List>
          {children}
        </List.List>
      )
    }
    return null;
  }

  formatLabel(label) {
    if (!label || !label.trim()) {
      return '-';
    } else if (label.length > MAX_LENGTH) {
      return label.substring(0, MAX_LENGTH) + '\u2026';
    } else {
      return label;
    }
  }

  render() {
    return (
      <List.Item>
        <List.Icon name={this.props.icon} style={{float: 'initial'}}/>
        <List.Content>
          <List.Header>{this.formatLabel(this.props.item.getIn(['label', 'en']))}</List.Header>
          {this.getSubList()}
        </List.Content>
      </List.Item>);
  }
}

const TreeItemConnected = connectItem(TreeItem);

export {
  TreeItem,
  TreeItemConnected as default
};
