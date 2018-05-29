import React, {Component} from 'react';
import {Message} from 'semantic-ui-react';
import {connect} from 'react-redux';
import * as Status from '../helpers/constants';
import {setActiveItem, showVariables} from '../actions';

class ErrorList extends Component {

  translateError(error) {
    let type, text = '';
    switch (error.get('type')) {
      case 'VARIABLE':
        type = 'Variable';
        break;
      case 'VISIBILITY':
        type = 'Visibility';
        break;
      case 'GENERAL':
        type = error.get('message') === 'INVALID_DEFAULT_VALUE' ? 'Default' : 'General';
        break;
      case 'REQUIREMENT':
        type = 'Requirement';
        break;
      case 'VALIDATION':
        type = 'Validation';
        break;
      default:
        type = error.get('type');
    };
    switch (error.get('message')) {
      case 'RB_VARIABLE_NEEDS_EXPRESSION':
        text = 'Missing expression';
        break;
      case 'INVALID_DEFAULT_VALUE':
        text = 'Invalid value';
        break;
      case 'UNKNOWN_VARIABLE':
        text = 'Unknown variable';
        break;
      case 'SYNTAX_ERROR':
        text = 'Syntax error';
        break;
    };
    return (
      <React.Fragment>
        <strong>{type}:</strong> {text}
      </React.Fragment>
    );
  }

  render() {
   if (this.props.errors) {
    let errorMap = this.props.errors.groupBy(e => e.get('itemId') || '$general$');
    let errors = errorMap.entrySeq().map((e, i) => <Message key={i} error>
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
    errors: state.editor && state.editor.get('errors'),
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
