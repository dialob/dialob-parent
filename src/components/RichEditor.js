import React, {Component} from 'react';
import {debounce} from 'lodash';
import RichMarkdownEditor from 'rich-markdown-editor';

class RichEditor extends Component {

  handleChange = debounce(value => {
   if (this.props.onChange) {
     this.props.onChange(value());
   }
  }, 250);

  render() {
    const {id, defaultValue, placeholder} = this.props;
    return (
      <RichMarkdownEditor
        id={id}
        defaultValue={defaultValue || ''}
        onShowToast={message => window.alert(message)}
        onChange={this.handleChange}
        placeholder={placeholder}
      />
    );
  }
}

export {
  RichEditor as default
};
