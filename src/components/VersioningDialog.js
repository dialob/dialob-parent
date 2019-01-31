import React, { Component } from "react";
import { Modal, Button, List, Header, Loader, Segment } from "semantic-ui-react";
import { connect } from "react-redux";
import { hideVersioning, fetchVersions } from "../actions";

const VersionItem = ({name, created, icon}) => {
  return (
    <List.Item>
      <List.Content floated='right'>
        <Button size='tiny'>Activate</Button>
      </List.Content>
      <List.Icon name={icon} size='large' verticalAlign='middle' />
      <List.Content>
        <List.Header>{name}</List.Header>
        <List.Description>{created}</List.Description>
      </List.Content>
    </List.Item>
  );
}

class VersioningDialog extends Component {

  componentWillUpdate(nextProps) {
    if (!this.props.versioningOpen && nextProps.versioningOpen && this.props.fetchVersions) {
      this.props.fetchVersions();
    }
  }

  renderVersions() {
   if (!this.props.versions) {
      return <Loader inline='centered' active/>;
   }

   let items = this.props.versions.map((v, i) => <VersionItem key={i} name={v.get('name')} created={v.get('created')} icon='tag' />);
   items = items.push(<VersionItem key='latest' name='LATEST' created='----' icon='edit' />);

   return (
    <List celled selection>
      {items.reverse()}
    </List>
   );
  }

  /*

            <Label as='a' tag>
      New
      <Label.Detail>xxxx</Label.Detail>
    </Label>
    <Label as='a' color='red' tag>
      Upcoming
      <Label.Detail>xxxx</Label.Detail>
    </Label>
    <Label as='a' color='teal' tag>
      Featured
      <Label.Detail>xxxx</Label.Detail>
       <Icon name='delete' />
    </Label>
            */


  render() {
    if (this.props.versioningOpen) {
      return (
        <Modal open size='small'>
          <Modal.Header>Versioning</Modal.Header>
          <Modal.Content scrolling>
          <Header as='h4'>List of tagged versions</Header>
          <p>Only the <strong>LATEST</strong> version is editable!</p>
            {this.renderVersions()}
          </Modal.Content>
          <Modal.Actions>
            <Button primary onClick={() => this.props.hideVersioning()}>
              OK
            </Button>
          </Modal.Actions>
        </Modal>
      );
    } else {
      return null;
    }
  }
}

const VersioningDialogConnected = connect(
  state => ({
    versioningOpen:
      state.dialobComposer.editor &&
      state.dialobComposer.editor.get("versioningDialog"),
    versions:
      state.dialobComposer.editor &&
      state.dialobComposer.editor.get("versions"),
  }),
  {
    hideVersioning,
    fetchVersions
  }
)(VersioningDialog);

export { VersioningDialogConnected as default, VersioningDialog };
