import React, {Component} from 'react';
import {Input, Segment} from 'semantic-ui-react';
import Item, {connectItem} from './Item';
import AddItemMenu from '../components/AddItemMenu';
import classnames from 'classnames';

class Group extends Item {

  render() {
    return (
      <React.Fragment>
        <Input onFocus={() => this.props.setActive()} fluid icon={this.props.icon} iconPosition='left' placeholder={this.props.placeholder} className='top attached' defaultValue={this.props.item.getIn(['label', 'en'])} />
        <Segment className={classnames({'composer-active': this.props.active})} attached='bottom'>
          {this.createChildren()}
          <AddItemMenu />
        </Segment>
      </React.Fragment>);
  }
}

const GroupConnected = connectItem(Group);

export {
  Group,
  GroupConnected as default
};
