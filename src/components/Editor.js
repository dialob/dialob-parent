import React, {Component} from 'react';
import {Segment, Menu, Icon, Input, Button} from 'semantic-ui-react';
import {connect} from 'react-redux';
import {findRoot} from '../helpers/utils';
//import AddItemMenu from '../components/AddItemMenu';
import {itemFactory} from '../items';

class Editor extends Component {

  createChildren(activePage, props, config) {
    return activePage.get('items')
      .map(itemId => this.props.items.get(itemId))
      .map(item => itemFactory(item, props, config));
  }

  render() {
    const rootItem = this.props.findRootItem();
    const activePageIndex = 0;
    const activePage = this.props.items.get(rootItem.getIn(['items', activePageIndex]));
    const pages = rootItem ? rootItem.get('items')
                        .map(itemId => this.props.items.get(itemId))
                        .map((item, index) =><Menu.Item key={index} active={activePageIndex === index}>{item.getIn(['label', 'en'])}</Menu.Item>) : null;
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
        {this.createChildren(activePage)}
      </Segment>
    );
  }
}

const EditorConnected = connect(
  state => ({
    items: state.form && state.form.get('data'),
    get findRootItem() { return () => findRoot(this.items); }
  }),
  {}
)(Editor);

export {
  EditorConnected as default,
  Editor
};
