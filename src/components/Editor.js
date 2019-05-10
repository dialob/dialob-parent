import React, {Component} from 'react';
import {Segment, Menu, Input, Button, Dropdown, Label, Table} from 'semantic-ui-react';
import {connect} from 'react-redux';
import {setActivePage, addItem, deleteItem, loadForm, showItemOptions, showChangeId, copyItem, updateItem} from '../actions';
import {itemFactory} from '../items';
import * as Defaults from '../defaults';
import ItemTypeMenu from './ItemTypeMenu';
import CodeEditor from './CodeEditor';

const PageMenu = ({onDelete, onOptions, onChangeId, onDuplicate, editable}) => (
  <Dropdown icon='content' style={{marginLeft: '0.5em'}}>
    <Dropdown.Menu>
      <Dropdown.Item icon='options' text='Options...' onClick={onOptions}/>
      <Dropdown.Item disabled={!editable} icon='key' text='Change ID...' onClick={onChangeId} />
      <Dropdown.Item disabled={!editable} icon='remove' text='Delete' onClick={onDelete} />
      <Dropdown.Item disabled={!editable} icon='copy' text='Duplicate' onClick={onDuplicate} />
    </Dropdown.Menu>
  </Dropdown>
);

class Editor extends Component {

  constructor(props) {
    super(props);
    this.getValueset = this.findValueset.bind(this);
    this.getItemById = this.findItemById.bind(this);
  }

  findItemById(itemId) {
    return this.props.items.get(itemId);
  }

  findValueset(valueSetId) {
    return (this.props.valueSets && valueSetId) ? data.get('valueSets').find(v => v.get('id') === id) : null;
  }

  createChildren(activePage, props, config) {
    return activePage && activePage.get('items') && activePage.get('items')
      .map(itemId => this.findItemById(itemId))
      .map(item => itemFactory(item, props, config));
  }

  newItem(config, activePageId) {
    this.props.addItem(config, activePageId);
  }

  render() {
    const {rootItemId} = this.props;
    if (!rootItemId) {
      return null;
    }
    const rootItem = this.findItemById(rootItemId);

    const activePageId = this.props.activePageId ? this.props.activePageId: rootItem.getIn(['items', 0]);
    const activePage = this.props.items.get(activePageId);
    const pages = rootItem && rootItem.get('items') ? rootItem.get('items')
                        .map(itemId => this.findItemById(itemId))
                        .map((item, index) =>
                            <Menu.Item onClick={() => this.props.setActivePage(item.get('id'))} key={index} active={item.get('id') === activePageId}>{item.getIn(['label', 'en']) || <em>{item.get('id')}</em>}
                               <PageMenu onDelete={() => this.props.deleteItem(item.get('id'))}
                                         onOptions={() => this.props.showItemOptions(item.get('id'), true)}
                                         onChangeId={() => this.props.showChangeId(item.get('id'))}
                                         onDuplicate={() => this.props.copyItem(item.get('id'))}
                                         editable={!this.props.formTag}
                                 />
                            </Menu.Item>) : null;
    return (
      <Segment basic>
        <Menu tabular attached='top'>
          {pages}
          <Menu.Menu position='right' >
            <Menu.Item>
              { (!pages || pages.size === 0) && <Label pointing='right' size='large' color='blue'>No pages yet, click here to add one</Label> }
              <Button icon='add' onClick={() => this.newItem(Defaults.PAGE_CONFIG, rootItemId)} disabled={!!this.props.formTag} />
            </Menu.Item>
          </Menu.Menu>
        </Menu>
        { pages && pages.size > 0 &&
          <Table attached='bottom'>
           <Table.Body>
              <Table.Row>
                <Table.Cell>
                  <Input transparent fluid placeholder='Page label' value={activePage.getIn(['label', this.props.language]) || ''} onChange={(evt) => this.props.updateItem(activePageId, 'label', evt.target.value, this.props.language)}/>
                </Table.Cell>
              </Table.Row>
              <Table.Row>
                <Table.Cell error={this.props.errors && this.props.errors.filter(e => e.get('type') === 'VISIBILITY' && e.get('itemId') === activePageId).size > 0}>
                  <CodeEditor value={activePage.get('activeWhen') || ''} onChange={value => this.props.updateItem(activePageId, 'activeWhen', value)} placeholder='Visibility' icon='eye' readOnly={this.props.formTag} errors={this.props.errors && this.props.errors.filter(e => e.get('type') === 'VISIBILITY' && e.get('itemId') === activePageId)} />
                </Table.Cell>
              </Table.Row>
            </Table.Body>
          </Table>
        }

        {this.createChildren(activePage, {parentItemId: activePageId, getItemById: this.getItemById, getValueset: this.getValueset}, this.props.itemEditors)}
        {
          activePage &&
            <Dropdown button text='Add item' disabled={!!this.props.formTag}>
              <Dropdown.Menu>
                <ItemTypeMenu categoryFilter={(category => category.type === 'structure')} onSelect={(config) => this.newItem(config, activePageId)}/>
              </Dropdown.Menu>
            </Dropdown>
        }
      </Segment>
    );
  }
}

const EditorConnected = connect(
  state => ({
    config: state.dialobComposer.config,
    items: state.dialobComposer.form && state.dialobComposer.form.get('data'),
    valueSets: state.dialobComposer.form && state.dialobComposer.form.get('valueSets'),
    activePageId: state.dialobComposer.editor && state.dialobComposer.editor.get('activePageId'),
    itemEditors:  state.dialobComposer.config && state.dialobComposer.config.itemEditors,
    formTag: state.dialobComposer.form.get('_tag'),
    language: (state.dialobComposer.editor && state.dialobComposer.editor.get('activeLanguage')) || Defaults.FALLBACK_LANGUAGE,
    errors: state.dialobComposer.editor && state.dialobComposer.editor.get('errors'),
    rootItemId: state.dialobComposer.editor.get('rootItemId')
  }),
  {
    setActivePage,
    addItem,
    deleteItem,
    loadForm,
    showItemOptions,
    showChangeId,
    copyItem,
    updateItem
  }
)(Editor);

export {
  EditorConnected as default,
  Editor
};
