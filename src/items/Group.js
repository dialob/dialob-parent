import React, {Component} from 'react';
import {Input, Segment, Table, Dropdown, Icon, Popup, Button, Menu} from 'semantic-ui-react';
import Item, {connectItem} from './Item';
import ItemTypeMenu from '../components/ItemTypeMenu';
import ItemMenu from '../components/ItemMenu';
import classnames from 'classnames';

class Group extends Item {

  render() {
    const itemTypeFilter = i => this.props.item.get('type') !== 'surveygroup' ? i.config.type !== 'survey' : true;
    return (
      <React.Fragment>
        <Table attached='top' onClick={(e) => {e.stopPropagation(); this.props.setActive();}}  color={this.props.active ? 'blue' : null}>
          <Table.Body>
            <Table.Row>
              <Table.Cell selectable collapsing width={2}>
                <a onClick={() => {if (this.props.editable) { this.props.changeId(); }}}>{this.props.item.get('id')}</a>
              </Table.Cell>
              <Table.Cell>
                <Input transparent fluid placeholder={this.props.placeholder} value={this.props.item.getIn(['label', this.props.language]) || ''} onChange={(e) => this.props.setAttribute('label', e.target.value, this.props.language)}/>
              </Table.Cell>
              <Table.Cell collapsing>
                {this.props.item.get('type')}
              </Table.Cell>
              <Table.Cell collapsing>
                <ItemMenu item={this.props.item} parentItemId={this.props.parentItemId} onDelete={this.props.delete}/>
              </Table.Cell>
            </Table.Row>
          </Table.Body>
        </Table>
        <Table onClick={(e) => {e.stopPropagation(); this.props.setActive();}}  celled attached >
          <Table.Body>
            <Table.Row>
              <Table.Cell error={this.getErrors().filter(e => e.get('type') === 'VISIBILITY').size > 0}>
               <Input transparent fluid placeholder='Visibility' value={this.props.item.get('activeWhen') || ''} onChange={(e) => this.props.setAttribute('activeWhen', e.target.value)}/>
              </Table.Cell>
            </Table.Row>
          </Table.Body>
        </Table>
        <Segment onClick={(e) => {e.stopPropagation(); this.props.setActive();}}  className={classnames('composer-group', {'composer-active': this.props.active})} attached='bottom'>
          {this.createChildren({parentItemId: this.props.item.get('id')})}

          <Dropdown button text='Add item'>
            <Dropdown.Menu>
              <ItemTypeMenu itemTypeFilter={itemTypeFilter} onSelect={(config) => this.props.newItem(config, this.props.item.get('id'))}/>
            </Dropdown.Menu>
          </Dropdown>
        </Segment>
      </React.Fragment>);
  }
}

const GroupConnected = connectItem(Group);

export {
  Group,
  GroupConnected as default
};
