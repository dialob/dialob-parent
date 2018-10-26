import React, {Component} from 'react';
import {Icon, Loader} from 'semantic-ui-react';
import {connect} from 'react-redux';
import * as Status from '../helpers/constants';

class StatusIndicator extends Component {

  render() {
    if (this.props.status === Status.STATUS_OK) {
      return <Icon name='check' color='green' fitted size='small' />;
    } else if (this.props.status === Status.STATUS_BUSY) {
      return <Loader size='mini' inline active />;
    } else if (this.props.status === Status.STATUS_ERRORS ||
               this.props.status === Status.STATUS_FATAL) {
                return <Icon name='warning sign' color='red' fitted size='small'/>;
               }
    return null;
  }
}

const StatusIndicatorConnected = connect(
  state => ({
    status: state.dialobComposer.editor && state.dialobComposer.editor.get('status'),
  }),
  {
  }
)(StatusIndicator);

export {
  StatusIndicator,
  StatusIndicatorConnected as default
}
