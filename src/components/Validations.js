import React, {Component} from 'react';
import {Table, Segment, Label, Icon, Button, Input, Form, Header} from 'semantic-ui-react';
import {connect} from 'react-redux';
import {createValidation, deleteValidation, updateValidation} from '../actions';
import * as Defaults from '../defaults';
import { METHODS } from 'http';

const ValidationRule = ({onChangeMessage, onChangeRule, onRemove, message, rule, getErrors}) => {
  return (
    <Segment>
      <Label as='a' ribbon='right' icon='remove' onClick={onRemove} />
      <Form>
        <Form.Input label='Message' icon='exclamation circle' fluid value={message || ''} onChange={onChangeMessage}/>
        <Form.Input label='Validation rule' icon='check' fluid value={rule || ''} onChange={onChangeRule} error={getErrors().size > 0}/>
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
        onChangeRule={(evt) => this.props.changeValidation(index, 'rule', evt.target.value)}
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
    language: (state.editor && state.editor.get('activeLanguage')) || Defaults.FALLBACK_LANGUAGE,
    errors: state.editor && state.editor.get('errors')
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
