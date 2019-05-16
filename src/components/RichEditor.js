import React, {Component} from 'react';
import {debounce} from 'lodash';
import RichMarkdownEditor from 'rich-markdown-editor';

class RichEditor extends Component {

  handleChange = debounce(value => {
   if (this.props.onChange) {
     const v = value().replace(/\\{/gm, '{').replace(/\\}/gm, '}');
     this.props.onChange(v);
   }
  }, 250);

  render() {
    const {id, defaultValue, placeholder} = this.props;
    return (
      <RichMarkdownEditor
        readOnly={!this.props.active}
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
