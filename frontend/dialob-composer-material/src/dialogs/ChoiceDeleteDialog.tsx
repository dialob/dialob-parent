import React from 'react';
import { Button, Dialog, DialogActions, DialogContent, DialogTitle } from '@mui/material';
import { FormattedMessage } from 'react-intl';

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
      <DialogActions>
        <Button onClick={onClose} variant='text' color='error'><FormattedMessage id='buttons.cancel' /></Button>
        <Button onClick={handleClick} variant='contained'><FormattedMessage id='buttons.confirm' /></Button>
      </DialogActions>
    </Dialog>
  );
};

export default ChoiceDeleteDialog;
