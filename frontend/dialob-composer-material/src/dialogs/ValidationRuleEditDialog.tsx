import React from 'react';
import CodeMirror from '@uiw/react-codemirror';
import { javascript } from '@codemirror/lang-javascript';
import { Dialog, DialogTitle, DialogContent, Typography, Box, TextField, Button, IconButton } from '@mui/material';
import { Add, Close } from '@mui/icons-material';
import { useEditor } from '../editor';
import { DialobItem, ValidationRule, useComposer } from '../dialob';
import { DialogActionButtons, DialogHelpButton, DialogLanguageMenu } from './DialogComponents';

interface IndexedRule {
  index: number;
  validationRule: ValidationRule;
}

const indexValidationRules = (existingRules: ValidationRule[]) => {
  if (existingRules.length > 0) {
    const newRules: IndexedRule[] = [];
    existingRules.forEach((validation, index) => {
      newRules.push({ index, validationRule: { ...validation } });
    });
    return newRules;
  } else {
    return [];
  }
}

const ValidationRuleEditDialog: React.FC = () => {
  const { form, createValidation, deleteValidation, setValidationExpression, setValidationMessage } = useComposer();
  const item: DialobItem = Object.values(form.data).find((item) => item.id === 'workRelatedRiskAnalysis')!;
  const existingRules = item.validations || [];
  const { editor, setValidationRuleEditDialogOpen } = useEditor();
  const open = editor.validationRuleEditDialogOpen || false;
  const [rules, setRules] = React.useState<IndexedRule[]>(() => indexValidationRules(existingRules));
  const [errors, setErrors] = React.useState<string[]>([]);
  const [activeLanguage, setActiveLanguage] = React.useState(editor.activeFormLanguage);

  const handleClose = () => {
    setValidationRuleEditDialogOpen(false);
  }

  const handleClick = () => {
    rules.forEach((r, index) => {
      if (r.validationRule.rule && existingRules.length > 0 && existingRules[index] && r.validationRule.rule !== existingRules[index].rule) {
        setValidationExpression(item.id, r.index, r.validationRule.rule);
      }
      if (r.validationRule.message && existingRules.length > 0 && existingRules[index] && existingRules[index].message &&
        existingRules[index].message![activeLanguage] && r.validationRule.message[activeLanguage] !== existingRules[index].message![activeLanguage]) {
        setValidationMessage(item.id, r.index, activeLanguage, r.validationRule.message[activeLanguage]);
      }
      if (existingRules.length === 0 || r.index >= existingRules.length) {
        createValidation(item.id, r.validationRule);
      }
      if (index !== r.index) {
        deleteValidation(item.id, r.index);
      }
    });
    handleClose();
  }

  React.useEffect(() => {
    if (rules.length > 0) {
      const ruleErrors: string[] = [];
      rules.forEach((rule) => {
        // TODO add rule error check
        // random boolean for now
        const invalid = Math.random() < 0.5;
        if (invalid) {
          ruleErrors.push('Invalid rule: ' + rule.validationRule.rule);
        }
      });
      if (ruleErrors.length > 0) {
        const id = setTimeout(() => {
          setErrors(ruleErrors);
        }, 3000);
        return () => clearTimeout(id);
      }
    } else {
      setErrors([]);
    }
  }, [rules]);

  return (
    <Dialog open={open} maxWidth='md' fullWidth>
      <DialogTitle sx={{ display: 'flex', flexDirection: 'row', alignItems: 'center' }}>
        <Typography>{item.id}: validation</Typography>
        <Box flexGrow={1} />
        <Box sx={{ display: 'flex', width: 0.35, justifyContent: 'space-between' }}>
          <DialogLanguageMenu activeLanguage={activeLanguage} setActiveLanguage={setActiveLanguage} />
          <Button color='inherit' endIcon={<Add />} onClick={() => {
            const newRules = [...rules];
            newRules.push({ index: newRules.length, validationRule: { rule: '', message: { [activeLanguage]: '' } } });
            setRules(newRules);
          }}>Add</Button>
          <DialogHelpButton helpUrl='https://docs.dialob.io/#/400_dialob_expression_language:_DEL/100_basic_del' />
        </Box>
      </DialogTitle>
      <DialogContent>
        {rules.map((r) => {
          const { index, validationRule } = r;
          return (
            <Box key={index} sx={{ display: 'flex', flexDirection: 'column', border: 1, borderRadius: 1, p: 2, mb: 2 }}>
              <Box sx={{ display: 'flex', flexDirection: 'row', alignItems: 'center' }}>
                <Typography variant='subtitle1'>Rule {index + 1}</Typography>
                <Box flexGrow={1} />
                <IconButton onClick={() => {
                  const newRules = [...rules];
                  newRules.splice(index, 1);
                  setRules(newRules);
                }}><Close /></IconButton>
              </Box>
              <Box sx={{ my: 2 }}>
                <Typography variant='caption'>Expression</Typography>
                <CodeMirror value={validationRule.rule} onChange={(value) => {
                  const newRules = [...rules];
                  newRules[index].validationRule.rule = value;
                  setRules(newRules);
                }} extensions={[javascript({ jsx: true })]} />
              </Box>
              <Typography variant='caption'>Message</Typography>
              <TextField fullWidth value={validationRule.message ? validationRule.message[activeLanguage] : ''} onChange={(e) => {
                const newRules = [...rules];
                newRules[index].validationRule.message = { ...newRules[index].validationRule.message, [activeLanguage]: e.target.value };
                setRules(newRules);
              }} />
            </Box>
          )
        })}
        {errors.length > 0 && <Box sx={{ border: 1, borderRadius: 0.5, borderColor: 'error.main', p: 2 }}>
          {errors.map((error, index) => <Typography key={index} color='error'>{error}</Typography>)}
        </Box>}
      </DialogContent>
      <DialogActionButtons handleClose={handleClose} handleClick={handleClick} />
    </Dialog>
  );
};

export default ValidationRuleEditDialog;
