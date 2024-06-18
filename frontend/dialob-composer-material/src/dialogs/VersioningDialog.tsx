import React from "react";
import {
  Dialog, DialogTitle, DialogContent, Button, DialogActions, Typography, Box,
  TableHead, TableRow, TableCell, TableBody, alpha, useTheme, IconButton,
  Tooltip
} from "@mui/material";
import { Close, ContentCopy, Download, EditNote, Help, LocalOffer } from "@mui/icons-material";
import { FormattedMessage } from "react-intl";
import { ComposerTag, useComposer } from "../dialob";
import { BorderedTable } from "../components/TableEditorComponents";
import { downloadForm } from "../utils/ParseUtils";
import { useBackend } from "../backend/useBackend";
import { useEditor } from "../editor";
import { SaveResult } from "../backend/types";
import { useDocs } from "../utils/DocsUtils";


const VersioningDialog: React.FC<{ open: boolean, onClose: () => void }> = ({ open, onClose }) => {
  const theme = useTheme();
  const { form, setForm } = useComposer();
  const { getTags, loadForm, saveForm } = useBackend();
  const { setErrors, clearErrors } = useEditor();
  const docsUrl = useDocs('versioning');
  const [tags, setTags] = React.useState<ComposerTag[]>([]);

  const LATEST_TAG: ComposerTag = React.useMemo(() => ({
    formId: form._id, name: 'LATEST', formName: form.name, description: 'Latest version',
    created: new Date().toISOString(), type: 'NORMAL'
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }), []);

  React.useEffect(() => {
    if (open) {
      getTags(form.name).then(setTags);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [open]);

  const handleLoadVersion = (tag: ComposerTag) => {
    loadForm(tag.formId, tag.name).then(form => {
      if (tag.name === 'LATEST') {
        saveForm(form, true)
          .then(saveResponse => {
            if (saveResponse.success && saveResponse.result) {
              const result = saveResponse.result as SaveResult;
              const errors = result.errors?.map(e => {
                if (e.itemId && e.itemId.includes(':')) {
                  const itemId = e.itemId.split(':')[0];
                  return { ...e, itemId: itemId };
                }
                return e;
              });
              setErrors(errors);
            } else if (saveResponse.apiError) {
              setErrors([{ level: 'FATAL', message: saveResponse.apiError.message }])
            }
          });
      } else {
        setForm(form, tag.name);
        clearErrors();
      }
      onClose();
    });
  }

  const handleDownload = (tag: ComposerTag) => {
    if (tag.name === 'LATEST') {
      downloadForm(form);
    } else {
      loadForm(tag.formId, tag.name).then(form => {
        downloadForm(form);
      });
    }
  }

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth='lg'>
      <DialogTitle sx={{ fontWeight: 'bold', display: 'flex', justifyContent: 'space-between' }}>
        <FormattedMessage id='dialogs.versioning.title' />
        <Button variant='outlined' endIcon={<Help />}
          onClick={() => window.open(docsUrl, "_blank")}>
          <FormattedMessage id='buttons.help' />
        </Button>
      </DialogTitle>
      <DialogContent sx={{ display: 'flex', flexDirection: 'column', borderTop: 1, borderBottom: 1, borderColor: 'divider', p: 0, height: '70vh' }}>
        <Box sx={{ p: 3 }}>
          <Typography variant='h5' fontWeight='bold'><FormattedMessage id='dialogs.versioning.list' /></Typography>
          <Typography sx={{ my: 2 }}><FormattedMessage id='dialogs.versioning.editable.desc' /></Typography>
          <BorderedTable>
            <TableHead>
              <TableRow>
                <TableCell sx={{ p: 1 }}>
                  <Typography fontWeight='bold'><FormattedMessage id='dialogs.versioning.list.header.name' /></Typography>
                </TableCell>
                <TableCell sx={{ p: 1 }}>
                  <Typography fontWeight='bold'><FormattedMessage id='dialogs.versioning.list.header.description' /></Typography>
                </TableCell>
                <TableCell sx={{ p: 1 }}>
                  <Typography fontWeight='bold'><FormattedMessage id='dialogs.versioning.list.header.created' /></Typography>
                </TableCell>
                <TableCell align='center' sx={{ p: 1 }}>
                  <Typography fontWeight='bold'><FormattedMessage id='dialogs.versioning.list.header.actions' /></Typography>
                </TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {[LATEST_TAG, ...tags].map(tag => (
                <TableRow key={tag.formId} sx={tag.name === 'LATEST' ? { backgroundColor: alpha(theme.palette.primary.main, 0.1) } : {}}>
                  <TableCell>
                    <Tooltip
                      title={<Button endIcon={<ContentCopy />} variant='text' color='inherit' onClick={() => navigator.clipboard.writeText(tag.formId)}>
                        <Typography><FormattedMessage id='dialogs.versioning.copy.id' /></Typography>
                      </Button>}
                      placement='left'>
                      <Box sx={{ display: 'flex', p: 1 }}>
                        {tag.name === 'LATEST' ? <EditNote fontSize='medium' color='primary' sx={{ mr: 1 }} /> : <LocalOffer fontSize='small' sx={{ mr: 1 }} />}
                        <Typography>{tag.name}</Typography>
                      </Box>
                    </Tooltip>
                  </TableCell>
                  <TableCell sx={{ p: 1 }}><Typography>{tag.description}</Typography></TableCell>
                  <TableCell sx={{ p: 1 }}><Typography>{new Date(tag.created).toLocaleString('en-GB')}</Typography></TableCell>
                  <TableCell align='center'>
                    <Button variant='outlined' color='primary'
                      disabled={tag.name === form._tag || (form._tag === undefined && tag.name === 'LATEST')}
                      onClick={() => handleLoadVersion(tag)}>
                      <FormattedMessage id='buttons.activate' />
                    </Button>
                    <IconButton sx={{ ml: 1 }} onClick={() => handleDownload(tag)}><Download color='success' /></IconButton>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </BorderedTable>
        </Box>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} endIcon={<Close />}><FormattedMessage id='buttons.close' /></Button>
      </DialogActions>
    </Dialog>
  )
}

export default VersioningDialog;
