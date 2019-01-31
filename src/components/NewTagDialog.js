import React, { Component } from "react";
import { Modal, Button, List, Header, Loader, Segment, Form } from "semantic-ui-react";
import { connect } from "react-redux";
import { hideNewTag, createNewTag } from "../actions";

class NewTagDialog extends Component {

  constructor(props) {
    super(props);
    this.state = {
      tag: ''
    };
  }

  componentWillUpdate(nextProps) {
    if (!this.props.newTagOpen && nextProps.newTagOpen) {
      this.setState({tag: ''});
    }
  }

  render() {
    if (this.props.newTagOpen) {
      return (
        <Modal open size='tiny'>
          <Modal.Header>New Tag</Modal.Header>
          <Modal.Content>
            <Form>
              <Form.Field>
                <Form.Input label='New tag name' fluid focus value={this.state.tag} onChange={(e, d) => this.setState({tag: d.value}) }/>
              </Form.Field>
            </Form>
          </Modal.Content>
          <Modal.Actions>
            <Button primary onClick={() => this.props.createNewTag(this.state.tag)}>
              OK
            </Button>
            <Button onClick={() => this.props.hideNewTag()}>
              Cancel
            </Button>
          </Modal.Actions>
        </Modal>
      );
    } else {
      return null;
    }
  }
}

const NewTagDialogConnected = connect(
  state => ({
    newTagOpen:
      state.dialobComposer.editor &&
      state.dialobComposer.editor.get('newTagDialog')
  }),
  {
    hideNewTag,
    createNewTag
  }
)(NewTagDialog);

export { NewTagDialogConnected as default, NewTagDialog };
