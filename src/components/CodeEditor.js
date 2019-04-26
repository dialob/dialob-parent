import React, {Component} from 'react';
import CodeMirrorIntegration from 'codemirror';
import {debounce} from 'lodash';

import 'codemirror/lib/codemirror.css';
import 'codemirror/addon/lint/lint.css';
import 'codemirror/addon/hint/show-hint.css';
import 'codemirror/addon/lint/lint';
import 'codemirror/addon/hint/show-hint';

class CodeEditor extends Component {

  handleChange = debounce(value => {
    if (this.props.onChange) {
      this.props.onChange(value);
    }
  }, 250);

  componentDidMount() {
    const element = this.element;
    if (element) {

        const editor = CodeMirrorIntegration.fromTextArea(element, {
          mode: 'del',
          lineNumbers: false,
          tabSize: 2,
        });

        editor.on('change', e => this.handleChange(e.getValue()));
    }
  }

  render() {
    return <textarea rows={2} id={this.props.id} ref={element => this.element = element} defaultValue={this.props.value} />;
  }

}

export {
  CodeEditor as default
};
