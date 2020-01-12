import React, {Component} from 'react';
import {Modal, Button, Tab } from 'semantic-ui-react';
import {connect} from 'react-redux';
import {updateItem, hideRuleEdit} from '../actions';
import CodeEditor from './CodeEditor';
import Immutable from 'immutable';

const NOT_EDITABLE_TYPES = ['group', 'rowgroup', 'surveygroup', 'note'];
const TAB_PAGES = ['activeWhen', 'required'];

class RuleEditDialog extends Component {

  setAttribute(attribute, value, language = null) {
    this.props.updateItem(this.props.ruleEditOpen.get('itemId'), attribute, value, language);
  }

  getErrors() {
    const itemId = this.props.ruleEditOpen.get('itemId');
    return this.props.errors
    ? this.props.errors.filter(e => (e.get('message').startsWith('VALUESET_') && e.get('itemId') === this.props.item.get('valueSetId')) || e.get('itemId') === itemId)
    : new Immutable.List([]);
  }

  render() {
    const {ruleEditOpen, hideRuleEdit, getItem} = this.props;

    if (ruleEditOpen) {
      const item = getItem();

      const errors = this.getErrors();

      const visibilityErrors = errors.filter(e => e.get('type') === 'VISIBILITY');

      const panes = [
        {
          menuItem: {key: 'activeWhen', icon: 'eye', content: 'Visibility', color: visibilityErrors.size > 0 ? 'red' : 'black'},
          render: () => <Tab.Pane><CodeEditor active={true} styleClass='dialob-window-codeedit' value={item.get('activeWhen') || ''} onChange={value => this.setAttribute('activeWhen', value)} placeholder='Visibility' errors={visibilityErrors}/></Tab.Pane>
        }
      ];

      if (NOT_EDITABLE_TYPES.indexOf(item.get('type')) < 0) {
        const requirementErrors = errors.filter(e => e.get('type') === 'REQUIREMENT');
        panes.push( {
          menuItem: {key: 'required', icon: 'gavel', content: 'Requirement', color: requirementErrors.size > 0 ? 'red' : 'black'},
          render: () => <Tab.Pane><CodeEditor active={true} styleClass='dialob-window-codeedit' value={item.get('required') || ''} onChange={value => this.setAttribute('required', value)} placeholder='Required' errors={requirementErrors}/></Tab.Pane>
        });
      }

      return (
        <Modal open size='large' centered={false} closeOnEscape={true} onClose={() => hideRuleEdit()}>
          <Modal.Header>Rules for <em>{item.get('id')}</em></Modal.Header>
          <Modal.Content >
            <Tab panes={panes} defaultActiveIndex={TAB_PAGES.indexOf(ruleEditOpen.get('rule'))} />
          </Modal.Content>
          <Modal.Actions>
            <Button primary onClick={() => hideRuleEdit()}>OK</Button>
          </Modal.Actions>
        </Modal>
      );
    } else {
      return null;
    }
  }
}

const RuleEditDialogConnected = connect(
  state => ({
    ruleEditOpen: state.dialobComposer.editor && state.dialobComposer.editor.get('ruleEditOpen'),
    errors: state.dialobComposer.editor && state.dialobComposer.editor.get('errors'),
    get getItem() { return () => state.dialobComposer.form && state.dialobComposer.form.getIn(['data', this.ruleEditOpen.get('itemId')]); }
  }), {
    hideRuleEdit,
    updateItem,
  }
)(RuleEditDialog);

export {
  RuleEditDialogConnected as default,
  RuleEditDialog
};
