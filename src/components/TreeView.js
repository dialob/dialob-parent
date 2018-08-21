import React, {Component} from 'react';
import {connect} from 'react-redux';
import {Menu, List} from 'semantic-ui-react';
import {findRoot} from '../helpers/utils';
import {TreeItem} from '../items';
import {moveItem} from '../actions';

class TreeView extends Component {

  moveItem(dragIndex, hoverIndex, dragParent, hoverParent, itemId) {
    this.props.moveItem(dragIndex, hoverIndex, dragParent, hoverParent, itemId);
  }

  render() {
    const rootItem = this.props.findRootItem();
    const parent = rootItem;
    const treeItems = rootItem && rootItem.get('items') && rootItem.get('items')
            .map(itemId => this.props.items.get(itemId))
            .map((page, index) => <TreeItem index={index} parent={parent} isPage={true} id={page.get('id')} moveItem={this.moveItem.bind(this)} key={page.get('id')} item={page.set('type', 'page')} icon='folder' pageId={page.get('id')}/>);
    return (
      <Menu vertical fixed='left' style={{marginTop: this.props.marginTop, width: this.props.menuWidth, overflowY: 'scroll'}}>
        <Menu.Item>
          <List size='small'>
             {treeItems}
          </List>
        </Menu.Item>
      </Menu>);
  }
}

const TreeViewConnected = connect(
  state => ({
    items: state.form && state.form.get('data'),
    get findRootItem() { return () => findRoot(this.items); }
  }),
  {
    moveItem
  }
)(TreeView);

export {
  TreeViewConnected as default,
  TreeView
};

