import React, {Component} from 'react';
import {Segment, Form} from 'semantic-ui-react';
import {connect} from 'react-redux';
import {updateItem} from '../../actions';

class GenericPropEditor extends Component {
  render() {
    const {item, updateItem} = this.props;
    return (
      <Segment>
        <Form>
          <Form.Field>
            <Form.Input fluid label='Additional attribute' value={item.get('attr') || ''} onChange={(evt) =>
                 updateItem(item.get('id'), 'attr', evt.target.value, null)} />
          </Form.Field>
        </Form>
      </Segment>
    );
  }
}

const GenericPropEditorConnected = connect(
  state => ({
  }), {
    updateItem
  }
)(GenericPropEditor);

export {
  GenericPropEditorConnected as default,
  GenericPropEditor
};
