import React, {Component} from 'react';
import {Table, Button, Input, Dropdown, Form, Divider} from 'semantic-ui-react';
import {connect} from 'react-redux';
import {findValueset} from '../../helpers/utils';
import {createValueset, addValuesetEntry} from '../../actions';
import * as Defaults from '../../defaults';

class Choices extends Component {

  entryEditor(entry, key) {
    return (
      <Table.Row key={key}>
        <Table.Cell collapsing>
          <Button size='tiny' icon='remove' />
        </Table.Cell>
        <Table.Cell>
           <Input transparent fluid defaultValue={entry.get('id')} />
        </Table.Cell>
        <Table.Cell>
           <Input transparent fluid defaultValue={entry.getIn(['label', this.props.language])}/>
        </Table.Cell>
      </Table.Row>);
  }

  render() {
    if (!this.props.getValueset()) {
      let options = [];
      return (
        <React.Fragment>
          <Form.Field>
            <label>Select global valueset</label>
            <Dropdown fluid search selection options={options} />
          </Form.Field>
          <Divider horizontal>Or</Divider>
          <Button onClick={() => this.props.createValueset(this.props.item.get('id'))}>Create local value set</Button>
        </React.Fragment>
      );
    } else  {
      let rows = this.props.getValueset().get('entries').map((e, key) => this.entryEditor(e, key));
      return (
        <Table celled>
        <Table.Header>
          <Table.Row>
            <Table.HeaderCell collapsing><Button size='tiny' icon='add' /></Table.HeaderCell>
            <Table.HeaderCell collapsing>Key</Table.HeaderCell>
            <Table.HeaderCell>Text</Table.HeaderCell>
          </Table.Row>
        </Table.Header>
        <Table.Body>
          {rows}
        </Table.Body>
      </Table>
      );
    }

    /*
    const rows = this.props.item.get('props') && this.props.item.get('props').entrySeq()
      .map((p, i) => this.propEditor(p, i));
    return (
      <Table celled>
        <Table.Header>
          <Table.Row>
            <Table.HeaderCell collapsing><Button size='tiny' icon='add' /></Table.HeaderCell>
            <Table.HeaderCell collapsing>Key</Table.HeaderCell>
            <Table.HeaderCell>Text</Table.HeaderCell>
          </Table.Row>
        </Table.Header>
        <Table.Body>
          {rows}
        </Table.Body>
      </Table>
    );
    */
  }
}

const ChoicesConnected = connect(
  (state, props) => ({
    language: (state.editor && state.editor.get('activeLanguage')) || Defaults.FALLBACK_LANGUAGE,
    get getValueset() { return () => findValueset(state.form, props.item.get('valueSetId')); }
  }), {
    createValueset
  }
)(Choices);

export {
  ChoicesConnected as default,
  Choices
};
