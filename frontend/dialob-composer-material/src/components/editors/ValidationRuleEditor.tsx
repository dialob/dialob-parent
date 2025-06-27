import React from 'react';
import { FormattedMessage } from 'react-intl';
import { Box, Button, Typography, Alert, Tabs, Tab, Tooltip } from '@mui/material';
import { Add, Delete, Warning } from '@mui/icons-material';
import { useEditor } from '../../editor';
import { LocalizedStringEditor } from './LocalizedStringEditor';
import { ErrorMessage } from '../ErrorComponents';
import CodeMirror from '../code/CodeMirror';
import { getErrorSeverity } from '../../utils/ErrorUtils';
import { IndexedRule } from './types';
import { useSave } from '../../dialogs/contexts/saving/useSave';

const ValidationRuleEditor: React.FC = () => {
  const { editor } = useEditor();
  const { savingState, createValidation, deleteValidation, setValidationExpression } = useSave();
  const item = savingState.item;
  const itemErrors = editor.errors?.filter(e => e.itemId === item?.id && e.type === 'VALIDATION');
  const [rules, setRules] = React.useState<IndexedRule[]>([]);
  const [activeRule, setActiveRule] = React.useState<IndexedRule | undefined>(undefined);

  React.useEffect(() => {
    const validationRules: IndexedRule[] = [];
    item?.validations?.forEach((rule, index) => {
      validationRules.push({ index, validationRule: rule })
    });
    setRules(validationRules);
    if (activeRule === undefined || !validationRules.some((r) => r.index === activeRule.index)) {
      setActiveRule(validationRules.length > 0 ? validationRules[0] : undefined);
    }
  }, [item]);

  if (!item) {
    return null;
  }

  const handleDelete = (e: React.MouseEvent<HTMLDivElement, MouseEvent>, rule: IndexedRule) => {
    e.stopPropagation();
    const newRules = [...rules];
    newRules.splice(rule.index, 1);
    setActiveRule(newRules.length > 0 ? newRules[0] : undefined);
    deleteValidation(item.id, rule.index);
  }

  const handleAdd = () => {
    const newRules = [...rules];
    const newRule: IndexedRule = { index: newRules.length, validationRule: {} };
    newRules.push(newRule);
    setActiveRule(newRules[newRules.length - 1]);
    createValidation(item.id);
  }

  const handleUpdate = (value: string) => {
    if (activeRule) {
      setActiveRule({ ...activeRule, validationRule: { ...activeRule.validationRule, rule: value } });
      if (item && activeRule && item.validations?.[activeRule.index] &&
        value !== item.validations?.[activeRule.index].rule && value !== '') {
        setValidationExpression(item.id, activeRule.index, value);
      }
    }
  }

  return (
    <>
      <Box sx={{ display: 'flex', alignItems: 'center' }}>
        <Tabs value={activeRule?.index} onChange={(e, value) => setActiveRule(rules[value])} sx={{ borderBottom: 1, borderColor: 'divider' }}>
          {rules.map((rule) => <Tab key={rule.index} label={
            <Box sx={{ display: 'flex', alignItems: 'center' }}>
              <FormattedMessage id='dialogs.options.validations.rule.tab' values={{ index: rule.index + 1 }} />
              <Tooltip title={<FormattedMessage id='dialogs.options.validations.rule.delete' />}>
                <Box
                  sx={{ ml: 1, display: 'flex', alignItems: 'center', color: 'error.main', ':hover': { color: 'inherit' } }}
                  onClick={(e) => handleDelete(e, rule)}
                >
                  <Delete />
                </Box>
              </Tooltip>
            </Box>
          } onClick={() => setActiveRule(rule)} value={rule.index} />)}
        </Tabs>
        <Box flexGrow={1} />
        <Button endIcon={<Add />} sx={{ textTransform: 'none' }} onClick={handleAdd}>
          <FormattedMessage id='dialogs.options.validations.rule.add' />
        </Button>
      </Box>
      {rules.length > 0 && activeRule !== undefined && <Box sx={{ mt: 2 }}>
        <CodeMirror value={activeRule.validationRule.rule ?? ''} onChange={handleUpdate} errors={itemErrors} />
      </Box>}
      {itemErrors?.map((error, index) => <Alert key={index} severity={getErrorSeverity(error)} sx={{ mt: 2 }} icon={<Warning />}>
        <Typography color={error.level.toLowerCase()}><ErrorMessage error={error} /></Typography>
      </Alert>)}
      <Box sx={{ mt: 2 }}>
        <LocalizedStringEditor type='validations' rule={activeRule} setRule={setActiveRule} />
      </Box>
    </>
  );
}

export { ValidationRuleEditor };
