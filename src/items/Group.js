import React, {Component} from 'react';
import {Input, Segment, Table, Dropdown, Icon} from 'semantic-ui-react';
import Item, {connectItem} from './Item';
import AddItemMenu from '../components/AddItemMenu';
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
                <Input transparent fluid placeholder={this.props.placeholder} defaultValue={this.props.item.getIn(['label', 'en'])}/>
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
        <Segment className={classnames({'composer-active': this.props.active})} attached='bottom'>
          {this.createChildren()}
          <AddItemMenu />
        </Segment>
      </React.Fragment>);
  }
}

const GroupConnected = connectItem(Group);

export {
  Group,
  GroupConnected as default
};
