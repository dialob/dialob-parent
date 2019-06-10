import React, {Component} from 'react';
import {Modal, Button, Tab, List, Statistic, Form, Dropdown} from 'semantic-ui-react';
import {connect} from 'react-redux';
import {hideFormOptions, setMetadataValue} from '../actions';
import moment from 'moment';

const InformationPane = ({metadata, formId, formName, stats}) => {
  return (
    <Tab.Pane>
      <List divided>
        <List.Item>
          <List.Header>ID</List.Header>
          {formId}
        </List.Item>
        <List.Item>
          <List.Header>Technical name</List.Header>
          {formName}
        </List.Item>
        <List.Item>
          <List.Header>Created</List.Header>
          {moment(metadata.get('created')).format('LLLL')}
        </List.Item>
        <List.Item>
          <List.Header>Last saved</List.Header>
          {moment(metadata.get('lastSaved')).fromNow()}
        </List.Item>
        <List.Item>
          <List.Header>Stats</List.Header>
          <Statistic.Group size='mini' items={stats} />
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
          <Form.Checkbox label='Show inactive pages during filling' checked={metadata.get('showDisabled')} onChange={(evt, data) => onChange('showDisabled', data.checked)} />
        </Form.Field>
      </Form>
    </Tab.Pane>
  );
};

class FormOptionsDialog extends Component {

  render() {
    const {formOptions, form} = this.props;
    if (formOptions) {
      const metadata = form.get('metadata');
      const items = form.get('data');
      const valueSets = form.get('valueSets');
      const variables = form.get('variables');
      const formId = form.get('_id');
      const formName = form.get('name');

      const tabs = [
        {menuItem: 'Options', render: () => <OptionsPane metadata={metadata} onChange={(attr, value) => this.props.setMetadataValue(attr, value) } />},
        {menuItem: 'Information', render: () => <InformationPane
           metadata={metadata}
           formId={formId}
           formName={formName}
           stats={[
            { key: 'items', label: 'Items', value: items.size },
            { key: 'valueSets', label: 'Lists', value: valueSets ? valueSets.size : 0 },
            { key: 'vars', label: 'Variables', value: variables ? variables.size : 0 }
           ]}
           />}
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
    formOptions: state.dialobComposer.editor && state.dialobComposer.editor.get('formOptions'),
    form: state.dialobComposer.form
  }), {
    hideFormOptions,
    setMetadataValue
  }
)(FormOptionsDialog);

export {
  FormOptionsDialogConnected as default,
  FormOptionsDialog
};
