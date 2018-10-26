import React, {Component} from 'react';
import {Form, TextArea} from 'semantic-ui-react';
import {connect} from 'react-redux';
import {updateItem} from '../../actions'
import * as Defaults from '../../defaults';

class Description extends Component {

  onChange(v) {
    this.props.setAttribute('description', v.value, this.props.language);
  }

  render() {
    const value = this.props.item.getIn(['description', this.props.language]) || '';
    return (
      <Form>
        <Form.Field>
        <label>Enter description for this item</label>
        <TextArea autoHeight onChange={(evt, data) => this.onChange(data)} value={value} />
        </Form.Field>
      </Form>
    );
  }
}

const DescriptionConnected = connect(
  state => ({
    language: (state.dialobComposer.editor && state.dialobComposer.editor.get('activeLanguage')) || Defaults.FALLBACK_LANGUAGE,
  }),
  (dispatch, props) => ({
    setAttribute: (attribute, value, language = null) => dispatch(updateItem(props.item.get('id'), attribute, value, language)),
  })
)(Description);

export {
  DescriptionConnected as default,
  Description
};
