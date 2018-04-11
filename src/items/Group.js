import React, {Component} from 'react';
import {Input, Segment, Table, Dropdown, Icon, Popup, Button, Menu} from 'semantic-ui-react';
import Item, {connectItem} from './Item';
import ItemTypeMenu from '../components/ItemTypeMenu';
import ItemMenu from '../components/ItemMenu';
import classnames from 'classnames';

class Group extends Item {

  render() {
    return (
      <React.Fragment>
        <Table attached='top' onClick={() => this.props.setActive()} color={this.props.active ? 'blue' : null}>
          <Table.Body>
            <Table.Row>
            <Table.Cell collapsing width={2}>
                {this.props.item.get('id')}
              </Table.Cell>
              <Table.Cell>
                <Input transparent fluid placeholder={this.props.placeholder} value={this.props.item.getIn(['label', this.props.language]) || ''} onChange={(e) => this.props.setAttribute('label', e.target.value, this.props.language)}/>
              </Table.Cell>
              <Table.Cell collapsing>
                <Dropdown text={this.props.item.get('type')}>
                  <Dropdown.Menu>
                    <Dropdown.Item>Group</Dropdown.Item>
                    <Dropdown.Item>Survey</Dropdown.Item>
                    <Dropdown.Item>Multi-row</Dropdown.Item>
                  </Dropdown.Menu>
                </Dropdown>
              </Table.Cell>
              <Table.Cell collapsing>
                <ItemMenu item={this.props.item} parentItemId={this.props.parentItemId} onDelete={this.props.delete}/>
              </Table.Cell>
            </Table.Row>
          </Table.Body>
        </Table>
        <Table onClick={() => this.props.setActive()}  celled attached >
          <Table.Body>
            <Table.Row>
              <Table.Cell>
               <Input transparent fluid placeholder='Visibility' value={this.props.item.get('activeWhen') || ''} onChange={(e) => this.props.setAttribute('activeWhen', e.target.value)}/>
              </Table.Cell>
            </Table.Row>
          </Table.Body>
        </Table>
        <Segment className={classnames({'composer-active': this.props.active})} attached='bottom'>
          {this.createChildren({parentItemId: this.props.item.get('id')})}
          <Popup trigger={<Button circular icon='add' />} on='click'>
            <Menu secondary vertical>
              <Menu.Item>
               <Menu.Header>Add new</Menu.Header>
              </Menu.Item>
              <ItemTypeMenu onSelect={(config) => this.props.newItem(config, this.props.item.get('id'))}/>
            </Menu>
          </Popup>
        </Segment>
      </React.Fragment>);
  }
}

const GroupConnected = connectItem(Group);

export {
  Group,
  GroupConnected as default
};
