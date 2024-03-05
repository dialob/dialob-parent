import React from 'react';
import { FormattedMessage } from 'react-intl';
import Papa from 'papaparse';
import FileSaver from 'file-saver';
import { Add, Close, Delete, Download, Upload, Visibility } from '@mui/icons-material';
import {
  Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, IconButton, List,
  ListItemButton, Popover, Stack, TableCell, TableContainer, TableHead, TableRow, TextField, Typography
} from '@mui/material';
import { ValueSet, ValueSetEntry, useComposer } from '../dialob';
import { generateValueSetId } from '../dialob/reducer';
import { StyledTable } from '../components/TableEditorComponents';
import ChoiceList from '../components/ChoiceList';
import UploadValuesetDialog from './UploadValuesetDialog';
import { useEditor } from '../editor';
import { getErrorColor } from '../utils/ErrorUtils';

interface GlobalValueSet {
  id: string;
  label?: string;
  entries: ValueSetEntry[];
}

const GlobalListsDialog: React.FC<{ open: boolean, onClose: () => void }> = ({ open, onClose }) => {
  const { form, createValueSet, addValueSetEntry, setGlobalValueSetName } = useComposer();
  const { editor, setActiveList } = useEditor();
  const dialogOpen = open || editor.activeList !== undefined;
  const formLanguages = form.metadata.languages;
  const [globalValueSets, setGlobalValueSets] = React.useState<GlobalValueSet[] | undefined>(undefined);
  const [currentValueSet, setCurrentValueSet] = React.useState<ValueSet | undefined>(undefined);
  const [uploadDialogOpen, setUploadDialogOpen] = React.useState(false);
  const [anchorEl, setAnchorEl] = React.useState<HTMLElement | null>(null);
  const [name, setName] = React.useState<string | undefined>(undefined);
  const users = currentValueSet && Object.values(form.data).filter(i => i.valueSetId === currentValueSet?.id);

  React.useEffect(() => {
    const activeList = form.valueSets?.find(vs => vs.id === editor.activeList);
    if (activeList) {
      setCurrentValueSet(activeList);
    }
  }, [editor.activeList]);

  React.useEffect(() => {
    const gvs = form.metadata.composer?.globalValueSets;
    const valueSets = form.valueSets;
    const mappedGvs = gvs?.map(gvs => {
      const found = valueSets?.find(vs => vs.id === gvs.valueSetId)!;
      return { ...found, label: gvs.label }
    });
    setGlobalValueSets(mappedGvs);
    if (!currentValueSet) {
      setCurrentValueSet(mappedGvs?.[0]);
    }
    setName(mappedGvs?.find(gvs => gvs.id === currentValueSet?.id)?.label || '');
  }, [form.metadata.composer?.globalValueSets, currentValueSet]);

  React.useEffect(() => {
    if (currentValueSet && name) {
      const id = setTimeout(() => {
        setGlobalValueSetName(currentValueSet.id, name);
      }, 1000);
      return () => clearTimeout(id);
    }
  }, [name])

  const handleClose = () => {
    setActiveList(undefined);
    onClose();
  }

  const handleAddValueSetEntry = () => {
    if (currentValueSet) {
      const newEntry = {
        id: 'choice' + (currentValueSet.entries.length + 1),
        label: {},
      };
      addValueSetEntry(currentValueSet.id, newEntry);
      setCurrentValueSet({ ...currentValueSet, entries: [...currentValueSet.entries, newEntry] });
    }
  }

  const addGlobalList = () => {
    const newGvsIndex = form.metadata.composer?.globalValueSets?.length ?? 0;
    const newGvsName = 'untitled' + (newGvsIndex + 1);
    const newGvsId = generateValueSetId(form);
    createValueSet(null);
    if (newGvsId) {
      setGlobalValueSetName(newGvsId, newGvsName);
      setCurrentValueSet({ id: newGvsId, entries: [] });
    }
  }

  const downloadValueSet = () => {
    if (!currentValueSet) {
      return;
    }
    const entries = currentValueSet?.entries;
    const result: { [key: string]: any }[] = [];
    entries.forEach(e => {
      let entry: { [key: string]: any } = { ID: e.id };
      for (const lang in e.label) {
        entry[lang] = e.label[lang];
      }
      result.push(entry);
    });
    const csv = Papa.unparse(result);
    const blob = new Blob([csv], { type: 'text/csv' });
    FileSaver.saveAs(blob, `valueSet-${currentValueSet.id}.csv`);
  }

  return (
    <>
      <UploadValuesetDialog open={uploadDialogOpen} onClose={() => setUploadDialogOpen(true)} currentValueSet={currentValueSet} setCurrentValueSet={setCurrentValueSet} />
      <Dialog open={dialogOpen} onClose={handleClose} fullWidth maxWidth='xl'>
        <DialogTitle sx={{ display: 'flex', alignItems: 'center' }}>
          <Typography variant='h5' fontWeight='bold'>Global lists</Typography>
          <Box flexGrow={1} />
          <Typography sx={{ mr: 2 }}>Users: <b>{users ? users.length : 0}</b></Typography>
          {users &&
            <>
              <Button onClick={(e) => setAnchorEl(e.currentTarget)} endIcon={<Visibility />}>Show users</Button>
              <Popover open={Boolean(anchorEl)} anchorEl={anchorEl} onClose={() => setAnchorEl(null)} anchorOrigin={{
                vertical: 'bottom',
                horizontal: 'left',
              }}>
                <List>
                  {users.map(i => (
                    <ListItemButton key={i.id} sx={{ justifyContent: 'flex-start', color: 'text.primary' }}>{i.id}</ListItemButton>
                  ))}
                </List>
              </Popover>
            </>
          }
          <Button onClick={addGlobalList} endIcon={<Add />} sx={{ ml: 2 }}>Add new list</Button>
        </DialogTitle>
        <DialogContent sx={{ borderTop: 1, borderBottom: 1, borderColor: 'divider', p: 0 }}>
          <Box sx={{ display: 'flex', height: '70vh', p: 3 }}>
            <Stack sx={{ mr: 3 }}>
              {globalValueSets?.map(gvs => {
                const errorColor = getErrorColor(editor.errors, gvs.id);
                return <Button key={gvs.id} variant={gvs.id === currentValueSet?.id ? 'contained' : 'outlined'} color={errorColor}
                  onClick={() => setCurrentValueSet({ id: gvs.id, entries: gvs.entries })}>
                  {gvs.label}
                </Button>
              })}
            </Stack>
            <Box sx={{ width: 1 }}>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', mb: 2 }}>
                <TextField value={name || ''} onChange={(e) => setName(e.target.value)} sx={{ width: '70%' }} />
                <Button endIcon={<Delete />} color='error'>Delete list</Button>
              </Box>
              <TableContainer>
                <StyledTable>
                  <TableHead>
                    <TableRow>
                      <TableCell width='20%' align='center'>
                        <IconButton onClick={handleAddValueSetEntry}><Add color='success' /></IconButton>
                        <IconButton onClick={() => setUploadDialogOpen(true)}><Upload /></IconButton>
                        <IconButton onClick={downloadValueSet}><Download /></IconButton>
                      </TableCell>
                      <TableCell width='30%' sx={{ p: 1 }}><Typography fontWeight='bold'><FormattedMessage id='dialogs.options.key' /></Typography></TableCell>
                      {formLanguages?.map(lang => (
                        <TableCell key={lang} width={formLanguages ? `${50 / formLanguages.length}%` : 0} sx={{ p: 1 }}>
                          <Typography fontWeight='bold'>
                            <FormattedMessage id='dialogs.options.text' /> - <FormattedMessage id={`locales.${lang}`} />
                          </Typography>
                        </TableCell>
                      ))}
                    </TableRow>
                  </TableHead>
                  <ChoiceList valueSet={currentValueSet} updateValueSet={setCurrentValueSet} isGlobal={true} />
                </StyledTable>
              </TableContainer>
            </Box>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose} endIcon={<Close />}><FormattedMessage id='buttons.close' /></Button>
        </DialogActions>
      </Dialog>
    </>
  );
}

export default GlobalListsDialog;
