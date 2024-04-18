import React from "react";
import { Dialog, DialogTitle, DialogContent, Button, DialogActions, Typography, TextField, Box, TextareaAutosize } from "@mui/material";
import { Check, Close } from "@mui/icons-material";
import { FormattedMessage } from "react-intl";

const CreateTagDialog: React.FC<{ open: boolean, onClose: () => void }> = ({ open, onClose }) => {
  const [name, setName] = React.useState<string | undefined>();
  const [desc, setDesc] = React.useState<string | undefined>();
  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth='sm'>
      <DialogTitle>
        <Typography fontWeight='bold' variant='h4'><FormattedMessage id='dialogs.create.tag.title' /></Typography>
      </DialogTitle>
      <DialogContent sx={{ display: 'flex', borderTop: 1, borderBottom: 1, borderColor: 'divider', p: 0, height: '50vh' }}>
        <Box sx={{ p: 3, display: 'flex', flexDirection: 'column', width: 1 }}>
          <Typography fontWeight='bold'><FormattedMessage id='dialogs.create.tag.name' /></Typography>
          <TextField value={name || ''} onChange={e => setName(e.target.value)} fullWidth />
          <Typography fontWeight='bold' sx={{ mt: 2 }}><FormattedMessage id='dialogs.create.tag.desc' /></Typography>
          <TextareaAutosize value={desc || ''} onChange={e => setDesc(e.target.value)} minRows={4} />
        </Box>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} endIcon={<Check />} variant='contained'><FormattedMessage id='buttons.confirm' /></Button>
        <Button onClick={onClose} endIcon={<Close />}><FormattedMessage id='buttons.close' /></Button>
      </DialogActions>
    </Dialog>
  )
}

export default CreateTagDialog;
