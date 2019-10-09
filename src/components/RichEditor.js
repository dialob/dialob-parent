import React, {Component} from 'react';
import debounce from 'lodash.debounce';
import RichMarkdownEditor from 'rich-markdown-editor';
import editorTheme from 'rich-markdown-editor/lib/theme';

class RichEditor extends Component {

  handleChange = debounce(value => {
   if (this.props.onChange) {
     const v = value().replace(/\\{/gm, '{').replace(/\\}/gm, '}');
     this.props.onChange(v);
   }
  }, 250);

  render() {
    const {id, defaultValue, placeholder, zIndex} = this.props;
    const customTheme = editorTheme;
    if (zIndex) { customTheme.zIndex = zIndex; }
    return (
      <RichMarkdownEditor
        readOnly={!this.props.active}
        id={id}
        defaultValue={defaultValue || ''}
        onShowToast={message => window.alert(message)}
        onChange={this.handleChange}
        placeholder={placeholder}
        theme={customTheme}
      />
    );
  }
}

export {
  RichEditor as default
};
