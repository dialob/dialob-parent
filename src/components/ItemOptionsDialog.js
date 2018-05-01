import React, {Component} from 'react';
import {Modal, Button, Tab} from 'semantic-ui-react';
import {connect} from 'react-redux';
import {hideItemOptions} from '../actions';
import ItemProps from './options/ItemProps';
import Styleclasses from './options/Styleclasses';
import Description from './options/Description';
import Choices from './options/Choices';

class ItemOptionsDialog extends Component {

  render() {
    if (this.props.itemOptions) {

      const tabs = [
        {menuItem: 'Properties', render: () => <Tab.Pane><ItemProps item={this.props.getItem()} /></Tab.Pane>},
        {menuItem: 'Style classes', render: () => <Tab.Pane><Styleclasses item={this.props.getItem()} /></Tab.Pane>},
        {menuItem: 'Description', render: () => <Tab.Pane><Description item={this.props.getItem()} /></Tab.Pane>},
        {menuItem: 'Choices', render: () => <Tab.Pane><Choices item={this.props.getItem()} /></Tab.Pane>}
      ];

      return (
        <Modal open>
          <Modal.Header>Item options for <em>{this.props.getItem().get('id')}</em></Modal.Header>
          <Modal.Content scrolling>
            <Tab panes={tabs} />
          </Modal.Content>
          <Modal.Actions>
            <Button primary onClick={() => this.props.hideItemOptions()}>OK</Button>
          </Modal.Actions>
        </Modal>
      );
    } else {
      return null;
    }
  }
}

const ItemOptionsDialogConnected = connect(
  state => ({
    itemOptions: state.editor && state.editor.get('itemOptions'),
    get getItem() { return () => state.form && state.form.getIn(['data', this.itemOptions]); }
  }), {
    hideItemOptions
  }
)(ItemOptionsDialog);

export {
  ItemOptionsDialogConnected as default,
  ItemOptionsDialog
};
