import React, {Component} from 'react';
import {Segment} from 'semantic-ui-react';
import {connect} from 'react-redux';
import {updateItem} from '../../actions'
import * as Defaults from '../../defaults';
import RichEditor from '../RichEditor';

class Description extends Component {

  render() {
    const value = this.props.item.getIn(['description', this.props.language]) || '';
    return (
      <Segment>
        <RichEditor active={true} id={`dscr_${this.props.item.get('id')}`} onChange={(v) => this.props.setAttribute('description', v, this.props.language)} defaultValue={value} placeholder='Enter description for this item' />
      </Segment>
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
