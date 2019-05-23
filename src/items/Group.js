import React from 'react';
import {Input, Segment, Table, Dropdown, Icon} from 'semantic-ui-react';
import Item, {connectItem} from './Item';
import ItemTypeMenu from '../components/ItemTypeMenu';
import ItemMenu from '../components/ItemMenu';
import classnames from 'classnames';
import CodeEditor from '../components/CodeEditor';

class Group extends Item {

  render() {
    const {treeCollapsed, item, itemId, editable} = this.props;
    const itemTypeFilter = i => item.get('type') !== 'surveygroup' ? i.config.type !== 'survey' : true;

    return (
      <React.Fragment>
        <Table attached={treeCollapsed ? null :'top'} onClick={(e) => {e.stopPropagation(); this.setActive();}}  color={this.props.active ? 'blue' : null}>
          <Table.Body>
            <Table.Row>
              <Table.Cell collapsing>
                <Icon name={treeCollapsed ? 'caret right' : 'caret down'} size='large' fitted onClick={() => this.setTreeCollapsed(!treeCollapsed)}/>
              </Table.Cell>
              <Table.Cell selectable collapsing width={2}>
                <a onClick={() => {if (editable) { this.changeId(); }}}>{itemId}</a>
              </Table.Cell>
              <Table.Cell>
                <Input transparent fluid placeholder={this.props.placeholder} value={item.getIn(['label', this.props.language]) || ''} onChange={(e) => this.setAttribute('label', e.target.value, this.props.language)}/>
              </Table.Cell>
              <Table.Cell collapsing>
                {item.get('type')}
              </Table.Cell>
              <Table.Cell collapsing>
                <ItemMenu item={item} parentItemId={this.props.parentItemId} onDelete={this.deleteItem}/>
              </Table.Cell>
            </Table.Row>
          </Table.Body>
        </Table>

        { !treeCollapsed ?
         <React.Fragment>
            <Table onClick={(e) => {e.stopPropagation(); this.setActive();}}  celled attached >
              <Table.Body>
                <Table.Row>
                  <Table.Cell error={this.getErrors().filter(e => e.get('type') === 'VISIBILITY').size > 0}>
                  <CodeEditor value={item.get('activeWhen') || ''} onChange={value => this.setAttribute('activeWhen', value)} placeholder='Visibility' readOnly={!editable} icon='eye' errors={this.getErrors().filter(e => e.get('type') === 'VISIBILITY')}/>
                  </Table.Cell>
                </Table.Row>
              </Table.Body>
            </Table>
            <Segment onClick={(e) => {e.stopPropagation(); this.setActive();}}  className={classnames('composer-group', {'composer-active': this.props.active})} attached='bottom'>
              {this.createChildren({parentItemId: itemId, getItemById: this.props.getItemById})}

              <Dropdown button text='Add item' disabled={!editable} lazyLoad>
                <Dropdown.Menu>
                  <ItemTypeMenu itemTypeFilter={itemTypeFilter} onSelect={(config) => this.newItem(config, itemId)}/>
                </Dropdown.Menu>
              </Dropdown>
            </Segment>
        </React.Fragment>
        : null
        }
      </React.Fragment>);
  }
}

const GroupConnected = connectItem(Group);

export {
  Group,
  GroupConnected as default
};
