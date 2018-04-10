import React, {Component} from 'react';
import {Segment, Menu, Icon, Input, Button} from 'semantic-ui-react';
import {connect} from 'react-redux';
import {findRoot} from '../helpers/utils';
import {setActivePage} from '../actions';
import {itemFactory} from '../items';

class Editor extends Component {

  createChildren(activePage, props, config) {
    return activePage.get('items')
      .map(itemId => this.props.items.get(itemId))
      .map(item => itemFactory(item, props, config));
  }

  render() {
    const rootItem = this.props.findRootItem();
    const activePageId = this.props.activePageId ? this.props.activePageId: rootItem.getIn(['items', 0]);
    const activePage = this.props.items.get(activePageId);
    const pages = rootItem ? rootItem.get('items')
                        .map(itemId => this.props.items.get(itemId))
                        .map((item, index) =><Menu.Item onClick={() => this.props.setActivePage(item.get('id'))} key={index} active={item.get('id') === activePageId}>{item.getIn(['label', 'en'])}</Menu.Item>) : null;
    return (
      <Segment basic>
        <Menu tabular>
          {pages}
          <Menu.Menu position='right'>
            <Menu.Item>
              <Button icon='add'/>
            </Menu.Item>
          </Menu.Menu>
        </Menu>
        {this.createChildren(activePage, {parentItemId: activePageId})}
      </Segment>
    );
  }
}

const EditorConnected = connect(
  state => ({
    items: state.form && state.form.get('data'),
    activePageId: state.editor && state.editor.get('activePageId'),
    get findRootItem() { return () => findRoot(this.items); }
  }),
  {
    setActivePage
  }
)(Editor);

export {
  EditorConnected as default,
  Editor
};
