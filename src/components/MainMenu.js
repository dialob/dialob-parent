import React, {Component} from 'react';
import {Container, Menu, Icon, Dropdown, Loader} from 'semantic-ui-react';
import {connect} from 'react-redux';
import * as Defaults from '../defaults';
import {setActiveLanguage, showFormOptions, showVariables, requestPreview, downloadForm, showValueSets} from '../actions';
import StatusIndicator from './StatusIndicator';
import * as Status from '../helpers/constants';

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
          <Menu.Item disabled>
            Versioning
          </Menu.Item>
          <Menu.Item disabled>
            Translations
          </Menu.Item>
          <Menu.Item onClick={() => this.props.showVariables()}>
            Variables
          </Menu.Item>
          <Menu.Item onClick={() => this.props.showValueSets()}>
            Lists
          </Menu.Item>
          <Menu.Item onClick={() => this.props.showFormOptions()}>
            Options
          </Menu.Item>
          <Menu.Menu position='right'>
              <Menu.Item onClick={() => this.props.downloadForm()}><Icon name='download' /></Menu.Item>
              <Menu.Item>
                <StatusIndicator />
              </Menu.Item>
              <Dropdown item text={this.getLanguageName(this.props.language)}>
                <Dropdown.Menu>
                  {this.getLanguages()}
                </Dropdown.Menu>
              </Dropdown>
              <Menu.Item disabled={this.props.status !== Status.STATUS_OK} onClick={() => this.props.requestPreview()}><Icon name='eye' /> Preview</Menu.Item>
          </Menu.Menu>
        </Menu>
      </Container>
    );
  }
}

const MainMenuConnected = connect(
  state => ({
    status: state.editor && state.editor.get('status'),
    language: (state.editor && state.editor.get('activeLanguage')) || Defaults.FALLBACK_LANGUAGE
  }),
  {
    setActiveLanguage,
    showFormOptions,
    showVariables,
    requestPreview,
    downloadForm,
    showValueSets
  }
)(MainMenu);

export {
  MainMenuConnected as default,
  MainMenu
};
