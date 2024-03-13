import React from 'react';
import { Button, Typography, Box, Alert } from '@mui/material';
import CodeMirror from '@uiw/react-codemirror';
import { javascript } from '@codemirror/lang-javascript';
import { FormattedMessage, useIntl } from 'react-intl';
import { Help, Warning } from '@mui/icons-material';
import { useComposer } from '../../dialob';
import { useEditor } from '../../editor';

type RuleType = 'visibility' | 'requirement';

const resolveRulePropName = (ruleType: RuleType): string => {
  switch (ruleType) {
    case 'visibility': return 'activeWhen';
    case 'requirement': return 'required';
    default: return '';
  }
}

const RuleEditor: React.FC<{ type: RuleType }> = ({ type }) => {
  const { updateItem } = useComposer();
  const { editor } = useEditor();
  const intl = useIntl();
  const item = editor.activeItem;
  const [ruleCode, setRuleCode] = React.useState<string | undefined>(undefined);
  const [errors, setErrors] = React.useState<string[]>([]);

  React.useEffect(() => {
    if (item) {
      setRuleCode(item[resolveRulePropName(type)]);
    }
  }, [item]);

  React.useEffect(() => {
    // 3 seconds after every code change, check if rule is valid and set error message
    if (ruleCode && ruleCode.length > 0) {
      // TODO add rule error check
      // random boolean for now
      const invalid = Math.random() < 0.5;
      if (invalid) {
        const id = setTimeout(() => {
          setErrors([intl.formatMessage({ id: 'dialogs.rules.error' }, { rule: ruleCode })]);
        }, 1000);
        return () => clearTimeout(id);
      } else {
        setErrors([]);
      }
    } else {
      setErrors([]);
    }
  }, [ruleCode]);

  React.useEffect(() => {
    if (item && ruleCode) {
      const id = setTimeout(() => {
        updateItem(item.id, resolveRulePropName(type), ruleCode);
      }, 300);
      return () => clearTimeout(id);
    }
  }, [ruleCode]);

  if (!item) {
    return null;
  }

  return (
    <Box sx={{ mb: 2 }}>
      <Typography color='text.hint'><FormattedMessage id={`dialogs.options.rules.${type}`} /></Typography>
      <Box>
        <CodeMirror value={ruleCode} onChange={(value) => setRuleCode(value)} extensions={[javascript({ jsx: true })]} />
      </Box>
      {errors.length > 0 && <Alert severity='error' sx={{ mt: 2 }} icon={<Warning />}>
        {errors.map((error, index) => <Typography key={index} color='error'>{error}</Typography>)}
      </Alert>}
    </Box>
  );
};

export default RuleEditor;
