import React from 'react';
import {Segment, Table, Icon} from 'semantic-ui-react';
import Item, {connectItem} from './Item';
import ItemMenu from '../components/ItemMenu';
import RichEditor from '../components/RichEditor';
import classnames from 'classnames';
import Scrolltarget from './Scrolltarget';

class Note extends Item {
  render() {
    const editorKey = `nrt_${this.props.item.get('id')}_${this.props.language}`;
    return (
      <Scrolltarget itemId={this.props.itemId} className='composer-scrolltarget'>
         <Table attached='top' onClick={(e) => {e.stopPropagation(); this.setActive(true);}} color={this.getBorderColor()}>
          <Table.Body>
            <Table.Row>
              <Table.Cell selectable>
                <a onClick={() => {if (this.props.editable) { this.changeId(); }}}>{this.props.itemId}</a>
              </Table.Cell>
              <Table.Cell collapsing>
                <ItemMenu item={this.props.item} parentItemId={this.props.parentItemId} onDelete={this.deleteItem}/>
              </Table.Cell>
            </Table.Row>
          </Table.Body>
        </Table>
        {
          this.props.active &&
          <Table onClick={(e) => {e.stopPropagation(); this.setActive(true);}}  celled attached >
            <Table.Body>
              <Table.Row>
                <Table.Cell error={this.getErrors().filter(e => e.get('type') === 'VISIBILITY').size > 0}>
                  <div className='dialob-rule'>
                    <Icon name='eye' className='dialob-rule-icon' />
                    {this.props.item.get('activeWhen') || <span className='dialob-placeholder'>Visibility</span>}
                  </div>
                </Table.Cell>
              </Table.Row>
            </Table.Body>
          </Table>
        }
        <Segment onClick={(e) => {e.stopPropagation(); this.setActive(true);}}  className={classnames({'composer-active': this.props.active})} attached='bottom'>
          <RichEditor key={editorKey} active={this.props.active} id={editorKey} onChange={(v) => this.setAttribute('label', v, this.props.language)} defaultValue={this.props.item.getIn(['label', this.props.language])} placeholder='Write note text...' />
        </Segment>
      </Scrolltarget>
    );
  }
}

const NoteConnected = connectItem(Note);

export {
  Note,
  NoteConnected as default
};
