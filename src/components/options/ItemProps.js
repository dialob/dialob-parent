import React, {Component} from 'react';
import {Table, Button, Input, Form} from 'semantic-ui-react';
import {addItemProp, updateItemProp, deleteItemProp} from '../../actions';
import {connect} from 'react-redux';

class ItemProps extends Component {

  constructor(props) {
    super(props);
    this.state = {
      newItemKey: ''
    };
  }

  propEditor(prop, key) {
    const itemId = this.props.item.get('id');
    return (
      <Table.Row key={key}>
        <Table.Cell collapsing>
          <Button size='tiny' icon='remove' onClick={() => this.props.deleteItemProp(itemId, prop[0])} />
        </Table.Cell>
        <Table.Cell>
          {prop[0]}
        </Table.Cell>
        <Table.Cell>
           <Input transparent fluid value={prop[1] || ''} onChange={(evt) => this.props.updateItemProp(itemId, prop[0], evt.target.value)} />
        </Table.Cell>
      </Table.Row>);
  }

  addKey(key) {
    this.props.addItemProp(this.props.item.get('id'), this.state.newItemKey);
    this.setState({newItemKey: ''});
  }

  render() {
    const rows = this.props.item.get('props') && this.props.item.get('props').entrySeq()
      .map((p, i) => this.propEditor(p, i));
    return (
      <React.Fragment>
        <Form>
          <Form.Field>
            <Form.Input fluid label='New property' value={this.state.newItemKey || ''} onChange={(evt) => this.setState({newItemKey: evt.target.value})} action={{content: 'Add', onClick: () => this.addKey(this.state.newItemKey)}} />
          </Form.Field>
        </Form>
        <Table celled>
          <Table.Header>
            <Table.Row>
              <Table.HeaderCell collapsing></Table.HeaderCell>
              <Table.HeaderCell>Name</Table.HeaderCell>
              <Table.HeaderCell>Value</Table.HeaderCell>
            </Table.Row>
          </Table.Header>
          <Table.Body>
            {rows}
          </Table.Body>
        </Table>
      </React.Fragment>
    );
  }
}

const ItemPropsConnected = connect(
  state => ({
  }), {
    addItemProp,
    updateItemProp,
    deleteItemProp
  }
)(ItemProps);

export {
  ItemPropsConnected as default,
  ItemProps
};
