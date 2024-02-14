import React from 'react';
import { Dialog, DialogContent, DialogTitle } from '@mui/material';
import { FormattedMessage } from 'react-intl';
import { DialogActionButtons } from './DialogComponents';

const ChoiceDeleteDialog: React.FC<{
  open: boolean, itemId: string,
  onClick: () => void, onClose: () => void
}> = ({ open, itemId, onClick, onClose }) => {

  const handleClick = () => {
    onClick();
    onClose();
  }

  return (
    <Dialog open={open} onClose={onClose}>
      <DialogTitle>
        <FormattedMessage id='dialogs.confirmation.delete.choice.title' />
      </DialogTitle>
      <DialogContent>
        <FormattedMessage id={`dialogs.confirmation.delete.choice.text`} values={{ itemId }} />
      </DialogContent>
      <DialogActionButtons handleClose={onClose} handleClick={handleClick} />
    </Dialog>
  );
};

export default ChoiceDeleteDialog;
