import React, {Component} from 'react';
import {Table, Segment, Label, Button, Form, Header} from 'semantic-ui-react';
import {connect} from 'react-redux';
import {createValidation, deleteValidation, updateValidation} from '../actions';
import * as Defaults from '../defaults';
import CodeEditor from '../components/CodeEditor';

const ValidationRule = ({onChangeMessage, onChangeRule, onRemove, message, rule, getErrors}) => {
  return (
    <Segment>
      <Label as='a' ribbon='right' icon='remove' onClick={onRemove} />
      <Form>
        <Form.Input label='Message' icon='exclamation circle' fluid value={message || ''} onChange={onChangeMessage}/>
        <Form.Field label='Validation rule' error={getErrors().size > 0}>
          <CodeEditor value={rule || ''} onChange={onChangeRule} icon='check'/>
        </Form.Field>
      </Form>
    </Segment>
   );
};

class Validations extends Component {

  getErrors(index) {
    return this.props.errors
      ? this.props.errors.filter(e => e.get('type') === 'VALIDATION' && e.get('itemId') === this.props.item.get('id') && e.get('index') === index)
      : [];
  }

  render() {
    const validations = this.props.item.get('validations');
    const items = validations ? validations.map((v, index) => <ValidationRule
        key={index}
        message={v.getIn(['message', this.props.language])}
        rule={v.get('rule')}
        onChangeMessage={(evt) => this.props.changeValidation(index, 'message', evt.target.value, this.props.language)}
        onChangeRule={(value) => this.props.changeValidation(index, 'rule', value)}
        onRemove={() => this.props.removeValidation(index)}
        getErrors={this.getErrors.bind(this, index)}
       /> ) : [];
    return (
      <Table celled attached='bottom' onClick={(evt) => evt.stopPropagation()}>
        <Table.Body>
          <Table.Row>
            <Table.Cell>
              <Header as='h5'>Validation rules</Header>
              {items}
              <Button onClick={() => this.props.newValidation(this.props.language)}>Add validation rule</Button>
            </Table.Cell>
          </Table.Row>
        </Table.Body>
      </Table>
    );
  }
}

const ValidationsConnected = connect(
  state => ({
    language: (state.dialobComposer.editor && state.dialobComposer.editor.get('activeLanguage')) || Defaults.FALLBACK_LANGUAGE,
    errors: state.dialobComposer.editor && state.dialobComposer.editor.get('errors')
  }),
  (dispatch, props) => ({
    newValidation: (language) => dispatch(createValidation(props.item.get('id'), language)),
    removeValidation: (index) => dispatch(deleteValidation(props.item.get('id'), index)),
    changeValidation: (index, attribute, value, language) => dispatch(updateValidation(props.item.get('id'), index, attribute, value, language))
  })
)(Validations);

export {
  Validations,
  ValidationsConnected as default
}
