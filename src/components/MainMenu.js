import React, {Component} from 'react';
import {Container, Menu, Icon} from 'semantic-ui-react';

class MainMenu extends Component {
  render() {
    return (
      <Container>
        <Menu fixed='top'>
          <Menu.Item header>
            Dialob Composer
          </Menu.Item>
          <Menu.Item>
            Versioning
          </Menu.Item>
          <Menu.Item>
            Translations
          </Menu.Item>
          <Menu.Item>
            Variables
          </Menu.Item>
          <Menu.Item>
            Lists
          </Menu.Item>
          <Menu.Item>
            Options
          </Menu.Item>
          <Menu.Menu position='right'>
              <Menu.Item><Icon name='eye' /> Preview</Menu.Item>
          </Menu.Menu>
        </Menu>
      </Container>
    );
  }
}

export default MainMenu;