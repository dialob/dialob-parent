import React, {Component} from 'react';
import {Modal, Button, Tab} from 'semantic-ui-react';
import {connect} from 'react-redux';
import {hideVariables} from '../actions';

class VariablesDialog extends Component {

  render() {
    if (this.props.variablesOpen) {

      const tabs = [
        {menuItem: 'Context variables', render: () => <Tab.Pane></Tab.Pane>},
        {menuItem: 'Runtime variables', render: () => <Tab.Pane></Tab.Pane>}
      ];

      return (
        <Modal open>
          <Modal.Header>Variables</Modal.Header>
          <Modal.Content scrolling>
            <Tab panes={tabs} />
          </Modal.Content>
          <Modal.Actions>
            <Button primary onClick={() => this.props.hideVariables()}>OK</Button>
          </Modal.Actions>
        </Modal>
      );
    } else {
      return null;
    }
  }
}

const VariablesDialogConnected = connect(
  state => ({
    variablesOpen: state.editor && state.editor.get('variablesDialog')
  }), {
    hideVariables
  }
)(VariablesDialog);

export {
  VariablesDialogConnected as default,
  VariablesDialog
};
