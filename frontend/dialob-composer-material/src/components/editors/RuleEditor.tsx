import React from 'react';
import { Typography, Box, Alert } from '@mui/material';
import { FormattedMessage } from 'react-intl';
import { Warning } from '@mui/icons-material';
import { useComposer } from '../../dialob';
import { useEditor } from '../../editor';
import { ErrorMessage } from '../ErrorComponents';
import CodeMirror from '../code/CodeMirror';
import { getErrorSeverity } from '../../utils/ErrorUtils';
import { DEFAULT_ITEMTYPE_CONFIG } from '../../defaults';

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
  const { editor, setActiveItem } = useEditor();
  const item = editor.activeItem;
  const itemErrors = editor.errors.filter(e => e.itemId === item?.id && e.type === type.toUpperCase());
  const [ruleCode, setRuleCode] = React.useState<string | undefined>(undefined);

  React.useEffect(() => {
    if (item) {
      setRuleCode(item[resolveRulePropName(type)]);
    }
  }, [item, type]);

  React.useEffect(() => {
    if (item && ruleCode && ruleCode !== item[resolveRulePropName(type)]) {
      const id = setTimeout(() => {
        updateItem(item.id, resolveRulePropName(type), ruleCode);
        setActiveItem({ ...item, [resolveRulePropName(type)]: ruleCode });
      }, 300);
      return () => clearTimeout(id);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [ruleCode]);

  if (!item) {
    return null;
  }

  if (!DEFAULT_ITEMTYPE_CONFIG.categories.find(c => c.type === 'input')?.items.find(i => i.config.type === item.type) && type === 'requirement') {
    return null;
  }

  return (
    <Box sx={{ mb: 2 }}>
      <Typography color='text.hint'><FormattedMessage id={`dialogs.options.rules.${type}`} /></Typography>
      <Box>
        <CodeMirror value={ruleCode} onChange={(value) => setRuleCode(value)} errors={itemErrors} />
      </Box>
      {itemErrors.length > 0 && itemErrors.map((error, index) => <Alert severity={getErrorSeverity(error)} sx={{ mt: 2 }} icon={<Warning />}>
        <Typography key={index} color={error.level.toLowerCase()}><ErrorMessage error={error} /></Typography>
      </Alert>)}
    </Box>
  );
};

export { RuleEditor };
