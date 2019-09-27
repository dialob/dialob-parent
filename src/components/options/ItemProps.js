import React, {Component} from 'react';
import {Table, Button, Input, Form} from 'semantic-ui-react';
import {addItemProp, updateItemProp, deleteItemProp} from '../../actions';
import {connect} from 'react-redux';
import {InputProp} from '../propEditors';
import {findItemTypeConfig} from '../../helpers/utils';

class ItemProps extends Component {

  constructor(props) {
    super(props);
    this.state = {
      newItemKey: '',
      itemTypeConfig: null,
      definedProps: []
    };
  }

  propEditor(prop, key, itemTypeConfig) {
    const itemId = this.props.item.get('id');
    const editorConfig = itemTypeConfig && itemTypeConfig.propEditors && itemTypeConfig.propEditors[prop[0]];

    const editor = (editorConfig && editorConfig.component) ?
        {
          component: editorConfig.component,
          props: editorConfig.props
        }
      : {
        component: InputProp,
        props: {}
      };

    return (
      <Table.Row key={key}>
        <Table.Cell collapsing>
          <Button size='tiny' icon='remove' onClick={() => this.props.deleteItemProp(itemId, prop[0])} />
        </Table.Cell>
        <Table.Cell collapsing>
          {prop[0]}
        </Table.Cell>
        <Table.Cell>
          <editor.component value={prop[1]} name={prop[0]} item={this.props.item} onChange={(v) => this.props.updateItemProp(itemId, prop[0], v)} {...editor.props} />
        </Table.Cell>
      </Table.Row>);
  }

  addKey(key) {
    if (!key || key.trim().length === 0)  {
      return;
    }

    const defaultValue = this.state.itemTypeConfig &&
                         this.state.itemTypeConfig.config &&
                         this.state.itemTypeConfig.config.props ?
                         this.state.itemTypeConfig.config.props[key]
                         : null

    this.props.addItemProp(this.props.item.get('id'), this.state.newItemKey, defaultValue);
    this.setState({newItemKey: ''});
  }

  componentDidMount() {
    const {itemTypes, item} = this.props;
    const itemTypeConfig = findItemTypeConfig(itemTypes, item.get('view') || item.get('type'));
    const definedProps = itemTypeConfig && itemTypeConfig.propEditors
      ? Object.keys(itemTypeConfig.propEditors).map(k => ({ key: k, text: k, value: k }))
    : [];
    this.setState({itemTypeConfig, definedProps});
  }

  addDefinedProp(prop) {
    const newProps = this.state.definedProps.concat([{key: prop, text: prop, value: prop}]);
    this.setState({definedProps: newProps});
  }

  render() {
    const {item} = this.props;
    const {itemTypeConfig, definedProps, newItemKey} = this.state;
    const props = item.get('props') && item.get('props');
    const rows = props ? props.entrySeq().map((p, i) => this.propEditor(p, i, itemTypeConfig)) : [];
    const suggestions = props ? definedProps.filter(p => !props.has(p.value)) : [];

    return (
      <React.Fragment>
        <Form>
          <Form.Group>
              <Form.Dropdown fluid search selection width={14} allowAdditions label='New property' options={suggestions} value={this.state.newItemKey} onAddItem={(_, data) => this.addDefinedProp(data.value)} onChange={(_, data) => this.setState({newItemKey: data.value})} />
              <Form.Button width={2} fluid label={'\u00A0'} disabled={!newItemKey} onClick={() => this.addKey(newItemKey)}>Add</Form.Button>
          </Form.Group>
        </Form>
        <Table celled compact>
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
    itemTypes:  state.dialobComposer.config.itemTypes
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
