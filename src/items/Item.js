import React from 'react';
import {itemFactory} from '.';
import {connect} from 'react-redux';
import {setActiveItem, addItem, updateItem, deleteItem, showChangeId, setActivePage, setTreeCollapse} from '../actions';
import * as Defaults from '../defaults';

import Immutable from 'immutable';

class Item extends React.Component {

  constructor(props) {
    super(props);
    this.deleteItem = this.props.deleteItem.bind(this, props.itemId);
  }

  createChildren(props, config) {
    return this.props.item.get('items') && this.props.item.get('items')
      .map(itemId => this.props.getItemById(itemId))
      .map(item => itemFactory(item, props, config || this.props.itemEditors));
  }

  getErrors() {
    return this.props.errors
      ? this.props.errors.filter(e => e.get('itemId') === this.props.itemId)
      : new Immutable.List([]);
  }

  setActive() {
    this.props.setActiveItem(this.props.itemId);
  }

  newItem(config, parentItemId, afterItemId) {
    this.props.addItem(config, parentItemId, afterItemId);
  }

  setAttribute(attribute, value, language = null) {
    this.props.updateItem(this.props.itemId, attribute, value, language);
  }

  changeId() {
    this.props.showChangeId(this.props.itemId);
  }

  setActivePage(pageId) {
    this.props.setActivePage(pageId);
  }

  setTreeCollapsed(collapsed) {
    this.props.setTreeCollapse(this.props.itemId, collapsed);
  }

}

function connectItem(component) {
  return connect(
    (state, props) => {
      const item = state.dialobComposer.form.getIn(['data', props.itemId]);
      return ({
        item,
        active: item && state.dialobComposer.editor && props.itemId === state.dialobComposer.editor.get('activeItemId'),
        language: (state.dialobComposer.editor && state.dialobComposer.editor.get('activeLanguage')) || Defaults.FALLBACK_LANGUAGE,
        errors: state.dialobComposer.editor && state.dialobComposer.editor.get('errors'),
        itemEditors: state.dialobComposer.config.itemEditors,
        editable: !state.dialobComposer.form.get('_tag'),
        treeCollapsed: state.dialobComposer.editor && state.dialobComposer.editor.get('treeCollapse') && state.dialobComposer.editor.get('treeCollapse').findIndex(id => id === props.itemId) > -1,
        rootItemId: state.dialobComposer.editor.get('rootItemId'),
        validations: item && item.get('validations')
      });
    }
    ,
    {
      setActiveItem,
      addItem,
      updateItem,
      deleteItem,
      showChangeId,
      setActivePage,
      setTreeCollapse
    }
  )(component);
}

export {
  Item as default,
  connectItem
};