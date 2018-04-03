import React, {Component} from 'react';
import {Segment, Header, Table, Input} from 'semantic-ui-react';

class Properties extends Component {
  render() {
    return (
      <Segment basic>
        <Header as='h3' attached='top'>Page 1</Header>
        <Table celled definition attached>
          <Table.Body>
            <Table.Row>
              <Table.Cell collapsing>
                ID
              </Table.Cell>
              <Table.Cell>
                <Input transparent fluid/>
              </Table.Cell>
            </Table.Row>
            <Table.Row>
              <Table.Cell collapsing>
                Item type
              </Table.Cell>
              <Table.Cell>
                <Input transparent fluid />
              </Table.Cell>
            </Table.Row>
            <Table.Row>
              <Table.Cell collapsing>
                Visibility
              </Table.Cell>
              <Table.Cell>
                <Input transparent fluid icon={{name: 'edit', link: true}}/>
              </Table.Cell>
            </Table.Row>
          </Table.Body>
        </Table>
      </Segment>
    );
  }
}

export default Properties;