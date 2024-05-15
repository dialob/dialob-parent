import React from 'react';
import { useEditor } from '../editor';
import { Dialog, DialogContent, DialogTitle } from '@mui/material';
import { useComposer } from '../dialob';
import { FormattedMessage } from 'react-intl';
import { DialogActionButtons } from './DialogComponents';
import { useBackend } from '../backend/useBackend';
import { DuplicateResult } from '../backend/types';

const ConfirmationDialog: React.FC = () => {
  const { form, deleteItem, duplicateItem: onDuplicate } = useComposer();
  const { duplicateItem } = useBackend();
  const { editor, setConfirmationDialogType } = useEditor();
  const type = editor.confirmationDialogType;
  const activeItem = editor.activeItem;

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
      duplicateItem(form, activeItem.id).then(duplicateResponse => {
        if (duplicateResponse.success && duplicateResponse.result) {
          const duplicateRes = duplicateResponse.result as DuplicateResult;
          const newData = duplicateRes.form.data;
          const newItem = newData[activeItem.id + 1];
          const parentItemId = Object.values(newData).find(i => i.items && i.items.includes(newItem.id))!.id;
          onDuplicate(newItem, parentItemId, activeItem.id);
        }
      });
      handleClose();
    }
  }

  return (
    <Dialog open onClose={handleClose}>
      <DialogTitle>
        {type === 'delete' && <FormattedMessage id='dialogs.confirmation.delete.title' />}
        {type === 'duplicate' && <FormattedMessage id='dialogs.confirmation.duplicate.title' />}
      </DialogTitle>
      <DialogContent>
        {type === 'delete' && <FormattedMessage id='dialogs.confirmation.delete.text' values={{ itemId: activeItem.id }} />}
        {type === 'duplicate' && <FormattedMessage id='dialogs.confirmation.duplicate.text' values={{ itemId: activeItem.id }} />}
      </DialogContent>
      <DialogActionButtons handleClick={handleClick} handleClose={handleClose} />
    </Dialog>
  );
};

export default ConfirmationDialog;
