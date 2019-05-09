import React, {Component} from 'react';
import {List, Ref} from 'semantic-ui-react';
import Item, {connectItem} from './Item';
import {treeItemFactory} from '.';
import md_strip_tags from 'remove-markdown';
import classnames from 'classnames';
import { DragSource, DropTarget } from 'react-dnd'
import {findDOMNode} from 'react-dom';
import {canContain} from '../defaults';

const MAX_LENGTH = 55;

const itemSource = {
  beginDrag(props) {
    return {
      id: props.id,
      index: props.index,
      parent: props.parent,
      isPage: props.isPage,
      itemType: props.item.get('type')
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
        && dragParent.get('id') === hoverParent.get('id')
    ) {
      return;
    }
    if (monitor.didDrop()) {
      return;
    }

    if (canContain(props.parent.get('type'), monitor.getItem().itemType)) {
      // Dropping aside
      const boundingRect = findDOMNode(component).getBoundingClientRect();
      const midY = (boundingRect.bottom - boundingRect.top) / 2;
      const y = monitor.getClientOffset().y - boundingRect.top;
      const targetIndex = y >= midY ? hoverIndex + 1 : hoverIndex;
      props.moveItem(dragIndex, targetIndex, dragParent.get('id'), hoverParent.get('id'), monitor.getItem().id);
    } else if (canContain(props.item.get('type'), monitor.getItem().itemType)) {
      // Dropping into
      props.moveItem(dragIndex, 0, dragParent.get('id'), props.item.get('id'), monitor.getItem().id);
    }
  }
};

class TreeItem extends Item {

  constructor(props) {
    super(props);
    this.node = null;
  }

  createChildren(props, config) {
    return this.props.item.get('items') && this.props.item.get('items')
      .map(itemId => this.props.getItemById(itemId))
      .map((item, index) => treeItemFactory(item, Object.assign(props, {index}), config));
  }

  getSubList() {
    const parent = this.props.item;
    const children = this.createChildren({pageId: this.props.pageId, parent, moveItem: this.props.moveItem, isPage: false, getItemById: this.props.getItemById}, this.props.itemEditors);
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
    const {connectDragSource, connectDropTarget, isOver, clientOffset, setTreeCollapsed, treeCollapsed} = this.props;
    let dragClass = null;
    if (isOver) {
      const boundingRect = this.node.getBoundingClientRect();
      const midY = (boundingRect.bottom - boundingRect.top) / 2;
      const y = this.props.clientOffset.y - boundingRect.top;
      if (y < midY) {
        dragClass = 'composer-drag-above';
      }
      if (y >= midY) {
        dragClass = 'composer-drag-below';
      }
    }
    return (
      <Ref innerRef={node => {connectDropTarget(node); connectDragSource(node); this.node = node;}}>
        <List.Item className={dragClass}>
          {
            this.props.treeCollapsible &&
              <List.Icon name={treeCollapsed ? 'caret right' : 'caret down'} style={{float: 'initial'}} onClick={() => setTreeCollapsed(!treeCollapsed)}/>
          }
          <List.Icon name={this.props.icon} style={{float: 'initial'}}/>
          <List.Content>
            <List.Header className={classnames({'composer-active': this.props.active})}>{this.formatLabel(this.preprocessLabel(this.props.item.getIn(['label', this.props.language])), this.props.item.get('id'))}</List.Header>
            {!treeCollapsed && this.getSubList()}
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
    (DropTarget('item', itemTarget, (connect, monitor) => ({
      connectDropTarget: connect.dropTarget(),
      isOver: monitor.isOver({shallow: true}),
      clientOffset: monitor.getClientOffset()
    }))
      (connectItem(TreeItem)));

export {
  TreeItem,
  TreeItemConnected as default
};
