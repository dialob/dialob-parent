import React from 'react';
import { FormattedMessage } from 'react-intl';
import { Add, Close, Delete, Download, Upload, Visibility, Warning } from '@mui/icons-material';
import {
  Alert,
  AlertColor,
  Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, IconButton, List,
  ListItemButton, Popover, Stack, TableCell, TableContainer, TableHead, TableRow, TextField, Typography
} from '@mui/material';
import { DialobItem, ValueSet, ValueSetEntry, useComposer } from '../dialob';
import { generateValueSetId } from '../dialob/reducer';
import { StyledTable } from '../components/TableEditorComponents';
import ChoiceList from '../components/ChoiceList';
import UploadValuesetDialog from './UploadValuesetDialog';
import { useEditor } from '../editor';
import { getErrorColor } from '../utils/ErrorUtils';
import { scrollToItem } from '../utils/ScrollUtils';
import { downloadValueSet } from '../utils/ParseUtils';
import { ErrorMessage } from '../components/ErrorComponents';

interface GlobalValueSet {
  id: string;
  label?: string;
  entries: ValueSetEntry[];
}

const GlobalListsDialog: React.FC<{ open: boolean, onClose: () => void }> = ({ open, onClose }) => {
  const { form, createValueSet, addValueSetEntry, setGlobalValueSetName, updateItem, deleteGlobalValueSet } = useComposer();
  const { editor, setActiveList, setActivePage, setHighlightedItem } = useEditor();
  const dialogOpen = open || editor.activeList !== undefined;
  const formLanguages = form.metadata.languages;
  const [globalValueSets, setGlobalValueSets] = React.useState<GlobalValueSet[] | undefined>(undefined);
  const [currentValueSet, setCurrentValueSet] = React.useState<ValueSet | undefined>(undefined);
  const [uploadDialogOpen, setUploadDialogOpen] = React.useState(false);
  const [anchorEl, setAnchorEl] = React.useState<HTMLElement | null>(null);
  const [name, setName] = React.useState<string | undefined>(undefined);
  const users = currentValueSet && Object.values(form.data).filter(i => i.valueSetId === currentValueSet?.id);
  const itemErrors = editor.errors.filter(e => e.itemId === currentValueSet?.id);

  React.useEffect(() => {
    const activeList = form.valueSets?.find(vs => vs.id === editor.activeList);
    if (activeList) {
      setCurrentValueSet(activeList);
    }
  }, [editor.activeList, form.valueSets]);

  React.useEffect(() => {
    if (dialogOpen) {
    const gvs = form.metadata.composer?.globalValueSets;
    const valueSets = form.valueSets;
    const mappedGvs = gvs?.map(gvs => {
      // eslint-disable-next-line @typescript-eslint/no-non-null-asserted-optional-chain
      const found = valueSets?.find(vs => vs.id === gvs.valueSetId)!;
      return { ...found, label: gvs.label }
    });
    setGlobalValueSets(mappedGvs);
    if (!currentValueSet) {
      setCurrentValueSet(mappedGvs?.[0]);
    }
    setName(mappedGvs?.find(gvs => gvs.id === currentValueSet?.id)?.label || '');
  }
  }, [form.metadata.composer?.globalValueSets, currentValueSet, form.valueSets, dialogOpen]);

  React.useEffect(() => {
    if (currentValueSet && name) {
      const id = setTimeout(() => {
        setGlobalValueSetName(currentValueSet.id, name);
      }, 1000);
      return () => clearTimeout(id);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [name])

  const handleClose = () => {
    setActiveList(undefined);
    setCurrentValueSet(undefined);
    onClose();
  }

  const addEntry = () => {
    if (currentValueSet) {
      const newEntry = {
        id: 'choice' + (currentValueSet.entries.length + 1),
        label: {},
      };
      addValueSetEntry(currentValueSet.id, newEntry);
      setCurrentValueSet({ ...currentValueSet, entries: [...currentValueSet.entries, newEntry] });
    }
  }

  const addNewList = () => {
    const newGvsIndex = form.metadata.composer?.globalValueSets?.length ?? 0;
    const newGvsName = 'untitled' + (newGvsIndex + 1);
    const newGvsId = generateValueSetId(form);
    createValueSet(null);
    if (newGvsId) {
      setGlobalValueSetName(newGvsId, newGvsName);
      setCurrentValueSet({ id: newGvsId, entries: [] });
    }
  }

  const convertToLocalList = (valueSet: ValueSet, item: DialobItem) => {
    const newId = generateValueSetId(form);
    createValueSet(item?.id, valueSet?.entries);
    updateItem(item?.id, 'valueSetId', newId);
  }

  const deleteList = () => {
    if (currentValueSet) {
      if (users) {
        users.forEach(u => convertToLocalList(currentValueSet, u));
      }
      deleteGlobalValueSet(currentValueSet.id);
      setCurrentValueSet(undefined);
    }
  }

  const handleScroll = (item: DialobItem) => {
    handleClose();
    setAnchorEl(null);
    setHighlightedItem(item);
    scrollToItem(item.id, Object.values(form.data), editor.activePage, setActivePage);
  }

  return (
    <>
      <UploadValuesetDialog open={uploadDialogOpen} onClose={() => setUploadDialogOpen(true)} currentValueSet={currentValueSet} setCurrentValueSet={setCurrentValueSet} />
      <Dialog open={dialogOpen} onClose={handleClose} fullWidth maxWidth='xl'>
        <DialogTitle sx={{ display: 'flex', alignItems: 'center' }}>
          <Typography fontWeight='bold'>Global lists</Typography>
          <Box flexGrow={1} />
          {globalValueSets && globalValueSets.length > 0 && <>
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
                      <ListItemButton key={i.id}
                        sx={{ justifyContent: 'flex-start', color: 'text.primary' }}
                        onClick={() => handleScroll(i)}
                      >
                        {i.id}
                      </ListItemButton>
                    ))}
                  </List>
                </Popover>
              </>
            }
          </>}
          <Button onClick={addNewList} endIcon={<Add />} sx={{ ml: 2 }}>Add new list</Button>
        </DialogTitle>
        <DialogContent sx={{ borderTop: 1, borderBottom: 1, borderColor: 'divider', p: 0 }}>
          <Box sx={{ display: 'flex', height: '70vh', p: 3 }}>
            {globalValueSets && globalValueSets.length > 0 && <>
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
                  <Button endIcon={<Delete />} color='error' onClick={deleteList}>Delete list</Button>
                </Box>
                <TableContainer>
                  <StyledTable>
                    <TableHead>
                      <TableRow>
                        <TableCell width='20%' align='center'>
                          <IconButton onClick={addEntry}><Add color='success' /></IconButton>
                          <IconButton onClick={() => setUploadDialogOpen(true)}><Upload /></IconButton>
                          <IconButton onClick={() => downloadValueSet(currentValueSet)}><Download /></IconButton>
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
                {itemErrors.map((error, index) => <Alert key={index} severity={error.severity.toLowerCase() as AlertColor} sx={{ mt: 2 }} icon={<Warning />}>
                  <Typography><ErrorMessage error={error} /></Typography>
                </Alert>)}
              </Box>
            </>}
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
