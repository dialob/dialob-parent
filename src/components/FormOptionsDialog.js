import React, {Component} from 'react';
import {Modal, Button, Tab, List, Statistic, Form, Dropdown} from 'semantic-ui-react';
import {connect} from 'react-redux';
import {hideFormOptions, setMetadataValue} from '../actions';

const InformationPane = ({metadata, formId}) => {
  return (
    <Tab.Pane>
      <List divided>
        <List.Item>
          <List.Header>ID</List.Header>
          {formId}
        </List.Item>
        <List.Item>
          <List.Header>Created</List.Header>
          {metadata.get('created')}
        </List.Item>
        <List.Item>
          <List.Header>Stats</List.Header>
          <Statistic.Group size='mini'>
            <Statistic value={0} label='Items' />
            <Statistic value={0} label='Value sets' />
            <Statistic value={0} label='Variables' />
          </Statistic.Group>
        </List.Item>
      </List>
    </Tab.Pane>
  );
};

const OptionsPane = ({metadata, onChange}) => {
  const options = metadata.get('labels') ? metadata.get('labels').toJS().map(c => ({key: c, text: c, value: c})) : [];
  return (
    <Tab.Pane>
      <Form>
        <Form.Field>
          <Form.Input fluid label='Dialog name' value={metadata.get('label') || ''} onChange={(evt) => onChange('label', evt.target.value)} />
        </Form.Field>
        <Form.Field>
          <label>Labels</label>
          <Dropdown allowAdditions fluid multiple search selection
          options={options} value={metadata.get('labels') ? metadata.get('labels').toJS() : []} onChange={(evt, data) => onChange('labels', data.value)} />
        </Form.Field>
        <Form.Field>
          <Form.Input fluid label='Default submit URL' value={metadata.get('defaultSubmitUrl') || ''} onChange={(evt) => onChange('defaultSubmitUrl', evt.target.value)} />
        </Form.Field>
        <Form.Field>
          <Form.Checkbox label='Show inactive elements during filling' checked={metadata.get('showDisabled')} onChange={(evt, data) => onChange('showDisabled', data.checked)} />
        </Form.Field>
      </Form>
    </Tab.Pane>
  );
};

class FormOptionsDialog extends Component {

  render() {
    if (this.props.formOptions) {

      const tabs = [
        {menuItem: 'Options', render: () => <OptionsPane metadata={this.props.metadata} onChange={(attr, value) => this.props.setMetadataValue(attr, value) } />},
        {menuItem: 'Information', render: () => <InformationPane metadata={this.props.metadata} formId={this.props.formId} />}
      ];

      return (
        <Modal open>
          <Modal.Header>Dialog Options</Modal.Header>
          <Modal.Content scrolling>
            <Tab panes={tabs} />
          </Modal.Content>
          <Modal.Actions>
            <Button primary onClick={() => this.props.hideFormOptions()}>OK</Button>
          </Modal.Actions>
        </Modal>
      );
    } else {
      return null;
    }
  }
}

const FormOptionsDialogConnected = connect(
  state => ({
    formOptions: state.editor && state.editor.get('formOptions'),
    metadata:  state.form && state.form.get('metadata'),
    formId: state.form && state.form.get('_id')
  }), {
    hideFormOptions,
    setMetadataValue
  }
)(FormOptionsDialog);

export {
  FormOptionsDialogConnected as default,
  FormOptionsDialog
};
