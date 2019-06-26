import React from 'react';
import {Table, Input} from 'semantic-ui-react';
import Item, {connectItem} from './Item';
import ItemMenu from '../components/ItemMenu';
import Validations from '../components/Validations';
import CodeEditor from '../components/CodeEditor'
import Scrolltarget from './Scrolltarget';

class SimpleField extends Item {
  render() {
    return (
      <Scrolltarget itemId={this.props.itemId} className='composer-scrolltarget'>
        <Table onClick={(e) => {e.stopPropagation(); this.setActive(true);}}  attached='top' color={this.getBorderColor()}>
          <Table.Body>
            <Table.Row>
              <Table.Cell selectable collapsing width={2} >
                <a onClick={() => {if (this.props.editable) { this.changeId(); }}}>{this.props.itemId}</a>
              </Table.Cell>
              <Table.Cell>
                <Input transparent fluid placeholder={this.props.placeholder} value={this.props.item.getIn(['label', this.props.language]) || ''} onChange={(e) => this.setAttribute('label', e.target.value, this.props.language)}/>
              </Table.Cell>
              <Table.Cell collapsing>
                {this.props.item.get('type')}
              </Table.Cell>
              <Table.Cell collapsing>
                <ItemMenu item={this.props.item} parentItemId={this.props.parentItemId} onDelete={this.deleteItem}/>
              </Table.Cell>
            </Table.Row>
          </Table.Body>
        </Table>
        <Table onClick={(e) => {e.stopPropagation(); this.setActive(true);}} celled attached={this.props.active ? true : 'bottom'} >
          <Table.Body>
            <Table.Row>
              <Table.Cell error={this.getErrors().filter(e => e.get('type') === 'VISIBILITY').size > 0}>
                <CodeEditor value={this.props.item.get('activeWhen') || ''} onChange={value => this.setAttribute('activeWhen', value)} placeholder='Visibility' readOnly={!this.props.editable} icon='eye' errors={this.getErrors().filter(e => e.get('type') === 'VISIBILITY')}/>
              </Table.Cell>
            </Table.Row>
            <Table.Row>
              <Table.Cell error={this.getErrors().filter(e => e.get('type') === 'REQUIREMENT').size > 0}>
                <CodeEditor value={this.props.item.get('required') || ''} onChange={value => this.setAttribute('required', value)} placeholder='Required' readOnly={!this.props.editable} icon='asterisk' errors={this.getErrors().filter(e => e.get('type') === 'REQUIREMENT')}/>
              </Table.Cell>
            </Table.Row>
            {
              this.props.active &&
              <Table.Row>
                <Table.Cell error={this.getErrors().filter(e => e.get('type') === 'GENERAL' && e.get('message') === 'INVALID_DEFAULT_VALUE').size > 0}>
                  <Input icon='pencil' transparent fluid placeholder='Default value' value={this.props.item.get('defaultValue') || ''} onChange={(e) => this.setAttribute('defaultValue', e.target.value)}/>
                </Table.Cell>
              </Table.Row>
            }
          </Table.Body>
        </Table>
        {
          this.props.active && <Validations item={this.props.item} validations={this.props.validations} readOnly={!this.props.editable} />
        }
      </Scrolltarget>
    );
  }
}

const SimpleFieldConnected = connectItem(SimpleField);

export {
  SimpleField,
  SimpleFieldConnected as default
};
