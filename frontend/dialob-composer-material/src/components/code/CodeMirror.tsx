import React from 'react';
import ReactCodeMirror, { Extension } from '@uiw/react-codemirror';
import { EditorError } from '../../editor';
import { IntlShape, useIntl } from 'react-intl';
import { Diagnostic, linter } from '@codemirror/lint';
import { LanguageSupport, StreamLanguage, StringStream } from '@codemirror/language';
import { IGNORED_ITEM_TYPES, RESERVED_WORDS } from './language';
import { Completion, completeFromList } from '@codemirror/autocomplete';
import { ComposerState, useComposer } from '../../dialob';


type LintSeverity = "hint" | "info" | "warning" | "error";

interface CodeMirrorProps {
  value: string | undefined;
  onChange: (value: string) => void;
  errors?: EditorError[];
}

const getLinter = (intl: IntlShape, errors?: EditorError[]): Extension => {
  if (!errors) {
    return linter(() => []);
  }

  return linter(() => {
    const diagnostics: Diagnostic[] = []
    errors.forEach(error => {
      if (error.startIndex !== undefined && error.endIndex !== undefined) {
        const label = `errors.message.${error.message}`;
        const diagnostic = {
          from: error.startIndex,
          to: error.endIndex + 1,
          severity: error.level.toLowerCase() as LintSeverity,
          message: intl.formatMessage({ id: label })
        };
        diagnostics.push(diagnostic);
      }
    })
    return diagnostics
  })
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
const matcher = (stream: StringStream, _state: unknown): string | null => {
  if (stream.match(/'(?:[^\\]|\\.)*?(?:'|$)/)) {
    return 'string';
  }
  if (stream.match(/"(?:[^\\]|\\.)*?(?:"|$)/)) {
    return 'string';
  }
  if (stream.match(/0x[a-f\d]+|[-+]?(?:\.\d+|\d+\.?\d*)(?:e[-+]?\d+)?/i)) {
    return 'number';
  }
  if (stream.match(new RegExp(`(?:${RESERVED_WORDS.map(w => w.label).join('|')})\\b`))) {
    return 'keyword';
  }
  if (stream.match(/[-+/*=<>!]+/)) {
    return 'operator';
  }
  if (stream.match(/[a-z$][\w$]*/)) {
    return 'variable';
  }
  stream.next();
  return null;
}

const dialobExpressionLanguage = StreamLanguage.define({
  name: 'del',
  token: (stream) => matcher(stream, null),
});

const getAutocompletions = (form: ComposerState) => {
  const itemIds = Object.values(form.data).filter(item => !IGNORED_ITEM_TYPES.includes(item.type)).map(item => item.id);
  const variableIds = form.variables ? form.variables.map(variable => variable.name) : [];
  const ids = [...itemIds, ...variableIds];
  const idCompletions: Completion[] = ids.map(id => ({ label: id, type: 'variable' }));
  return dialobExpressionLanguage.data.of({
    autocomplete: completeFromList([...RESERVED_WORDS, ...idCompletions])
  })
}

const CodeMirror: React.FC<CodeMirrorProps> = (props) => {
  const { value, onChange, errors } = props;
  const { form } = useComposer();
  const intl = useIntl();
  const linter = getLinter(intl, errors)
  const autocomplete = getAutocompletions(form);
  const delLanguageSupport = new LanguageSupport(dialobExpressionLanguage, [autocomplete]);
  return <ReactCodeMirror value={value} onChange={onChange} extensions={[linter, delLanguageSupport]} />
}

export default CodeMirror;
