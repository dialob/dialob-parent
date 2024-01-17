import React from 'react';
import { useEditor } from '../editor';
import { Button, Dialog, DialogActions, DialogContent, DialogTitle } from '@mui/material';
import { useComposer } from '../dialob';

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
        {type === 'delete' && 'Delete item'}
        {type === 'duplicate' && 'Duplicate item'}
      </DialogTitle>
      <DialogContent
        sx={{
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
          gap: 2,
        }}
      >
        {type === 'delete' && `Are you sure you want to delete ${activeItem.id}? `}
        {type === 'duplicate' && `Are you sure you want to duplicate ${activeItem.id}? `}
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose} variant='text' color='error'>Cancel</Button>
        <Button onClick={handleClick} variant='contained'>Confirm</Button>
      </DialogActions>
    </Dialog>
  );
};

export default ConfirmationDialog;
