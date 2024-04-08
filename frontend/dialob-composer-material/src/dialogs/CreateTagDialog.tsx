import React from "react";
import { Dialog, DialogTitle, DialogContent, Button, DialogActions, Typography } from "@mui/material";
import { Close } from "@mui/icons-material";
import { FormattedMessage } from "react-intl";

const CreateTagDialog: React.FC<{ open: boolean, onClose: () => void }> = ({ open, onClose }) => {
  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth='sm'>
      <DialogTitle>
        <Typography fontWeight='bold' variant='h4'><FormattedMessage id='dialogs.create.tag.title' /></Typography>
      </DialogTitle>
      <DialogContent sx={{ display: 'flex', borderTop: 1, borderBottom: 1, borderColor: 'divider', p: 0, height: '70vh' }}>
        CreateTagDialog
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} endIcon={<Close />}><FormattedMessage id='buttons.close' /></Button>
      </DialogActions>
    </Dialog>
  )
}

export default CreateTagDialog;
