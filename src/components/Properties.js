import React, {Component} from 'react';
import {Segment, Header, Table, Input} from 'semantic-ui-react';
import {connect} from 'react-redux';

class Properties extends Component {
  render() {
    let activeItem = this.props.activeItemId && this.props.items && this.props.items.get(this.props.activeItemId);
    if (!activeItem) {
      return null;
    }
    return (
      <Segment basic>
        <Header as='h4' attached='top'>{activeItem.getIn(['label', 'en'])}</Header>
        <Table celled definition attached>
          <Table.Body>
            <Table.Row>
              <Table.Cell collapsing>
                ID
              </Table.Cell>
              <Table.Cell>
                <Input transparent fluid icon={{name: 'edit', link: true}} defaultValue={activeItem.get('id')}/>
              </Table.Cell>
            </Table.Row>
            <Table.Row>
              <Table.Cell collapsing>
                Type
              </Table.Cell>
              <Table.Cell>
                <Input transparent fluid defaultValue={activeItem.get('type')} />
              </Table.Cell>
            </Table.Row>
            <Table.Row>
              <Table.Cell collapsing>
                Default
              </Table.Cell>
              <Table.Cell>
                <Input transparent fluid defaultValue={activeItem.get('defaultValue')} />
              </Table.Cell>
            </Table.Row>
            <Table.Row>
              <Table.Cell collapsing>
                Visibility
              </Table.Cell>
              <Table.Cell>
                <Input transparent fluid icon={{name: 'edit', link: true}} defaultValue={activeItem.get('activeWhen')}/>
              </Table.Cell>
            </Table.Row>
            <Table.Row>
              <Table.Cell collapsing>
                Required
              </Table.Cell>
              <Table.Cell>
                <Input transparent fluid icon={{name: 'edit', link: true}} defaultValue={activeItem.get('required')}/>
              </Table.Cell>
            </Table.Row>
          </Table.Body>
        </Table>
      </Segment>
    );
  }
}

const PropertiesConnected = connect(
  state => ({
    activeItemId: state.editor && state.editor.get('activeItemId'),
    items: state.form && state.form.get('data')
  }),
  {}
)(Properties);

export {
  Properties,
  PropertiesConnected as default
}