import React, {Component} from 'react';
import {Table, Input, Icon, Dropdown} from 'semantic-ui-react';
import Item, {connectItem} from './Item';

class SimpleField extends Item {
  render() {
    return (
      <React.Fragment>
        <Table onClick={() => this.props.setActive()} attached='top' color={this.props.active ? 'blue' : null}>
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
                    <Dropdown.Header content='Change type' />
                    <Dropdown item text='Inputs'>
                      <Dropdown.Menu>
                        <Dropdown.Item>Text</Dropdown.Item>
                        <Dropdown.Item>Text box</Dropdown.Item>
                        <Dropdown.Item>Decimal</Dropdown.Item>
                        <Dropdown.Item>Integer</Dropdown.Item>
                      </Dropdown.Menu>
                    </Dropdown>
                    <Dropdown item text='Output'>
                      <Dropdown.Menu>
                        <Dropdown.Item>Note</Dropdown.Item>
                        <Dropdown.Item>Image</Dropdown.Item>
                        <Dropdown.Item>Report</Dropdown.Item>
                      </Dropdown.Menu>
                    </Dropdown>
                    <Dropdown item text='Structure'>
                      <Dropdown.Menu>
                        <Dropdown.Item>Group</Dropdown.Item>
                        <Dropdown.Item>Survey</Dropdown.Item>
                        <Dropdown.Item>Multi-row</Dropdown.Item>
                      </Dropdown.Menu>
                    </Dropdown>
                  </Dropdown.Menu>
                </Dropdown>
              </Table.Cell>
              <Table.Cell collapsing>
                <Dropdown trigger={<Icon name='content' link />}>
                  <Dropdown.Menu>
                    <Dropdown.Item icon='options' text='Options...'/>
                    <Dropdown.Item icon='remove' text='Delete' />
                    <Dropdown.Divider />
                    <Dropdown.Item icon='copy' text='Duplicate' />
                    <Dropdown.Item icon='move' text='Move to' />
                    <Dropdown.Divider />
                    <Dropdown.Item icon='add' text='Add new' />
                  </Dropdown.Menu>
                </Dropdown>
              </Table.Cell>
            </Table.Row>
          </Table.Body>
        </Table>
        <Table onClick={() => this.props.setActive()}  celled attached='bottom' >
          <Table.Body>
            <Table.Row>
              <Table.Cell>
               <Input transparent fluid placeholder='Visibility' defaultValue={this.props.item.get('activeWhen')}/>
              </Table.Cell>
            </Table.Row>
            <Table.Row>
              <Table.Cell>
               <Input transparent fluid placeholder='Required' defaultValue={this.props.item.get('required')}/>
              </Table.Cell>
            </Table.Row>
          </Table.Body>
        </Table>
      </React.Fragment>
    );
  }
}

const SimpleFieldConnected = connectItem(SimpleField);

export {
  SimpleField,
  SimpleFieldConnected as default
};
