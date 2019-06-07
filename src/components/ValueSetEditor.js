import React, {Component} from 'react';
import {Table, Button, Input, Message} from 'semantic-ui-react';
import {connect} from 'react-redux';
import {findValueset} from '../helpers/utils';
import {createValueset, createValuesetEntry, updateValuesetEntry, deleteValuesetEntry} from '../actions';
import * as Defaults from '../defaults';
import { translateErrorMessage } from '../helpers/utils';

class ValueSetEditor extends Component {

  entryEditor(entry, index) {
    const vsId = this.props.valueSetId;
    return (
      <Table.Row key={index}>
        <Table.Cell collapsing>
          <Button size='tiny' icon='remove' onClick={() => this.props.deleteValuesetEntry(vsId, index)} />
        </Table.Cell>
        <Table.Cell>
           <Input transparent fluid value={entry.get('id') || ''} onChange={(e) => this.props.updateValuesetEntry(vsId, index, e.target.value, null, null)} />
        </Table.Cell>
        <Table.Cell>
           <Input transparent fluid value={entry.getIn(['label', this.props.language]) || ''} onChange={(e) => this.props.updateValuesetEntry(vsId, index, null, e.target.value, this.props.language)}/>
        </Table.Cell>
      </Table.Row>);
  }

  render() {
      const dedupe = (item, idx, arr) => arr.indexOf(item) === idx;

      const rows = this.props.getValueset().get('entries') ? this.props.getValueset().get('entries').map((e, i) => this.entryEditor(e, i)) : [];
      const errors = this.props.errors &&
          this.props.errors
            .filter(e => e.get('message').startsWith('VALUESET_') && e.get('itemId') === this.props.valueSetId)
            .groupBy(e => e.get('level'));

      const errorList = errors && errors.get('ERROR') &&
                <Message attached={errors.get('WARNING') ? true : 'bottom'} error header='Errors'
                    list={errors.get('ERROR').map(e => translateErrorMessage(e)).toJS().filter(dedupe)} />;

      const warningList = errors && errors.get('WARNING') &&
                <Message attached='bottom' warning header='Warnings'
                    list={errors.get('WARNING').map(e => translateErrorMessage(e)).toJS().filter(dedupe)} />;

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
