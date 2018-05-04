import React, {Component} from 'react';
import {Message} from 'semantic-ui-react';
import {connect} from 'react-redux';
import * as Status from '../helpers/constants';

class ErrorList extends Component {

  render() {
   if (this.props.errors) {
    let errors = this.props.errors.map((e, i) => <Message key={i} error header={e.get('message')}/>)
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
  }
)(ErrorList);

export {
  ErrorList,
  ErrorListConnected as default
}
