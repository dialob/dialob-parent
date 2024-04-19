import React from 'react';
import { FormattedMessage, useIntl } from 'react-intl';
import { Box, Button, Typography, Alert, Tabs, Tab, Tooltip } from '@mui/material';
import { Add, Delete, Warning } from '@mui/icons-material';
import CodeMirror from '@uiw/react-codemirror';
import { javascript } from '@codemirror/lang-javascript';
import { useEditor } from '../../editor';
import { ValidationRule, useComposer } from '../../dialob';
import { LocalizedStringEditor } from './LocalizedStringEditor';


export interface IndexedRule {
  index: number;
  validationRule: ValidationRule;
}

const ValidationRuleEditor: React.FC = () => {
  const { createValidation, deleteValidation, setValidationExpression } = useComposer();
  const { editor, setActiveItem } = useEditor();
  const intl = useIntl();
  const item = editor.activeItem;
  const [rules, setRules] = React.useState<IndexedRule[]>([]);
  const [errors, setErrors] = React.useState<string[]>([]);
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
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [item]);


  React.useEffect(() => {
    // 3 seconds after every code change, check if rule is valid and set error message
    if (activeRule && activeRule.validationRule.rule && activeRule?.validationRule.rule.length > 0) {
      // TODO add rule error check
      // random boolean for now
      const invalid = Math.random() < 0.5;
      if (invalid) {
        const id = setTimeout(() => {
          setErrors([intl.formatMessage({ id: 'errors.invalid.rule' }, { rule: activeRule.validationRule.rule })]);
        }, 1000);
        return () => clearTimeout(id);
      } else {
        setErrors([]);
      }
    } else {
      setErrors([]);
    }
  }, [activeRule, intl]);

  React.useEffect(() => {
    if (item && activeRule && activeRule.validationRule.rule && item.validations?.[activeRule.index] &&
      activeRule.validationRule.rule !== item.validations?.[activeRule.index].rule) {
      const expression = activeRule.validationRule.rule;
      const id = setTimeout(() => {
        const validations = [...item.validations || []];
        const newValidations = validations.map((rule, index) => index === activeRule.index ? { ...rule, rule: expression } : rule);
        setActiveItem({ ...item, validations: newValidations });
        setValidationExpression(item.id, activeRule.index, expression);
      }, 300);
      return () => clearTimeout(id);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [activeRule?.validationRule.rule]);

  if (!item) {
    return null;
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
        <CodeMirror value={activeRule.validationRule.rule} onChange={handleUpdate} extensions={[javascript({ jsx: true })]} />
      </Box>}
      <Box sx={{ mt: 2 }}>
        <LocalizedStringEditor type='validations' rule={activeRule} setRule={setActiveRule} />
      </Box>
      {errors.length > 0 && <Alert severity='error' sx={{ mt: 2 }} icon={<Warning />}>
        {errors.map((error, index) => <Typography key={index} color='error'>{error}</Typography>)}
      </Alert>}
    </>
  );
}

export { ValidationRuleEditor };
