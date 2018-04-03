import React, {Component} from 'react';
import {Segment, Menu, Icon, Input, Button} from 'semantic-ui-react';
import {connect} from 'react-redux';
import {findRoot} from '../helpers/utils';
import AddItemMenu from '../components/AddItemMenu';

class Editor extends Component {
  render() {
    const rootItem = this.props.findRootItem();
    const activePageIndex = 0;
    const pages = rootItem ? rootItem.get('items').map(itemId => this.props.items.get(itemId)).map((item, index) =><Menu.Item key={index} active={activePageIndex === index}>{item.getIn(['label', 'en'])}</Menu.Item>) : null;
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

        <Input fluid icon='square outline' iconPosition='left' placeholder='Group title' className='top attached' />
        <Segment attached='bottom' raised>
          <Input fluid icon='square outline' iconPosition='left' placeholder='Group title' className='top attached' />
          <Segment attached='bottom' raised>
            <p><Input fluid icon='font' iconPosition='left' placeholder='Text label' /></p>
            <p><Input fluid icon='calendar' iconPosition='left' placeholder='Date label' /></p>
            <p><Input fluid icon='caret down' iconPosition='left' placeholder='Choice label' /></p>
            <AddItemMenu />
          </Segment>
          <AddItemMenu />
        </Segment>
        <AddItemMenu />
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
