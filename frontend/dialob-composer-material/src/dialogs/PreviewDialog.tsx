import React from "react";
import {
  Dialog, DialogTitle, DialogContent, DialogActions, Button, Box, Typography,
  TableHead, TableRow, TableCell, TableBody, TextField
} from "@mui/material";
import { Close, Visibility } from "@mui/icons-material";
import { useComposer } from "../dialob";
import { FormattedMessage } from "react-intl";
import { BorderedTable } from "../components/TableEditorComponents";
import { useBackend } from "../backend/useBackend";
import { useEditor } from "../editor";
import { CreateSessionResult, PreviewSessionContext } from "../backend/types";
import { isContextVariable } from "../utils/ItemUtils";

const ContextValueRow: React.FC<{ name: string, value: string }> = ({ name, value }) => {
  const { setContextValue } = useComposer();
  const [editableValue, setEditableValue] = React.useState<string>(value);

  React.useEffect(() => {
    const id = setTimeout(() => {
      if (editableValue !== '' && editableValue !== value) {
        setContextValue(name, editableValue)
      }
    }, 1000);
    return () => clearTimeout(id);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [editableValue]);

  return (
    <TableRow>
      <TableCell sx={{ p: 1 }}><Typography>{name}</Typography></TableCell>
      <TableCell sx={{ p: 1 }}>
        <TextField variant='standard' value={editableValue} onChange={e => setEditableValue(e.target.value)} InputProps={{ disableUnderline: true }} fullWidth />
      </TableCell>
    </TableRow>
  )
}

const PreviewDialog: React.FC<{ open: boolean, onClose: () => void }> = ({ open, onClose }) => {
  const { form } = useComposer();
  const { config, createPreviewSession } = useBackend();
  const { editor, setErrors } = useEditor();
  const contextValues = form.metadata.composer?.contextValues;
  const contextVariables = form.variables?.filter(isContextVariable);

  const initPreview = () => {
    const context: PreviewSessionContext = Object.entries(contextValues || {}).map(([name, value]) => ({ id: name, value }));
    createPreviewSession(form._id, editor.activeFormLanguage, context).then((response) => {
      const result = response.result as CreateSessionResult;
      if (response.success) {
        const win = window.open(`${config.transport.previewUrl}/${result._id}`);
        if (win) {
          win.focus();
        } else {
          setErrors([{ level: 'FATAL', message: 'FATAL_POPUP' }]);
        }
      } else if (response.apiError) {
        setErrors([{ level: 'FATAL', message: response.apiError.message }]);
      }
      onClose();
    });
  }

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth='md'>
      <DialogTitle sx={{ fontWeight: 'bold' }}>
        <FormattedMessage id='dialogs.preview.title' />
      </DialogTitle>
      <DialogContent sx={{ display: 'flex', borderTop: 1, borderBottom: 1, borderColor: 'divider', p: 0, height: '70vh' }}>
        <Box sx={{ p: 3, width: 1 }}>
          <Typography sx={{ mb: 2 }}><FormattedMessage id='dialogs.preview.desc' /></Typography>
          <BorderedTable>
            <TableHead>
              <TableRow>
                <TableCell width='30%' sx={{ p: 1 }}>
                  <Typography fontWeight='bold'><FormattedMessage id='dialogs.preview.table.id' /></Typography>
                </TableCell>
                <TableCell width='70%' sx={{ p: 1 }}>
                  <Typography fontWeight='bold'><FormattedMessage id='dialogs.preview.table.value' /></Typography>
                </TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {contextVariables?.map(variable => <ContextValueRow key={variable.name} name={variable.name} value={contextValues?.[variable.name] || ''} />)}
            </TableBody>
          </BorderedTable>
        </Box>
      </DialogContent>
      <DialogActions>
        <Button onClick={initPreview} endIcon={<Visibility />} variant='contained'><FormattedMessage id='buttons.preview' /></Button>
        <Button onClick={onClose} endIcon={<Close />}><FormattedMessage id='buttons.close' /></Button>
      </DialogActions>
    </Dialog>
  )
}

export default PreviewDialog;
