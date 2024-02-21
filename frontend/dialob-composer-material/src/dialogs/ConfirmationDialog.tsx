import React from 'react';
import { useEditor } from '../editor';
import { Dialog, DialogContent, DialogTitle } from '@mui/material';
import { useComposer } from '../dialob';
import { FormattedMessage } from 'react-intl';
import { DialogActionButtons } from './DialogComponents';

const ConfirmationDialog: React.FC = () => {
  const { deleteItem } = useComposer();
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
