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
  hover(props, monitor, component) {
    if (!component) {
      return;
    }
    //console.log('C', findDOMNode(component).classList);
  },

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

    /*
    console.log('Props', props);
    console.log(`Dragged item: ${monitor.getItem().id}`);
    console.log(`Hovered item: ${props.item.get('id')}`);
    console.log(`Hovered item's parent: ${props.parent.get('id')}`);
    console.log(`Hovered item's type: ${props.item.get('type')}`);
    console.log(`Hovered item's parent type: ${props.parent.get('type')}`);

    console.log(`Can drop above`, canContain(props.parent.get('type'), monitor.getItem().itemType));
    console.log(`Can drop into`, canContain(props.item.get('type'), monitor.getItem().itemType));
    */

    if (canContain(props.parent.get('type'), monitor.getItem().itemType)) {
      // Dropping above
      console.log('Dropping above');
      props.moveItem(dragIndex, hoverIndex, dragParent.get('id'), hoverParent.get('id'), monitor.getItem().id);
    } else if (canContain(props.item.get('type'), monitor.getItem().itemType)) {
      // Dropping into
      console.log('Dropping into');
      props.moveItem(dragIndex, 0, dragParent.get('id'), props.item.get('id'), monitor.getItem().id);
    }
  }
};

class TreeItem extends Item {

  createChildren(props, config) {
    return this.props.item.get('items') && this.props.item.get('items')
      .map(itemId => this.props.items.get(itemId))
      .map((item, index) => treeItemFactory(item, Object.assign(props, {index}), config));
  }

  getSubList() {
    const parent = this.props.item;
    const children = this.createChildren({pageId: this.props.pageId, parent, moveItem: this.props.moveItem, isPage: false});
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
    const {connectDragSource, connectDropTarget, isOver} = this.props;
    return (
      <Ref innerRef={node => {connectDropTarget(node); connectDragSource(node);}}>
        <List.Item className={classnames({'composer-drag-above': isOver})}>
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
    (DropTarget('item', itemTarget, (connect, monitor) => ({
      connectDropTarget: connect.dropTarget(),
      isOver: monitor.isOver({shallow: true})
    }))
      (connectItem(TreeItem)));

export {
  TreeItem,
  TreeItemConnected as default
};
