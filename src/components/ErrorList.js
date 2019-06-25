import React, {Component} from 'react';
import {Message} from 'semantic-ui-react';
import {connect} from 'react-redux';
import {setActiveItem, showVariables, showValueSets} from '../actions';
import { translateErrorType, translateErrorMessage } from '../helpers/utils';

class ErrorList extends Component {

  translateError(error) {
    const type = translateErrorType(error);
    const text = translateErrorMessage(error);
    return (
      <React.Fragment>
        {type && <strong>{type}: </strong>}
        {text}
      </React.Fragment>
    );
  }

  resolveItemId(error) {
    if (error.get(0).get('message').startsWith('VALUESET_')) {
      const item = this.props.items.find(i => i.get('valueSetId') === error.get(0).get('itemId'));
      if (item) { 
        return item.get('id');
      } else {
        return null;
      }
    }
    return error.get(0).get('itemId');
  }

  clickHandler(error, itemId) {
    console.log('CLK', error, itemId);
    if (error.getIn([0, 'type']) === 'VARIABLE') {
      this.props.showVariables()
    } else if (itemId) {
      this.props.setActiveItem(itemId);
    } else if (!itemId && error.getIn([0, 'message']).startsWith('VALUESET_')) {
      this.props.showValueSets();
    }
  }

  render() {
   if (this.props.errors) {
    let errorMap = this.props.errors.groupBy(e => e.get('itemId') || '$general$');
    let errors = errorMap.entrySeq().map((e, i) => {
      const uiItemId = this.resolveItemId(e[1]);
      return (<Message key={i} error={e[1].getIn([0, 'level']) != 'WARNING'} warning={e[1].getIn([0, 'level']) === 'WARNING'}>
        <Message.Header className='composer-error-link' onClick={this.clickHandler.bind(this, e[1], uiItemId)}>{uiItemId ? uiItemId : 'Global list'}</Message.Header>
        <Message.List>
          {
            e[1].map((m, j) => <Message.Item key={j}>{this.translateError(m)}</Message.Item>)
          }
        </Message.List>
      </Message>);
      }
    );

    return (<React.Fragment>
      {errors}
    </React.Fragment>);
   } else {
     return null;
   }
  }
}

const ErrorListConnected = connect(
  state => ({
    errors: state.dialobComposer.editor && state.dialobComposer.editor.get('errors'),
    items: state.dialobComposer.form && state.dialobComposer.form.get('data'),
    valueSets: state.dialobComposer.form && state.dialobComposer.form.get('valueSets')
  }),
  {
    setActiveItem,
    showVariables,
    showValueSets
  }
)(ErrorList);

export {
  ErrorList,
  ErrorListConnected as default
}
