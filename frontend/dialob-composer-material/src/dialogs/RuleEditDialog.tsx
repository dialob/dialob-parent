import React from 'react';
import { Dialog, DialogTitle, DialogContent, Typography, Box } from '@mui/material';
import { RuleEditDialogType, useEditor } from '../editor';
import { useComposer } from '../dialob';
import { DialogActionButtons, DialogHelpButton } from './DialogComponents';
import CodeMirror from '@uiw/react-codemirror';
import { javascript } from '@codemirror/lang-javascript';
import { FormattedMessage, useIntl } from 'react-intl';


const resolveRulePropName = (ruleType: RuleEditDialogType): string => {
  switch (ruleType) {
    case 'visibility': return 'activeWhen';
    case 'requirement': return 'required';
    default: return '';
  }
}

const RuleEditDialog: React.FC = () => {
  const { updateItem } = useComposer();
  const { editor, setRuleEditDialogType, setActiveItem } = useEditor();
  const intl = useIntl();
  const item = editor.activeItem;
  const open = item && editor.ruleEditDialogType !== undefined || false;
  const [ruleCode, setRuleCode] = React.useState<string | undefined>(undefined);
  const [errors, setErrors] = React.useState<string[]>([]);

  const handleClose = () => {
    setRuleEditDialogType(undefined);
    setActiveItem(undefined);
  }

  const handleClick = () => {
    if (editor.ruleEditDialogType && item && ruleCode && ruleCode.length > 0) {
      updateItem(item.id, resolveRulePropName(editor.ruleEditDialogType), ruleCode);
      handleClose();
    }
  }

  React.useEffect(() => {
    if (editor.ruleEditDialogType && item) {
      setRuleCode(item[resolveRulePropName(editor.ruleEditDialogType)]);
    }
  }, [editor.ruleEditDialogType, item]);

  React.useEffect(() => {
    // 3 seconds after every code change, check if rule is valid and set error message
    if (ruleCode && ruleCode.length > 0) {
      // TODO add rule error check
      // random boolean for now
      const invalid = Math.random() < 0.5;
      if (invalid) {
        const id = setTimeout(() => {
          setErrors([intl.formatMessage({ id: 'dialogs.rules.error' }, { rule: ruleCode })]);
        }, 3000);
        return () => clearTimeout(id);
      } else {
        setErrors([]);
      }
    } else {
      setErrors([]);
    }
  }, [ruleCode]);

  if (!item) {
    return null;
  }

  return (
    <Dialog open={open} maxWidth='md' fullWidth>
      <DialogTitle sx={{ display: 'flex', flexDirection: 'row', alignItems: 'center' }}>
        <Typography><FormattedMessage id={`dialogs.rules.${editor.ruleEditDialogType}.title`} values={{ itemId: item.id }} /></Typography>
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
      <DialogActionButtons handleClose={handleClose} handleClick={handleClick} />
    </Dialog>
  );
};

export default RuleEditDialog;
