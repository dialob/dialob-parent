import React from 'react';
import { useEditor } from '../editor';
import { CircularProgress, Dialog, DialogActions, DialogContent, DialogTitle } from '@mui/material';
import { useComposer } from '../dialob';
import { FormattedMessage } from 'react-intl';
import { DialogActionButtons } from './DialogComponents';
import { useBackend } from '../backend/useBackend';
import { DuplicateResult } from '../backend/types';

const ConfirmationDialog: React.FC = () => {
  const { form, deleteItem, setForm } = useComposer();
  const { duplicateItem } = useBackend();
  const { editor, setConfirmationDialogType, setErrors } = useEditor();
  const type = editor.confirmationDialogType;
  const activeItem = editor.activeItem;
  const [loading, setLoading] = React.useState(false);

  if (type === undefined || activeItem === undefined) {
    return null;
  }

  const handleClose = () => {
    setConfirmationDialogType(undefined);
  }

  const handleClick = () => {
    if (type === 'delete') {
      deleteItem(activeItem.id);
      handleClose();
    } else if (type === 'duplicate') {
      setLoading(true);
      duplicateItem(form, activeItem.id)
        .then(duplicateResponse => {
          if (duplicateResponse.success && duplicateResponse.result) {
            const duplicateRes = duplicateResponse.result as DuplicateResult;
            setForm(duplicateRes.form, undefined, true);
          } else if (duplicateResponse.apiError) {
            setErrors([{ level: 'FATAL', message: duplicateResponse.apiError.message }]);
          }
          setLoading(false);
          handleClose();
        });
    }
  }

  return (
    <Dialog open onClose={handleClose} fullWidth maxWidth='sm'>
      <DialogTitle>
        {type === 'delete' && <FormattedMessage id='dialogs.confirmation.delete.title' />}
        {type === 'duplicate' && <FormattedMessage id='dialogs.confirmation.duplicate.title' />}
      </DialogTitle>
      <DialogContent>
        {type === 'delete' && <FormattedMessage id='dialogs.confirmation.delete.text' values={{ itemId: activeItem.id }} />}
        {type === 'duplicate' && <FormattedMessage id='dialogs.confirmation.duplicate.text' values={{ itemId: activeItem.id }} />}
      </DialogContent>
      {loading ? <DialogActions><CircularProgress sx={{ m: 1 }} /></DialogActions> :
        <DialogActionButtons handleClick={handleClick} handleClose={handleClose} />}
    </Dialog>
  );
};

export default ConfirmationDialog;
