import React from 'react';
import { Button, Dialog, DialogActions, DialogContent, DialogTitle } from '@mui/material';
import { FormattedMessage } from 'react-intl';

const ConvertConfirmationDialog: React.FC<{
  type: 'local' | 'global' | undefined,
  onClick: () => void, onClose: () => void
}> = ({ type, onClick, onClose }) => {

  const handleClick = () => {
    onClick();
    onClose();
  }

  return (
    <Dialog open={type !== undefined} onClose={onClose}>
      <DialogTitle>
        <FormattedMessage id='dialogs.confirmation.convert.title' />
      </DialogTitle>
      <DialogContent>
        <FormattedMessage id={`dialogs.confirmation.convert.${type}.text`} />
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} variant='text' color='error'><FormattedMessage id='buttons.cancel' /></Button>
        <Button onClick={handleClick} variant='contained'><FormattedMessage id='buttons.confirm' /></Button>
      </DialogActions>
    </Dialog>
  );
};

export default ConvertConfirmationDialog;
