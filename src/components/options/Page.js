import React, {Component} from 'react';
import {Dropdown, Segment, Form, TextArea, Label} from 'semantic-ui-react';
import {connect} from 'react-redux';
import {updateItem, showChangeId} from '../../actions'
import { translateErrorMessage } from '../../helpers/utils';

class Page extends Component {

  onChange(attribute, v, language = null) {
    this.props.setAttribute(attribute, v, language);
  }

  getErrors() {
    return this.props.errors
      ? this.props.errors.filter(e => e.get('itemId') === this.props.item.get('id'))
      : [];
  }

  renderErrors() {
    return this.getErrors().map((e, i) => <React.Fragment key={i}>
    <span>{translateErrorMessage(e)}</span><br />
    </React.Fragment>);
  }

  render() {
    const className = this.props.item.get('className');
    const value = className ? className : [];
    const options = className ? className.map(c => ({key: c, text: c, value: c})) : [];
    return (
      <Form>
        <Form.Field>
          <Form.Input fluid label='Label' value={this.props.item.getIn(['label', this.props.language]) || ''} onChange={(evt) => this.onChange('label', evt.target.value, this.props.language)} />
        </Form.Field>
        <Form.Field error={this.getErrors().size > 0}>
          <label>Visibility rule</label>
          <TextArea autoHeight value={this.props.item.get('activeWhen') || ''} onChange={(evt, data) => this.onChange('activeWhen', data.value)} />
           { this.getErrors().size > 0 && <Label basic color='red' pointing>{this.renderErrors()}</Label> }
        </Form.Field>
      </Form>
    );
  }
}

const PageConnected = connect(
  state => ({
    language: (state.editor && state.editor.get('activeLanguage')) || Defaults.FALLBACK_LANGUAGE,
    errors: state.editor && state.editor.get('errors')
  }),
  (dispatch, props) => ({
    setAttribute: (attribute, value, language = null) => dispatch(updateItem(props.item.get('id'), attribute, value, language)),
    changeId: (id) => dispatch(showChangeId(id))
  })
)(Page);

export {
  PageConnected as default,
  Page
};
