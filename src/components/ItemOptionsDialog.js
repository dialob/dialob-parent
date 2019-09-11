import React, {Component} from 'react';
import {Modal, Button, Tab} from 'semantic-ui-react';
import {connect} from 'react-redux';
import {hideItemOptions} from '../actions';
import Styleclasses from './options/Styleclasses';
import Description from './options/Description';
import Choices from './options/Choices';
import ItemProps from './options/ItemProps';
import {CHOICE_ITEM_TYPES, DEFAULT_ITEM_CONFIG} from '../defaults';

class ItemOptionsDialog extends Component {

  findItemConfig() {
    const config = this.props.itemEditors || DEFAULT_ITEM_CONFIG;
    const item = this.props.getItem();
    const itemConfig = config.items.find(e => e.matcher(item));
    return itemConfig;
  }

  render() {
    if (this.props.itemOptions) {
      let tabs = [
        {menuItem: 'Description', render: () => <Tab.Pane><Description item={this.props.getItem()} /></Tab.Pane>},
        {menuItem: 'Style classes', render: () => <Tab.Pane><Styleclasses item={this.props.getItem()} /></Tab.Pane>},
        {menuItem: 'Properties', render: () => <Tab.Pane><ItemProps item={this.props.getItem()} /></Tab.Pane>},
      ];

      if (CHOICE_ITEM_TYPES.findIndex(t => t === this.props.getItem().get('type')) > -1) {
        tabs.unshift({menuItem: 'Choices', render: () => <Tab.Pane><Choices item={this.props.getItem()} /></Tab.Pane>});
      }

      const itemConfig = this.findItemConfig();
      if (itemConfig && itemConfig.propEditors) {
        itemConfig.propEditors.forEach(e => {
             tabs.push({menuItem: e.name, render: () => <Tab.Pane><e.editor item={this.props.getItem()} /></Tab.Pane>});
          }
        );
      }

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
    itemOptions: state.dialobComposer.editor && state.dialobComposer.editor.get('itemOptions'),
    itemEditors:  state.dialobComposer.config && state.dialobComposer.config.itemEditors,
    get getItem() { return () => state.dialobComposer.form && state.dialobComposer.form.getIn(['data', this.itemOptions.get('itemId')]); }
  }), {
    hideItemOptions
  }
)(ItemOptionsDialog);

export {
  ItemOptionsDialogConnected as default,
  ItemOptionsDialog
};
