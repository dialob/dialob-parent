import React, {Component} from 'react';
import {Menu, Dropdown, Popup, Button} from 'semantic-ui-react';

class AddItemMenu extends Component {
  render() {
    return (
      <Popup trigger={<Button circular icon='add' />} on='click'>
        <Menu secondary vertical>
          <Menu.Item>
            <Menu.Header>Add new</Menu.Header>
          </Menu.Item>
          <Dropdown item text='Inputs'>
            <Dropdown.Menu>
              <Dropdown.Item>Text</Dropdown.Item>
              <Dropdown.Item>Text box</Dropdown.Item>
              <Dropdown.Item>Decimal</Dropdown.Item>
              <Dropdown.Item>Integer</Dropdown.Item>
            </Dropdown.Menu>
          </Dropdown>
          <Dropdown item text='Structure'>
            <Dropdown.Menu>
              <Dropdown.Item>Group</Dropdown.Item>
              <Dropdown.Item>Survey</Dropdown.Item>
              <Dropdown.Item>Multi-row</Dropdown.Item>
            </Dropdown.Menu>
          </Dropdown>
        </Menu>
      </Popup>
    );
  }
}

export default AddItemMenu;
