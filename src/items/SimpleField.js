import React, {Component} from 'react';
import {Input} from 'semantic-ui-react';
import Item, {connectItem} from './Item';

class SimpleField extends Item {
  render() {
    return (
      <p><Input onFocus={() => this.props.setActive()} fluid icon={this.props.icon} iconPosition='left' placeholder={this.props.placeholder} defaultValue={this.props.item.getIn(['label', 'en'])}/></p>
    );
  }
}

const SimpleFieldConnected = connectItem(SimpleField);

export {
  SimpleField,
  SimpleFieldConnected as default
};
