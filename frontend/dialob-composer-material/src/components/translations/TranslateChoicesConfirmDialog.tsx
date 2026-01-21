import React from 'react';
import { FormattedMessage } from 'react-intl';
import { Button, Dialog, DialogActions, DialogContent, DialogTitle, Typography } from '@mui/material';
import { useEditor } from '../../editor';
import { getLanguageName } from '../../utils/TranslationUtils';

interface TranslateChoicesConfirmDialogProps {
  open: boolean;
  onConfirm: () => void;
  onCancel: () => void;
}

const TranslateChoicesConfirmDialog: React.FC<TranslateChoicesConfirmDialogProps> = ({ open, onConfirm, onCancel }) => {
  const { editor } = useEditor();
  const sourceLanguage = editor.activeFormLanguage;

  return (
    <Dialog open={open} onClose={onCancel}>
      <DialogTitle>
        <FormattedMessage id='dialogs.choices.translate.title' />
      </DialogTitle>
      <DialogContent>
        <Typography>
          <FormattedMessage 
            id='dialogs.choices.translate.confirm' 
            values={{ source: getLanguageName(sourceLanguage) }}
          />
        </Typography>
      </DialogContent>
      <DialogActions>
        <Button onClick={onCancel}>
          <FormattedMessage id='buttons.cancel' />
        </Button>
        <Button onClick={onConfirm} variant='contained'>
          <FormattedMessage id='buttons.translate' />
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default TranslateChoicesConfirmDialog;
