import React from 'react';
import { Dialog, DialogTitle, DialogContent, Typography, Box, Alert } from '@mui/material';
import { ValueSetEntry } from '../dialob';
import { DialogActionButtons, DialogHelpButton } from './DialogComponents';
import CodeMirror from '@uiw/react-codemirror';
import { javascript } from '@codemirror/lang-javascript';
import { FormattedMessage, useIntl } from 'react-intl';


const ChoiceRuleEditDialog: React.FC<{
  open: boolean,
  valueSetEntry?: ValueSetEntry,
  onUpdate: (entry: ValueSetEntry, rule: string) => void,
  onClose: () => void
}> = ({ open, valueSetEntry, onUpdate, onClose }) => {
  const intl = useIntl();
  const [ruleCode, setRuleCode] = React.useState<string>(valueSetEntry?.when || '');
  const [errors, setErrors] = React.useState<string[]>([]);

  const handleClick = () => {
    if (valueSetEntry && ruleCode && ruleCode.length > 0) {
      onUpdate(valueSetEntry, ruleCode);
      onClose();
    }
  }

  React.useEffect(() => {
    if (valueSetEntry && valueSetEntry.when) {
      setRuleCode(valueSetEntry.when);
    } else {
      setRuleCode('');
    }
  }, [valueSetEntry]);

  React.useEffect(() => {
    // 1 second after every code change, check if rule is valid and set error message
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

  if (!valueSetEntry) {
    return null;
  }

  return (
    <Dialog open={open} maxWidth='md' fullWidth>
      <DialogTitle sx={{ display: 'flex', flexDirection: 'row', alignItems: 'center' }}>
        <Typography><FormattedMessage id='dialogs.rules.visibility.title' values={{ itemId: valueSetEntry.id }} /></Typography>
        <Box flexGrow={1} />
        <DialogHelpButton helpUrl='https://docs.dialob.io/#/400_dialob_expression_language:_DEL/100_basic_del' />
      </DialogTitle>
      <DialogContent>
        <Box sx={{ mb: 2 }}>
          <CodeMirror value={ruleCode} onChange={(value) => setRuleCode(value)} extensions={[javascript({ jsx: true })]} />
        </Box>
        {errors.length > 0 && <Alert severity='error' sx={{ mt: 2 }}>
          {errors.map((error, index) => <Typography key={index} color='error'>{error}</Typography>)}
        </Alert>}
      </DialogContent>
      <DialogActionButtons handleClose={onClose} handleClick={handleClick} />
    </Dialog>
  );
};

export default ChoiceRuleEditDialog;
