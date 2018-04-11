import React, {Component} from 'react';
import {Menu, Dropdown, Popup, Button} from 'semantic-ui-react';
import {DEFAULT_ITEMTYPE_CONFIG} from '../defaults';
import PropTypes from 'prop-types';

class ItemTypeMenu extends Component {

  static get propTypes() {
    return {
      categoryFilter: PropTypes.func,
      onSelect: PropTypes.func.isRequired
    };
  }

  render() {
    return  DEFAULT_ITEMTYPE_CONFIG.categories.filter(this.props.categoryFilter || (i=> i)).map((category, ckey) =>
      <Dropdown key={ckey} item text={category.title}>
        <Dropdown.Menu>
        {
          category.items.map((item, ikey) => <Dropdown.Item key={ikey} onClick={() => this.props.onSelect(item.config)}>{item.title}</Dropdown.Item>)
        }
        </Dropdown.Menu>
      </Dropdown>
    );
  }
}

export default ItemTypeMenu;
