import React, {Component} from 'react';
import {Modal, Button, Menu, Label, Table, Tab, Segment, Dropdown} from 'semantic-ui-react';
import {connect} from 'react-redux';
import Immutable from 'immutable';
import {hideTranslation, updateItem, updateValuesetEntry, updateValidation, addLanguage, setActiveLanguage} from '../actions';
import { UnderstoodTableEditor } from '@resys/understood';
import * as Defaults from '../defaults';

class LanguageConfigurator extends Component {

  state = {
    selectedLanguage: null
  };

  render() {
    const rows = this.props.languages.map((l, i) => {
        const name = Defaults.LANGUAGES.find(c => c.code === l).name;
        return (
          <Table.Row key={i}>
            <Table.Cell onClick={() => this.props.setActiveLanguage(l)}>{l == this.props.activeLanguage ? <Label ribbon color='blue'>{name}</Label>: name}</Table.Cell>
            <Table.Cell></Table.Cell>
          </Table.Row>);
      }
    );
    return (
      <Table celled>
        <Table.Header>
          <Table.Row>
            <Table.HeaderCell>Language</Table.HeaderCell>
            <Table.HeaderCell collapsing />
          </Table.Row>
        </Table.Header>
        <Table.Body>
          {rows}
        </Table.Body>
        <Table.Footer>
          <Table.Row>
            <Table.HeaderCell>Add new language: <Dropdown search selection
                options={this.props.newLanguages}
                value={this.state.selectedLanguage}
                onChange={(evt, data) => this.setState({selectedLanguage: data.value})} /></Table.HeaderCell>
            <Table.HeaderCell singleLine>
              <Button size='mini' disabled>Copy from active</Button>
              <Button size='mini' onClick={() => this.props.addLanguage(this.state.selectedLanguage)} disabled={!this.state.selectedLanguage}>Create empty</Button>
            </Table.HeaderCell>
          </Table.Row>
        </Table.Footer>
      </Table>
    );
  }
}

const Translator = ({translations, initialLanguage, onChange, languages}) => {
  return (
    <Segment basic style={{ height: 500 }}>
      <UnderstoodTableEditor
        translations={translations}
        format='keyToLanguage'
        initialLanguages={[initialLanguage]}
        onChangeItem={onChange}
        availableLanguages={languages}
      />
    </Segment>
  );
};

class TranslationDialog extends Component {

  getItemTranslations() {
    let translations = {};
    this.props.items.forEach((v, k) => {
      if (v.get('label')) {
        translations[`i:${v.get('id')}:l`] = v.get('label').toJS();
      }
      if (v.get('description')) {
        translations[`i:${v.get('id')}:d`] = v.get('description').toJS();
      }
      if (v.get('validations')) {
        v.get('validations').forEach((val, idx) => {
          translations[`i:${v.get('id')}:v:${idx}`] = val.get('message').toJS();
          return true;
        });
      }
      return true;
    });
    return translations;
  }

  getValueSetTranslations() {
    if (this.props.valueSets) {
      let translations = {};
      this.props.valueSets.forEach((vs, k) => {
        if (vs.get('entries')) {
          vs.get('entries').forEach((e, k) => {
            translations[`v:${vs.get('id')}:${k}:${e.get('id')}`] = e.get('label').toJS();
            return true;
          });
        }
        return true;
      });
      return translations;
    } else {
      return {};
    }
  }

  updateTranslation(key, language, text) {
    const keyTokens = key.split(':');
    if (keyTokens[0] === 'i') {
      // Item
      let attribute = '';
      if (keyTokens[2] === 'l') {
        this.props.updateItem(keyTokens[1], 'label', text, language);
      } else if (keyTokens[2] === 'd') {
        this.props.updateItem(keyTokens[1], 'description', text, language);
      } else if (keyTokens[2] === 'v') {
        this.props.updateValidation(keyTokens[1], parseInt(keyTokens[3]), 'message', text, language);
      }
    } else if (keyTokens[0] === 'v') {
        // Valueset
        this.props.updateValuesetEntry(keyTokens[1], parseInt(keyTokens[2]), null, text, language);
    }
  }

  render() {
    if (this.props.translationOpen) {
      const newLanguages = Defaults.LANGUAGES
          .filter(lang => !this.props.formLanguages.contains(lang.code))
          .map(lang => ({key: lang.code, text: lang.name, value: lang.code}));

      const panes = [
        {menuItem: 'Languages', render: () => <LanguageConfigurator
                                               languages={this.props.formLanguages}
                                               activeLanguage={this.props.language}
                                               newLanguages={newLanguages}
                                               addLanguage={this.props.addLanguage}
                                               setActiveLanguage={this.props.setActiveLanguage}
                                              />
        },
        {menuItem: 'Fields', render: () => <Translator
                                              translations={this.getItemTranslations()}
                                              initialLanguage={this.props.language}
                                              onChange={this.updateTranslation.bind(this)}
                                              languages={this.props.formLanguages.toJS()} />
        },
        {menuItem: 'Lists', render: () =>  <Translator
                                              translations={this.getValueSetTranslations()}
                                              initialLanguage={this.props.language}
                                              onChange={this.updateTranslation.bind(this)}
                                              languages={this.props.formLanguages.toJS()} />
        }
      ];

      return (
        <Modal open size='large'>
          <Modal.Header>Translation</Modal.Header>
          <Modal.Content >
            <Tab panes={panes} />
          </Modal.Content>
          <Modal.Actions>
            <Button primary onClick={() => this.props.hideTranslation()}>OK</Button>
          </Modal.Actions>
        </Modal>
      );
    } else {
      return null;
    }
  }
}

const TranslationDialogConnected = connect(
  state => ({
    translationOpen: state.editor && state.editor.get('translationOpen'),
    language: (state.editor && state.editor.get('activeLanguage')) || Defaults.FALLBACK_LANGUAGE,
    formLanguages: state.form.getIn(['metadata', 'languages']),
    items: state.form && state.form.get('data'),
    valueSets: state.form && state.form.get('valueSets')
  }), {
    hideTranslation,
    updateItem,
    updateValuesetEntry,
    updateValidation,
    addLanguage,
    setActiveLanguage
  }
)(TranslationDialog);

export {
  TranslationDialogConnected as default,
  TranslationDialog
};
