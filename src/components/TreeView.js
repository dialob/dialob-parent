import React, {Component} from 'react';
import {connect} from 'react-redux';
import {Menu, List} from 'semantic-ui-react';
import {findRoot} from '../helpers/utils';
import {treeItemFactory, TreeItem} from '../items';

class TreeView extends Component {
  render() {
    const rootItem = this.props.findRootItem();
    const treeItems = rootItem && rootItem.get('items') && rootItem.get('items')
            .map(itemId => this.props.items.get(itemId))
            .map(page => <TreeItem key={page.get('id')} item={page} icon='folder' />);
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
  {}
)(TreeView);

export {
  TreeViewConnected as default,
  TreeView
};

