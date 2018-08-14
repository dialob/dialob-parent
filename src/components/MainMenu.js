import React, {Component} from 'react';
import {Container, Menu, Icon, Dropdown, Loader, Popup, Header} from 'semantic-ui-react';
import {connect} from 'react-redux';
import * as Defaults from '../defaults';
import {setActiveLanguage, showFormOptions, showVariables, requestPreview, downloadForm, showValueSets, showTranslation} from '../actions';
import StatusIndicator from './StatusIndicator';
import * as Status from '../helpers/constants';

class MainMenu extends Component {

  getLanguages() {
    return Defaults.LANGUAGES
      .filter(lang => this.props.formLanguages && this.props.formLanguages.contains(lang.code))
      .map((lang, i) =>
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
              &nbsp;<small>{this.props.formLabel}</small>
          </Menu.Item>
          <Menu.Item disabled>
            Versioning
          </Menu.Item>
          <Menu.Item onClick={() => this.props.showTranslation()}>
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
              <Menu.Item onClick={() => this.props.downloadForm()}>
                <Popup
                  trigger={<Icon name='download' />}
                  content='Download dialog as JSON'
                  on='hover' />
              </Menu.Item>
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
    language: (state.editor && state.editor.get('activeLanguage')) || Defaults.FALLBACK_LANGUAGE,
    formLanguages: state.form.getIn(['metadata', 'languages']),
    formLabel: state.form.getIn(['metadata', 'label'])
  }),
  {
    setActiveLanguage,
    showFormOptions,
    showVariables,
    requestPreview,
    downloadForm,
    showValueSets,
    showTranslation
  }
)(MainMenu);

export {
  MainMenuConnected as default,
  MainMenu
};
