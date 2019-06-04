import React, {Component} from 'react';
import {Message} from 'semantic-ui-react';
import {connect} from 'react-redux';
import {setActiveItem, showVariables} from '../actions';
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
    if (error.get('message').startsWith('VALUESET_')) {}
  }

  render() {
   if (this.props.errors) {
    let errorMap = this.props.errors.groupBy(e => e.get('itemId') || '$general$');
    let errors = errorMap.entrySeq().map((e, i) => <Message key={i} error={e[1].getIn([0, 'level']) != 'WARNING'} warning={e[1].getIn([0, 'level']) === 'WARNING'}>
      <Message.Header onClick={() => e[1].getIn([0, 'type']) === 'VARIABLE' ? this.props.showVariables() : this.props.setActiveItem(e[0])}>{e[0]}</Message.Header>
      <Message.List>
        {
          e[1].map((m, j) => <Message.Item key={j}>{this.translateError(m)}</Message.Item>)
        }
      </Message.List>
    </Message>);

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
    showVariables
  }
)(ErrorList);

export {
  ErrorList,
  ErrorListConnected as default
}
