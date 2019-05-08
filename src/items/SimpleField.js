import React, {Component} from 'react';
import {Table, Input} from 'semantic-ui-react';
import Item, {connectItem} from './Item';
import ItemMenu from '../components/ItemMenu';
import Validations from '../components/Validations';
import CodeEditor from '../components/CodeEditor';

class SimpleField extends Item {
  render() {
    return (
      <React.Fragment>
        <Table onClick={(e) => {e.stopPropagation(); this.props.setActive();}}  attached='top' color={this.props.active ? 'blue' : null}>
          <Table.Body>
            <Table.Row>
              <Table.Cell selectable collapsing width={2} >
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
        <Table onClick={(e) => {e.stopPropagation(); this.props.setActive();}} celled attached={this.props.active ? true : 'bottom'} >
          <Table.Body>
            <Table.Row>
              <Table.Cell error={this.getErrors().filter(e => e.get('type') === 'VISIBILITY').size > 0}>
                <CodeEditor value={this.props.item.get('activeWhen') || ''} onChange={value => this.props.setAttribute('activeWhen', value)} placeholder='Visibility' icon='eye'/>
              </Table.Cell>
            </Table.Row>
            <Table.Row>
              <Table.Cell error={this.getErrors().filter(e => e.get('type') === 'REQUIREMENT').size > 0}>
                <CodeEditor value={this.props.item.get('required') || ''} onChange={value => this.props.setAttribute('required', value)} placeholder='Required' icon='asterisk'/>
              </Table.Cell>
            </Table.Row>
            {
              this.props.active &&
              <Table.Row>
                <Table.Cell error={this.getErrors().filter(e => e.get('type') === 'GENERAL' && e.get('message') === 'INVALID_DEFAULT_VALUE').size > 0}>
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
