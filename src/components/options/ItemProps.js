import React, {Component} from 'react';
import {Table, Button, Input} from 'semantic-ui-react';

class ItemProps extends Component {

  propEditor(prop, key) {
    return (
      <Table.Row key={key}>
        <Table.Cell collapsing>
          <Button size='tiny' icon='remove' />
        </Table.Cell>
        <Table.Cell>
          {prop[0]}
        </Table.Cell>
        <Table.Cell>
           <Input transparent fluid defaultValue={prop[1]}/>
        </Table.Cell>
      </Table.Row>);
  }

  render() {
    const rows = this.props.item.get('props') && this.props.item.get('props').entrySeq()
      .map((p, i) => this.propEditor(p, i));
    return (
      <Table celled>
        <Table.Header>
          <Table.Row>
            <Table.HeaderCell collapsing><Button size='tiny' icon='add' /></Table.HeaderCell>
            <Table.HeaderCell>Name</Table.HeaderCell>
            <Table.HeaderCell>Value</Table.HeaderCell>
          </Table.Row>
        </Table.Header>
        <Table.Body>
          {rows}
        </Table.Body>
      </Table>
    );
  }
}

export {
  ItemProps as default
};
