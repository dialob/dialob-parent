import React from 'react';
import { Typography, Box, Alert, Button } from '@mui/material';
import { FormattedMessage } from 'react-intl';
import { Warning } from '@mui/icons-material';
import { useComposer } from '../../dialob';
import { useEditor } from '../../editor';
import { ErrorMessage } from '../ErrorComponents';
import CodeMirror from '../code/CodeMirror';
import { getErrorSeverity } from '../../utils/ErrorUtils';
import { DEFAULT_ITEMTYPE_CONFIG } from '../../defaults';
import { useBackend } from '../../backend/useBackend';

type RuleType = 'visibility' | 'requirement' | 'canaddrow' | 'canremoverow';

const resolveRulePropName = (ruleType: RuleType): string => {
  switch (ruleType) {
    case 'visibility': return 'activeWhen';
    case 'requirement': return 'required';
    case 'canaddrow': return 'canAddRowWhen';
    case 'canremoverow': return 'canRemoveRowWhen';
    default: return '';
  }
}

const RuleEditor: React.FC<{ type: RuleType }> = ({ type }) => {
  const { updateItem } = useComposer();
  const { editor, setActiveItem } = useEditor();
  const { config } = useBackend();
  const resolvedConfig = config.itemTypes ?? DEFAULT_ITEMTYPE_CONFIG;
  const item = editor.activeItem;
  const itemErrors = editor.errors?.filter(e => e.itemId === item?.id && e.type === type.toUpperCase());
  const [ruleCode, setRuleCode] = React.useState<string>('');

  React.useEffect(() => {
    if (item) {
      setRuleCode(item[resolveRulePropName(type)] || '');
    }
  }, [item, type]);

  const handleSaveRule = () => {
    if (item && ruleCode !== item[resolveRulePropName(type)]) {
      if (ruleCode === '' && item[resolveRulePropName(type)] === undefined) {
        return;
      }
      updateItem(item.id, resolveRulePropName(type), ruleCode);
      setActiveItem({ ...item, [resolveRulePropName(type)]: ruleCode });
    }
  }

  if (!item) {
    return null;
  }

  if (!resolvedConfig.categories.find(c => c.type === 'input')?.items.find(i => i.config.type === item.type) && type === 'requirement') {
    return null;
  }

  if (item.type !== 'rowgroup' && (type === 'canaddrow' || type === 'canremoverow')) {
    return null;
  }

  return (
    <Box sx={{ mb: 2 }}>
      <Typography color='text.hint'><FormattedMessage id={`dialogs.options.rules.${type}`} /></Typography>
      <Box>
        <CodeMirror value={ruleCode} onChange={(value) => setRuleCode(value)} errors={itemErrors} />
        {
          item && ruleCode !== (item[resolveRulePropName(type)] ?? '') && 
          <Box sx={{ display: 'flex', pt: 1, justifyContent: 'flex-end' }}>
            <Button onClick={handleSaveRule}><FormattedMessage id='buttons.rule.save' /></Button>
          </Box>
        }
      </Box>
      {itemErrors?.map((error, index) => <Alert key={index} severity={getErrorSeverity(error)} sx={{ mt: 2 }} icon={<Warning />}>
        <Typography color={error.level.toLowerCase()}><ErrorMessage error={error} /></Typography>
      </Alert>)}
    </Box>
  );
};

export { RuleEditor };
