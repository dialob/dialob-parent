import React, {Component} from 'react';
import {Message, Table, Icon, Input} from 'semantic-ui-react';
import Item, {connectItem} from './Item';
import Markdown from 'react-markdown';

class Note extends Item {
  render() {
    return (
      <React.Fragment>
         <Table attached='top' onClick={() => this.props.setActive()} color={this.props.active ? 'blue' : null}>
          <Table.Body>
            <Table.Row>
              <Table.Cell>
                {this.props.item.get('id')}
              </Table.Cell>
              <Table.Cell collapsing>
                <Icon name='content' />
              </Table.Cell>
            </Table.Row>
          </Table.Body>
        </Table>
        <Table onClick={() => this.props.setActive()}  celled attached >
          <Table.Body>
            <Table.Row>
              <Table.Cell>
               <Input transparent fluid placeholder='Visibility' defaultValue={this.props.item.get('activeWhen')}/>
              </Table.Cell>
            </Table.Row>
          </Table.Body>
        </Table>
        <Message info={this.props.active} onClick={() => this.props.setActive()} attached='bottom'>
          <Markdown source={this.props.item.getIn(['label', 'en'])} />
        </Message>
      </React.Fragment>
    );
  }
}

const NoteConnected = connectItem(Note);

export {
  Note,
  NoteConnected as default
};
