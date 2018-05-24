import React, {Component} from 'react';
import {Table, Input, Icon, Dropdown} from 'semantic-ui-react';
import Item, {connectItem} from './Item';
import ItemTypeMenu from '../components/ItemTypeMenu';
import ItemMenu from '../components/ItemMenu';
import Validations from '../components/Validations';

class SimpleField extends Item {
  render() {
    return (
      <React.Fragment>
        <Table onClick={(e) => {e.stopPropagation(); this.props.setActive();}}  attached='top' color={this.props.active ? 'blue' : null}>
          <Table.Body>
            <Table.Row>
              <Table.Cell selectable collapsing width={2} >
                <a onClick={() => this.props.changeId()}>{this.props.item.get('id')}</a>
              </Table.Cell>
              <Table.Cell>
                <Input transparent fluid placeholder={this.props.placeholder} value={this.props.item.getIn(['label', this.props.language]) || ''} onChange={(e) => this.props.setAttribute('label', e.target.value, this.props.language)}/>
              </Table.Cell>
              <Table.Cell collapsing>
                <Dropdown text={this.props.item.get('type')}>
                  <Dropdown.Menu>
                    <Dropdown.Header content='Change type'/>
                    <ItemTypeMenu categoryFilter={item => item.type === 'input'} onSelect={(config) => this.props.setType(config)}/>
                  </Dropdown.Menu>
                </Dropdown>
              </Table.Cell>
              <Table.Cell collapsing>
                <ItemMenu item={this.props.item} parentItemId={this.props.parentItemId} onDelete={this.props.delete}/>
              </Table.Cell>
            </Table.Row>
          </Table.Body>
        </Table>
        <Table onClick={(e) => {e.stopPropagation(); this.props.setActive();}} celled attached={this.props.active ? true : 'bottom'} >
          <Table.Body>
            <Table.Row>
              <Table.Cell>
               <Input icon='eye' transparent fluid placeholder='Visibility' value={this.props.item.get('activeWhen') || ''} onChange={(e) => this.props.setAttribute('activeWhen', e.target.value)}/>
              </Table.Cell>
            </Table.Row>
            <Table.Row>
              <Table.Cell>
               <Input icon='asterisk' transparent fluid placeholder='Required' value={this.props.item.get('required') || ''} onChange={(e) => this.props.setAttribute('required', e.target.value)}/>
              </Table.Cell>
            </Table.Row>
            {
              this.props.active &&
              <Table.Row>
                <Table.Cell>
                  <Input icon='pencil' transparent fluid placeholder='Default value' value={this.props.item.get('defaultValue') || ''} onChange={(e) => this.props.setAttribute('defaultValue', e.target.value)}/>
                </Table.Cell>
              </Table.Row>
            }
          </Table.Body>
        </Table>
        {
          this.props.active && <Validations item={this.props.item} />
        }
      </React.Fragment>
    );
  }
}

const SimpleFieldConnected = connectItem(SimpleField);

export {
  SimpleField,
  SimpleFieldConnected as default
};
