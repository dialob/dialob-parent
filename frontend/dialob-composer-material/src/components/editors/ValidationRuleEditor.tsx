import React from 'react';
import { FormattedMessage } from 'react-intl';
import { Box, Button, Typography, Alert, Tabs, Tab, Tooltip, Divider } from '@mui/material';
import { Add, Delete, Warning } from '@mui/icons-material';
import { useEditor } from '../../editor';
import { useComposer } from '../../dialob';
import { LocalizedStringEditor } from './LocalizedStringEditor';
import { ErrorMessage } from '../ErrorComponents';
import CodeMirror from '../code/CodeMirror';
import { getErrorSeverity } from '../../utils/ErrorUtils';
import { IndexedRule } from './types';
import { ValidationRule } from '../../types';

const ValidationRuleEditor: React.FC = () => {
  const { createValidation, deleteValidation, setValidationExpression } = useComposer();
  const { editor, setActiveItem } = useEditor();
  const item = editor.activeItem;
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

  const handleSaveRule = () => {
    if (item && activeRule && activeRule.validationRule.rule && item.validations?.[activeRule.index] &&
      activeRule.validationRule.rule !== item.validations?.[activeRule.index].rule) {
      const expression = activeRule.validationRule.rule;
      const validations = [...item.validations || []];
      const newValidations = validations.map((rule, index) => index === activeRule.index ? { ...rule, rule: expression } : rule);
      setActiveItem({ ...item, validations: newValidations });
      setValidationExpression(item.id, activeRule.index, expression);
    }
  }

  const handleDelete = (e: React.MouseEvent<HTMLDivElement, MouseEvent>, rule: IndexedRule) => {
    e.stopPropagation();
    const newRules = [...rules];
    newRules.splice(rule.index, 1);
    setRules(newRules);
    setActiveRule(newRules.length > 0 ? newRules[0] : undefined);
    const validations: ValidationRule[] = [...item.validations || []];
    validations.splice(rule.index, 1);
    setActiveItem({ ...item, validations: validations });
    deleteValidation(item.id, rule.index);
  }

  const handleAdd = () => {
    const newRules = [...rules];
    const newRule: IndexedRule = { index: newRules.length, validationRule: {} };
    newRules.push(newRule);
    setRules(newRules);
    const validations: ValidationRule[] = [...item.validations || [], newRule.validationRule];
    setActiveItem({ ...item, validations: validations });
    setActiveRule(newRules[newRules.length - 1]);
    createValidation(item.id);
  }

  const handleUpdate = (value: string) => {
    if (activeRule) {
      const newRules = [...rules];
      const newRule = { ...activeRule, validationRule: { ...activeRule.validationRule, rule: value } };
      newRules[activeRule.index] = newRule;
      setRules(newRules);
      setActiveRule({ ...activeRule, validationRule: { ...activeRule.validationRule, rule: value } });
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
        {
          activeRule.validationRule.rule !== item.validations?.[activeRule.index].rule && 
          <Box sx={{ display: 'flex', pt: 1, justifyContent: 'flex-end' }}>
            <Button onClick={handleSaveRule}><FormattedMessage id='buttons.rule.save' /></Button>
          </Box>
        }
      </Box>}
      <Box sx={{ mt: 2 }}>
        <LocalizedStringEditor type='validations' rule={activeRule} setRule={setActiveRule} />
      </Box>
      {itemErrors?.map((error, index) => <Alert key={index} severity={getErrorSeverity(error)} sx={{ mt: 2 }} icon={<Warning />}>
        <Typography color={error.level.toLowerCase()}><ErrorMessage error={error} /></Typography>
      </Alert>)}
    </>
  );
}

export { ValidationRuleEditor };
