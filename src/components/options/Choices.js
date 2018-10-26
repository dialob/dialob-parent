import React, {Component} from 'react';
import {Table, Button, Input, Dropdown, Form, Divider} from 'semantic-ui-react';
import {connect} from 'react-redux';
import {findValueset, isGlobalValueSet} from '../../helpers/utils';
import {createValueset, createValuesetEntry, updateValuesetEntry, deleteValuesetEntry, updateItem} from '../../actions';
import * as Defaults from '../../defaults';
import ValueSetEditor from '../ValueSetEditor';

class Choices extends Component {

  render() {
    if (!this.props.item.get('valueSetId') || isGlobalValueSet(this.props.globalValueSets, this.props.item.get('valueSetId'))) {
      let options = this.props.globalValueSets ? this.props.globalValueSets.map((vs, key) => ({text: vs.get('label') || 'untitled' + key, value: vs.get('valueSetId')})).toJS()
          : [];
      return (
        <React.Fragment>
          <Form.Field>
            <label>Select global valueset</label>
            <Dropdown fluid search selection options={options} value={this.props.item.get('valueSetId')}
              onChange={(evt, data) => this.props.updateItem(this.props.item.get('id'), 'valueSetId', data.value)} />
          </Form.Field>
          <Divider horizontal>Or</Divider>
          <Button onClick={() => this.props.createValueset(this.props.item.get('id'))}>Create local value set</Button>
        </React.Fragment>
      );
    } else  {
      return (<ValueSetEditor valueSetId={this.props.item.get('valueSetId')} />);
    }
  }
}

const ChoicesConnected = connect(
  state => ({
    globalValueSets: state.dialobComposer.form && state.dialobComposer.form.getIn(['metadata', 'composer', 'globalValueSets'])
  }), {
    createValueset,
    updateItem
  }
)(Choices);

export {
  ChoicesConnected as default,
  Choices
};
