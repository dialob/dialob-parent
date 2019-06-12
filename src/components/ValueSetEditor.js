import React, {Component} from 'react';
import {Table, Button, Input, Message} from 'semantic-ui-react';
import {connect} from 'react-redux';
import {findValueset} from '../helpers/utils';
import {createValueset, createValuesetEntry, updateValuesetEntry, deleteValuesetEntry} from '../actions';
import * as Defaults from '../defaults';
import { translateErrorMessage } from '../helpers/utils';

class EntryRow extends Component {
  render() {
    const {entry, index, valueSetErrors, deleteValuesetEntry, updateValuesetEntry, valueSetId, language} = this.props;
    const entryErrors = valueSetErrors && valueSetErrors.find(e => e.get('index') === index || e.get('expression') === entry.get('id'));
    return (
      <Table.Row key={index} error={entryErrors ? true : false}>
        <Table.Cell collapsing>
          <Button size='tiny' icon='remove' onClick={() => deleteValuesetEntry(valueSetId, index)} />
        </Table.Cell>
        <Table.Cell>
            <Input transparent fluid value={entry.get('id') || ''} onChange={(e) => updateValuesetEntry(valueSetId, index, e.target.value, null, null)} />
        </Table.Cell>
        <Table.Cell>
            <Input transparent fluid value={entry.getIn(['label', language]) || ''} onChange={(e) => updateValuesetEntry(valueSetId, index, null, e.target.value, language)}/>
        </Table.Cell>
      </Table.Row>);
  }
};

class ValueSetEditor extends Component {

  render() {
      const {deleteValuesetEntry, updateValuesetEntry, valueSetId, language} = this.props;
      const dedupe = (item, idx, arr) => arr.indexOf(item) === idx;

      const valueSetErrors = this.props.errors &&
          this.props.errors
            .filter(e => e.get('message').startsWith('VALUESET_') && e.get('itemId') === valueSetId)
      const errors = valueSetErrors && valueSetErrors.groupBy(e => e.get('level'));

      const errorList = errors && errors.get('ERROR') &&
                <Message attached={errors.get('WARNING') ? true : 'bottom'} error header='Errors'
                    list={errors.get('ERROR').map(e => translateErrorMessage(e)).toJS().filter(dedupe)} />;

      const warningList = errors && errors.get('WARNING') &&
                <Message attached='bottom' warning header='Warnings'
                    list={errors.get('WARNING').map(e => translateErrorMessage(e)).toJS().filter(dedupe)} />;

      const rows = this.props.getValueset().get('entries')
        ? this.props.getValueset().get('entries').map((e, i) =>
           <EntryRow entry={e} index={i} valueSetErrors={valueSetErrors}
              deleteValuesetEntry={deleteValuesetEntry} updateValuesetEntry={updateValuesetEntry} valueSetId={valueSetId} language={language} />)
        : [];

      return (
      <React.Fragment>
        <Table celled attached={errorList || warningList ? 'top' : null}>
          <Table.Header>
            <Table.Row>
              <Table.HeaderCell collapsing><Button size='tiny' icon='add' onClick={() => this.props.createValuesetEntry(this.props.getValueset().get('id'))} /></Table.HeaderCell>
              <Table.HeaderCell>Key</Table.HeaderCell>
              <Table.HeaderCell>Text</Table.HeaderCell>
            </Table.Row>
          </Table.Header>
          <Table.Body>
            {rows}
          </Table.Body>
        </Table>
        {errorList}
        {warningList}
      </React.Fragment>
      );
  }
}

const ValueSetEditorConnected = connect(
  (state, props) => ({
    language: (state.dialobComposer.editor && state.dialobComposer.editor.get('activeLanguage')) || Defaults.FALLBACK_LANGUAGE,
    get getValueset() { return () => findValueset(state.dialobComposer.form, props.valueSetId); },
    errors: state.dialobComposer.editor && state.dialobComposer.editor.get('errors')
  }), {
    createValueset,
    createValuesetEntry,
    updateValuesetEntry,
    deleteValuesetEntry
  }
)(ValueSetEditor);

export {
  ValueSetEditorConnected as default,
  ValueSetEditor
};
