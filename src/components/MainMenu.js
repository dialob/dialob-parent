import React, {Component} from 'react';
import {Container, Menu, Icon, Dropdown} from 'semantic-ui-react';
import {connect} from 'react-redux';
import * as Defaults from '../defaults';
import {setActiveLanguage} from '../actions';

class MainMenu extends Component {

  getLanguages() {
    return Defaults.LANGUAGES.map((lang, i) =>
      <Dropdown.Item key={i} active={lang.code === this.props.language} onClick={() => this.props.setActiveLanguage(lang.code)}>{lang.name}</Dropdown.Item>);
  }

  getLanguageName(code) {
    return Defaults.LANGUAGES.find(lang => lang.code === code).name;
  }

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
              <Dropdown item text={this.getLanguageName(this.props.language)}>
                <Dropdown.Menu>
                  {this.getLanguages()}
                </Dropdown.Menu>
              </Dropdown>
              <Menu.Item><Icon name='eye' /> Preview</Menu.Item>
          </Menu.Menu>
        </Menu>
      </Container>
    );
  }
}

const MainMenuConnected = connect(
  state => ({
    language: (state.editor && state.editor.get('activeLanguage')) || Defaults.FALLBACK_LANGUAGE
  }),
  {
    setActiveLanguage
  }
)(MainMenu);

export {
  MainMenuConnected as default,
  MainMenu
};
