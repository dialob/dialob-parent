import React, {Component} from 'react';
import {Modal, Button, Tab, Segment, Input} from 'semantic-ui-react';
import {connect} from 'react-redux';
import {hideValueSets, createValueset, setGlobalValuesetName} from '../actions';
import ValueSetEditor from './ValueSetEditor';

const VSEdit = ({valueSetId, label, onChangeName}) => {
  return (
    <Segment>
      <Input fluid value={label || ''} placeholder = 'List name' onChange={(evt) => onChangeName(valueSetId, evt.target.value)} />
      <ValueSetEditor valueSetId={valueSetId} />
    </Segment>
  );
};

class ValueSetDialog extends Component {

  render() {
    if (this.props.valueSetsOpen) {

      const panes = this.props.globalValueSets ? this.props.globalValueSets.map((gvs, key) => ({menuItem: gvs.get('label') || 'untitled' + key, render: () => <VSEdit key={key} valueSetId={gvs.get('valueSetId')} label={gvs.get('label')} onChangeName={this.props.setGlobalValuesetName} />})).toJS()
        : [];

      return (
        <Modal open size='large'>
          <Modal.Header>Global Lists</Modal.Header>
          <Modal.Content scrolling>
            <Tab menu={{fluid: true, vertical: true, pointing: true}} menuPosition='left' panes={panes} />
            <Button onClick={() => this.props.createValueset()}>Add new list</Button>
          </Modal.Content>
          <Modal.Actions>
            <Button primary onClick={() => this.props.hideValueSets()}>OK</Button>
          </Modal.Actions>
        </Modal>
      );
    } else {
      return null;
    }
  }
}

const ValueSetDialogConnected = connect(
  state => ({
    valueSetsOpen: state.editor && state.editor.get('valueSetsOpen'),
    globalValueSets: state.form && state.form.getIn(['metadata', 'composer', 'globalValueSets'])
  }), {
    hideValueSets,
    createValueset,
    setGlobalValuesetName
  }
)(ValueSetDialog);

export {
  ValueSetDialogConnected as default,
  ValueSetDialog
};
