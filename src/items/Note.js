import React, {Component} from 'react';
import {Segment, Table, Input} from 'semantic-ui-react';
import Item, {connectItem} from './Item';
import ItemMenu from '../components/ItemMenu';
import RichEditor from '../components/RichEditor';
import classnames from 'classnames';

class Note extends Item {
  render() {
    return (
      <React.Fragment>
         <Table attached='top' onClick={() => this.setActive()} color={this.props.active ? 'blue' : null}>
          <Table.Body>
            <Table.Row>
              <Table.Cell selectable>
                <a onClick={() => {if (this.props.editable) { this.changeId(); }}}>{this.props.item.get('id')}</a>
              </Table.Cell>
              <Table.Cell collapsing>
                <ItemMenu item={this.props.item} parentItemId={this.props.parentItemId} onDelete={this.deleteItem}/>
              </Table.Cell>
            </Table.Row>
          </Table.Body>
        </Table>
        <Table onClick={() => this.props.setActive()}  celled attached >
          <Table.Body>
            <Table.Row>
              <Table.Cell error={this.getErrors().filter(e => e.get('type') === 'VISIBILITY').size > 0}>
               <Input transparent fluid placeholder='Visibility' value={this.props.item.get('activeWhen') || ''} onChange={(e) => this.props.setAttribute('activeWhen', e.target.value)}/>
              </Table.Cell>
            </Table.Row>
          </Table.Body>
        </Table>
        <Segment onClick={(e) => {e.stopPropagation(); this.setActive();}}  className={classnames({'composer-active': this.props.active})} attached='bottom'>
          <RichEditor id={`nrt_${this.props.item.get('id')}`} onChange={(v) => this.setAttribute('label', v, this.props.language)} defaultValue={this.props.item.getIn(['label', 'en'])} placeholder='Write note text...' />
        </Segment>
      </React.Fragment>
    );
  }
}

const NoteConnected = connectItem(Note);

export {
  Note,
  NoteConnected as default
};
