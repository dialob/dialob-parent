import React from 'react';
import {Input, Segment, Table, Dropdown} from 'semantic-ui-react';
import Item, {connectItem} from './Item';
import ItemTypeMenu from '../components/ItemTypeMenu';
import ItemMenu from '../components/ItemMenu';
import classnames from 'classnames';
import CodeEditor from '../components/CodeEditor';

class Group extends Item {

  render() {
    const itemTypeFilter = i => this.props.item.get('type') !== 'surveygroup' ? i.config.type !== 'survey' : true;
    return (
      <React.Fragment>
        <Table attached='top' onClick={(e) => {e.stopPropagation(); this.setActive();}}  color={this.props.active ? 'blue' : null}>
          <Table.Body>
            <Table.Row>
              <Table.Cell selectable collapsing width={2}>
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
        <Table onClick={(e) => {e.stopPropagation(); this.setActive();}}  celled attached >
          <Table.Body>
            <Table.Row>
              <Table.Cell error={this.getErrors().filter(e => e.get('type') === 'VISIBILITY').size > 0}>
               <CodeEditor value={this.props.item.get('activeWhen') || ''} onChange={value => this.setAttribute('activeWhen', value)} placeholder='Visibility' readOnly={!this.props.editable} icon='eye' errors={this.getErrors().filter(e => e.get('type') === 'VISIBILITY')}/>
              </Table.Cell>
            </Table.Row>
          </Table.Body>
        </Table>
        <Segment onClick={(e) => {e.stopPropagation(); this.setActive();}}  className={classnames('composer-group', {'composer-active': this.props.active})} attached='bottom'>
          {this.createChildren({parentItemId: this.props.itemId, getItemById: this.props.getItemById})}

          <Dropdown button text='Add item' disabled={!this.props.editable} lazyLoad>
            <Dropdown.Menu>
              <ItemTypeMenu itemTypeFilter={itemTypeFilter} onSelect={(config) => this.newItem(config, this.props.itemId)}/>
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
