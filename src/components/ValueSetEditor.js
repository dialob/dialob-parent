import React, {Component} from 'react';
import {Table, Button, Input, Dropdown, Form, Divider} from 'semantic-ui-react';
import {connect} from 'react-redux';
import {findValueset} from '../helpers/utils';
import {createValueset, createValuesetEntry, updateValuesetEntry, deleteValuesetEntry} from '../actions';
import * as Defaults from '../defaults';

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
      let rows = this.props.getValueset().get('entries') ? this.props.getValueset().get('entries').map((e, i) => this.entryEditor(e, i)) : [];
      return (
        <Table celled>
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
      );
  }
}

const ValueSetEditorConnected = connect(
  (state, props) => ({
    language: (state.editor && state.editor.get('activeLanguage')) || Defaults.FALLBACK_LANGUAGE,
    get getValueset() { return () => findValueset(state.form, props.valueSetId); }
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
