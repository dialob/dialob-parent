import React from "react";
import { FormattedMessage } from "react-intl";
import { Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField, Typography } from "@mui/material";
import { Check, Close } from "@mui/icons-material";
import { ComposerTag } from "../dialob";
import { useBackend } from "../backend/useBackend";
import { useEditor } from "../editor";
import { SaveResult } from "../backend/types";

const CopyTagDialog: React.FC<{ tag: ComposerTag | undefined, onClose: () => void }> = ({ tag, onClose }) => {
  const [id, setId] = React.useState<string>('');
  const { loadForm, createForm } = useBackend();
  const { setErrors } = useEditor();

  if (!tag) {
    return null;
  }

  const handleCreateNew = () => {
    loadForm(tag.formId + '', tag.name).then(form => {
      const newForm = { ...form, _id: undefined, _rev: undefined, _tag: undefined, name: id };
      createForm(newForm)
        .then(saveResponse => {
          if (saveResponse.success && saveResponse.result) {
            const result = saveResponse.result as SaveResult;
            const location = window.location;
            if (location.search.includes('?id=')) {
              location.search = `id=${id}`;
            } else {
              location.pathname = id;
            }
          } else if (saveResponse.apiError) {
            setErrors([{ level: 'FATAL', message: saveResponse.apiError.message }])
            onClose();
          }
        })
        .catch(err => {
          setErrors([{ level: 'FATAL', message: err.message }]);
          onClose();
        }
      );
    });
  }

  return (
    <Dialog open={true} onClose={onClose} fullWidth maxWidth='sm'>
      <DialogTitle sx={{ fontWeight: 'bold' }}>
        <FormattedMessage id='dialogs.versioning.copy.new' />
      </DialogTitle>
      <DialogContent sx={{ borderTop: 1, borderBottom: 1, borderColor: 'divider', p: 0 }}>
        <Box sx={{ p: 2, pb: 3 }}>
          <Typography><FormattedMessage id='dialogs.versioning.copy.new.id' /></Typography>
          <TextField value={id} onChange={e => setId(e.target.value)} fullWidth />
        </Box>
      </DialogContent>
      <DialogActions>
        <Button variant="contained" onClick={handleCreateNew} endIcon={<Check />}><FormattedMessage id='buttons.confirm' /></Button>
        <Button onClick={onClose} endIcon={<Close />}><FormattedMessage id='buttons.close' /></Button>
      </DialogActions>
    </Dialog>
  )
}

export default CopyTagDialog;
