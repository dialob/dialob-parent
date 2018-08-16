import React, {Component} from 'react';
import {List, Ref} from 'semantic-ui-react';
import Item, {connectItem} from './Item';
import {treeItemFactory} from '.';
import md_strip_tags from 'remove-markdown';
import classnames from 'classnames';
import { DragSource, DropTarget } from 'react-dnd'

const MAX_LENGTH = 55;

const itemSource = {
  beginDrag(props) {
    return {
      id: props.id,
      index: props.index,
      parent: props.parent,
      isPage: props.isPage
    }
  }
};

const itemTarget = {
  drop(props, monitor, component) {
    const dragIndex = monitor.getItem().index;
    const dragParent = monitor.getItem().parent;
    const hoverIndex = props.index;
    const hoverParent = props.parent;

    if (dragIndex === hoverIndex
        && dragParent === hoverParent
    ) {
      return;
    }

    props.moveItem(
      dragIndex, hoverIndex, dragParent, hoverParent
    );
  }
};

class TreeItem extends Item {

  createChildren(props, config) {
    return this.props.item.get('items') && this.props.item.get('items')
      .map(itemId => this.props.items.get(itemId))
      .map((item, index) => treeItemFactory(item, Object.assign(props, {index}), config));
  }

  getSubList() {
    const parent = this.props.item.get('id');
    const children = this.createChildren({pageId: this.props.pageId, parent, moveItem: this.props.moveItem});
    if (children && children.size > 0) {
      return (
        <List.List>
          {children}
        </List.List>
      )
    }
    return null;
  }

  preprocessLabel(label) {
    if (this.props.item.get('type') === 'note') {
      return label && md_strip_tags(label);
    } else {
      return label;
    }
  }

  formatLabel(label, id) {
    if (!label || !label.trim()) {
      return <em>{id}</em>;
    } else if (label.length > MAX_LENGTH) {
      return label.substring(0, MAX_LENGTH) + '\u2026';
    } else {
      return label;
    }
  }

  render() {
    const {connectDragSource, connectDropTarget} = this.props;
    return (
      <Ref innerRef={node => {connectDropTarget(node); connectDragSource(node);}}>
        <List.Item >
          <List.Icon name={this.props.icon} style={{float: 'initial'}}/>
          <List.Content>
            <List.Header className={classnames({'composer-active': this.props.active})}>{this.formatLabel(this.preprocessLabel(this.props.item.getIn(['label', 'en'])), this.props.item.get('id'))}</List.Header>
            {this.getSubList()}
          </List.Content>
        </List.Item>
      </Ref>
    );
  }
}

const TreeItemConnected =
  DragSource('item', itemSource, (connect, monitor) =>
    ({
      connectDragSource: connect.dragSource(),
      isDragging: monitor.isDragging
    }))
    (DropTarget('item', itemTarget, connect => ({
    connectDropTarget: connect.dropTarget()
    }))
      (connectItem(TreeItem)));

export {
  TreeItem,
  TreeItemConnected as default
};
