import React, {Component} from 'react';
import {Menu, Dropdown, Popup, Button} from 'semantic-ui-react';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';

class ItemTypeMenu extends Component {

  static get propTypes() {
    return {
      categoryFilter: PropTypes.func,
      itemTypeFilter: PropTypes.func,
      onSelect: PropTypes.func.isRequired
    };
  }

  render() {
    return  this.props.itemTypes.categories.filter(this.props.categoryFilter || (i => i)).map((category, ckey) =>
      <Dropdown key={ckey} item text={category.title} closeOnChange>
        <Dropdown.Menu>
        {
          category.items.filter(this.props.itemTypeFilter || (i => i)).map((item, ikey) => <Dropdown.Item key={ikey} onClick={() => this.props.onSelect(item.config)}>{item.title}</Dropdown.Item>)
        }
        </Dropdown.Menu>
      </Dropdown>
    );
  }
}

const ItemTypeMenuConnected = connect(
  state => ({
    itemTypes:  state.dialobComposer.config.itemTypes
  }), {
  }
)(ItemTypeMenu);

export {
  ItemTypeMenuConnected as default,
  ItemTypeMenu
};
