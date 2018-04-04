import React, {Component} from 'react';
import {Message, Button} from 'semantic-ui-react';
import Item, {connectItem} from './Item';
import Markdown from 'react-markdown';

class Note extends Item {
  render() {
    return (
      <Message>
        <Markdown source={this.props.item.getIn(['label', 'en'])} />
        <Button size='small' icon='edit' />
      </Message>
    );
  }
}

const NoteConnected = connectItem(Note);

export {
  Note,
  NoteConnected as default
};
