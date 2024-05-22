import React from "react";
import {
  Dialog, DialogTitle, DialogContent, Button, DialogActions, Typography, TextField, Box, TextareaAutosize,
  Alert
} from "@mui/material";
import { Check, Close, Warning } from "@mui/icons-material";
import { FormattedMessage } from "react-intl";
import { useComposer } from "../dialob";
import { useBackend } from "../backend/useBackend";
import { useEditor } from "../editor";
import { ErrorMessage } from "../components/ErrorComponents";
import { getErrorSeverity } from "../utils/ErrorUtils";
import { SaveResult } from "../backend/types";

const CreateTagDialog: React.FC<{ open: boolean, onClose: () => void }> = ({ open, onClose }) => {
  const { form } = useComposer();
  const { createTag, saveForm } = useBackend();
  const { editor, setErrors } = useEditor();
  const tagErrors = editor.errors?.filter(e => e.type === 'TAG_ERROR');
  const [name, setName] = React.useState<string>('');
  const [desc, setDesc] = React.useState<string>('');

  const handleCreate = () => {
    createTag({ name, description: desc, formName: form.name })
      .then((res) => {
        if (res.success) {
          if (tagErrors.length > 0) {
            saveForm(form, true)
              .then(saveResponse => {
                if (saveResponse.success && saveResponse.result) {
                  const result = saveResponse.result as SaveResult;
                  setErrors(result.errors);
                } else if (saveResponse.apiError) {
                  setErrors([{ level: 'FATAL', message: saveResponse.apiError.message }])
                }
                onClose();
              });
          } else {
            onClose();
          }
        } else if (res.apiError) {
          if (res.apiError.message === '409') {
            return setErrors([{ level: 'FATAL', type: 'TAG_ERROR', message: 'TAG_EXISTS' }]);
          } else {
            return setErrors([{ level: 'FATAL', type: 'TAG_ERROR', message: res.apiError.message }]);
          }
        }
      });
  }

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth='sm'>
      <DialogTitle sx={{ fontWeight: 'bold' }}>
        <FormattedMessage id='dialogs.create.tag.title' />
      </DialogTitle>
      <DialogContent sx={{ display: 'flex', borderTop: 1, borderBottom: 1, borderColor: 'divider', p: 0, height: '50vh' }}>
        <Box sx={{ p: 3, display: 'flex', flexDirection: 'column', width: 1 }}>
          <Typography fontWeight='bold'><FormattedMessage id='dialogs.create.tag.name' /></Typography>
          <TextField value={name} onChange={e => setName(e.target.value)} fullWidth />
          <Typography fontWeight='bold' sx={{ mt: 2 }}><FormattedMessage id='dialogs.create.tag.desc' /></Typography>
          <TextareaAutosize value={desc} onChange={e => setDesc(e.target.value)} minRows={4} />
          {tagErrors && tagErrors.length > 0 && tagErrors.map((error, index) => <Alert severity={getErrorSeverity(error)} sx={{ mt: 2 }} icon={<Warning />}>
            <Typography key={index} color={error.level.toLowerCase()}><ErrorMessage error={error} /></Typography>
          </Alert>)}
        </Box>
      </DialogContent>
      <DialogActions>
        <Button onClick={handleCreate} endIcon={<Check />} variant='contained'><FormattedMessage id='buttons.confirm' /></Button>
        <Button onClick={onClose} endIcon={<Close />}><FormattedMessage id='buttons.close' /></Button>
      </DialogActions>
    </Dialog>
  )
}

export default CreateTagDialog;
