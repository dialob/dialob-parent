import React, {Component} from 'react';
import {Dropdown} from 'semantic-ui-react';
import ItemTypeMenu from './ItemTypeMenu';
import {connect} from 'react-redux';
import {addItem, showItemOptions, copyItem} from '../actions';
import PropTypes from 'prop-types';

class ItemMenu extends Component {

  static get propTypes() {
    return {
      item: PropTypes.object.isRequired,
      parentItemId: PropTypes.string.isRequired,
      onDelete: PropTypes.func.isRequired
    };
  }

  render() {
    return (
      <Dropdown icon='content' lazyLoad>
        <Dropdown.Menu>
          <Dropdown.Item icon='options' text='Options...' onClick={() => this.props.showItemOptions(this.props.item.get('id'))}/>
          <Dropdown.Item disabled={!!this.props.formTag} icon='remove' text='Delete' onClick={() => this.props.onDelete()} />
          <Dropdown.Item disabled={!!this.props.formTag} icon='copy' text='Duplicate' onClick={() => this.props.copyItem(this.props.item.get('id'))} />
          <Dropdown.Divider />
          <Dropdown item text='Insert new'>
              <Dropdown.Menu>
                <ItemTypeMenu onSelect={(config) => this.props.addItem(config, this.props.parentItemId, this.props.item.get('id'))} />
              </Dropdown.Menu>
          </Dropdown>
        </Dropdown.Menu>
      </Dropdown>
    );
  }
}

const ItemMenuConnected = connect(
  state => ({
    items: state.dialobComposer.form && state.dialobComposer.form.get('data'),
    formTag: state.dialobComposer.form.get('_tag'),
  }),
  {
    addItem,
    showItemOptions,
    copyItem
  }
)(ItemMenu);

export {
  ItemMenu,
  ItemMenuConnected as default
}
