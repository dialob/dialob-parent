import React, {Component} from 'react';
import CodeMirrorIntegration from 'codemirror';
import {debounce} from 'lodash';
import delHinter from '../del/hinting';
import {connect} from 'react-redux';
import classnames from 'classnames';

import 'codemirror/lib/codemirror.css';
import 'codemirror/addon/lint/lint.css';
import 'codemirror/addon/hint/show-hint.css';
import 'codemirror/addon/lint/lint';
import 'codemirror/addon/hint/show-hint';
import 'codemirror/addon/display/placeholder';

class CodeEditor extends Component {

  constructor(props) {
    super(props);
    this.editor = null;
  }

  handleChange = debounce(value => {
    if (this.props.onChange) {
      this.props.onChange(value);
    }
  }, 250);

  getHints(editor, options) {
    const cursor = editor.getCursor();
    const line = editor.getLine(cursor.line);
    const token = editor.getTokenAt(cursor);
    const pos = {
      id: cursor.line,
      content: token.string,
      at: cursor.ch,
      isEmpty: token.string.length === 0 || !token.string.trim()
    };

    return {
      list: options.getList(pos),
      from: CodeMirrorIntegration.Pos(cursor.line, pos.isEmpty ? token.end : token.start ),
      to: CodeMirrorIntegration.Pos(cursor.line, token.end)
    };
  }

  componentDidMount() {
    const element = this.element;
    if (element) {

        const hintOptions = {
          completeSingle: false,
          hint: this.getHints,
          getList: this.props.hinter
        }

        const editor = CodeMirrorIntegration.fromTextArea(element, {
          mode: 'del',
          lineNumbers: false,
          tabSize: 2,
          extraKeys: {
            'Ctrl-Space': 'autocomplete'
          },
          hintOptions
        });

        editor.on('change', e => this.handleChange(e.getValue()));

        this.editor = editor;
    }
  }

  focusEditor() {
    if (this.editor) {
      this.editor.focus();
    }
  }

  render() {
    const {id, value, placeholder, icon} = this.props;
    return (
      <div className={classnames('ui fluid transparent', {'icon': !!icon}, 'input')} onClick={() => this.focusEditor()} >
        <textarea rows={2} id={id} ref={element => this.element = element} defaultValue={value} placeholder={placeholder} />
        {icon &&
          <i aria-hidden='true' className={classnames(icon, 'icon')} />
        }
      </div>
    );
  }
}

const CodeEditorConnected = connect(
  state => ({
    hinter: context => delHinter(context, state.dialobComposer.form)
  }),
  {
  }
)(CodeEditor);

export {
  CodeEditorConnected as default,
  CodeEditor
};
