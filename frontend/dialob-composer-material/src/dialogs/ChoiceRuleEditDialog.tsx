import React from 'react';
import { Dialog, DialogTitle, DialogContent, Typography, Box } from '@mui/material';
import { ValueSetEntry } from '../dialob';
import { DialogActionButtons, DialogHelpButton } from './DialogComponents';
import CodeMirror from '@uiw/react-codemirror';
import { javascript } from '@codemirror/lang-javascript';


const ChoiceRuleEditDialog: React.FC<{
  open: boolean,
  valueSetEntry?: ValueSetEntry,
  onUpdate: (entry: ValueSetEntry, rule: string) => void,
  onClose: () => void
}> = ({ open, valueSetEntry, onUpdate, onClose }) => {
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
    // 3 seconds after every code change, check if rule is valid and set error message
    if (ruleCode && ruleCode.length > 0) {
      // TODO add rule error check
      // random boolean for now
      const invalid = Math.random() < 0.5;
      if (invalid) {
        const id = setTimeout(() => {
          setErrors(['Invalid rule: ' + ruleCode]);
        }, 3000);
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
        <Typography>{valueSetEntry.id}: visibility</Typography>
        <Box flexGrow={1} />
        <DialogHelpButton helpUrl='https://docs.dialob.io/#/400_dialob_expression_language:_DEL/100_basic_del' />
      </DialogTitle>
      <DialogContent>
        <Box sx={{ mb: 2 }}>
          <CodeMirror value={ruleCode} onChange={(value) => setRuleCode(value)} extensions={[javascript({ jsx: true })]} />
        </Box>
        {errors.length > 0 && <Box sx={{ border: 1, borderRadius: 0.5, borderColor: 'error.main', p: 2 }}>
          {errors.map((error, index) => <Typography key={index} color='error'>{error}</Typography>)}
        </Box>}
      </DialogContent>
      <DialogActionButtons handleClose={onClose} handleClick={handleClick} />
    </Dialog>
  );
};

export default ChoiceRuleEditDialog;
