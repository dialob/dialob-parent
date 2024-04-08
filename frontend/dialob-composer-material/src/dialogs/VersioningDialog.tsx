import React from "react";
import {
  Dialog, DialogTitle, DialogContent, Button, DialogActions, Typography, Box,
  TableHead, TableRow, TableCell, TableBody, alpha, useTheme, IconButton
} from "@mui/material";
import { Close, Download, EditNote, LocalOffer } from "@mui/icons-material";
import { FormattedMessage } from "react-intl";
import { ComposerTag, useComposer } from "../dialob";
import { BorderedTable } from "../components/TableEditorComponents";
import { downloadForm } from "../utils/ParseUtils";

const LATEST_TAG: ComposerTag = { id: '0', name: 'LATEST', description: 'Latest version', created: new Date().toISOString() };

const DEMO_TAGS: ComposerTag[] = [
  { id: '1', name: 'Tag 1', description: 'Description 1', created: new Date('2024-04-03T15:24:00').toISOString() },
  { id: '2', name: 'Tag 2', description: 'Description 2', created: new Date('2024-04-04T12:48:00').toISOString() },
  { id: '3', name: 'Tag 3', description: 'Description 3', created: new Date('2024-04-05T14:32:00').toISOString() },
  { id: '4', name: 'Tag 4', description: 'Description 4', created: new Date('2024-04-06T19:17:00').toISOString() },
  { id: '5', name: 'Tag 5', description: 'Description 5', created: new Date('2024-04-07T03:20:00').toISOString() },
];

const VersioningDialog: React.FC<{ open: boolean, onClose: () => void }> = ({ open, onClose }) => {
  const theme = useTheme();
  const { form, loadVersion } = useComposer();

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth='lg'>
      <DialogTitle>
        <Typography fontWeight='bold' variant='h4'><FormattedMessage id='dialogs.versioning.title' /></Typography>
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
              {[LATEST_TAG, ...DEMO_TAGS].map(tag => (
                <TableRow key={tag.id} sx={tag.name === 'LATEST' ? { backgroundColor: alpha(theme.palette.primary.main, 0.1) } : {}}>
                  <TableCell sx={{ display: 'flex', alignItems: 'center', p: 1 }}>
                    {tag.name === 'LATEST' ? <EditNote fontSize='medium' color='primary' sx={{ mr: 1 }} /> : <LocalOffer fontSize='small' sx={{ mr: 1 }} />}
                    <Typography>{tag.name}</Typography>
                  </TableCell>
                  <TableCell sx={{ p: 1 }}><Typography>{tag.description}</Typography></TableCell>
                  <TableCell sx={{ p: 1 }}><Typography>{new Date(tag.created).toLocaleString('en-GB')}</Typography></TableCell>
                  <TableCell align='center'>
                    <Button variant='outlined' color='primary'
                      disabled={tag.name === form._tag || (form._tag === undefined && tag.name === 'LATEST')}
                      onClick={() => loadVersion(tag.name)}>
                      <FormattedMessage id='buttons.activate' />
                    </Button>
                    <IconButton sx={{ ml: 1 }} onClick={() => downloadForm(form, tag.name)}><Download color='success' /></IconButton>
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
