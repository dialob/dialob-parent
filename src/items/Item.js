import React, {Component} from 'react';
import {itemFactory} from '.';
import {connect} from 'react-redux';
import {setActiveItem, addItem, updateItem, deleteItem, showChangeId, setActivePage, setTreeCollapse} from '../actions';
import * as Defaults from '../defaults';
import {findValueset} from '../helpers/utils';
import Immutable from 'immutable';

class Item extends Component {

  createChildren(props, config) {
    return this.props.item.get('items') && this.props.item.get('items')
      .map(itemId => this.props.items.get(itemId))
      .map(item => itemFactory(item, props, config || this.props.itemEditors));
  }

  getErrors() {
    return this.props.errors
      ? this.props.errors.filter(e => e.get('itemId') === this.props.item.get('id'))
      : new Immutable.List([]);
  }
}

function connectItem(component) {
  return connect(
    (state, props) => ({
      items: state.dialobComposer.form && state.dialobComposer.form.get('data'),
      active: props.item && state.dialobComposer.editor && props.item.get('id') === state.dialobComposer.editor.get('activeItemId'),
      language: (state.dialobComposer.editor && state.dialobComposer.editor.get('activeLanguage')) || Defaults.FALLBACK_LANGUAGE,
      errors: state.dialobComposer.editor && state.dialobComposer.editor.get('errors'),
      itemEditors: state.dialobComposer.config.itemEditors,
      editable: !state.dialobComposer.form.get('_tag'),
      treeCollapsed: state.dialobComposer.editor && state.dialobComposer.editor.get('treeCollapse') && state.dialobComposer.editor.get('treeCollapse').findIndex(id => id === props.item.get('id')) > -1,
      rootItemId: state.dialobComposer.editor.get('rootItemId'),
      get getValueset() { return (valueSetId) => findValueset(state.dialobComposer.form, valueSetId); },
    }),
    (dispatch, props) => ({
      setActive: () => dispatch(setActiveItem(props.item.get('id'))),
      newItem: (config, parentItemId, afterItemId) => dispatch(addItem(config, parentItemId, afterItemId)),
      setAttribute: (attribute, value, language = null) => dispatch(updateItem(props.item.get('id'), attribute, value, language)),
      delete: () => dispatch(deleteItem(props.item.get('id'))),
      changeId: () => dispatch(showChangeId(props.item.get('id'))),
      setActivePage: (pageId) => dispatch(setActivePage(pageId)),
      setTreeCollapsed: collapsed => dispatch(setTreeCollapse(props.item.get('id'), collapsed))
    })
  )(component);
}

export {
  Item as default,
  connectItem
};