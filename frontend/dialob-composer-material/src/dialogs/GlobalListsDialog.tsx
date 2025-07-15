import React from 'react';
import { FormattedMessage } from 'react-intl';
import { Add, Check, Close, Delete, Download, Help, Upload, Visibility, Warning } from '@mui/icons-material';
import {
  Alert, Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, IconButton, List,
  ListItemButton, Popover, Stack, TableCell, TableContainer, TableHead, TableRow, TextField, Typography
} from '@mui/material';
import { useComposer } from '../dialob';
import { DialobItem, ValueSet, ValueSetEntry } from '../types';
import { BorderedTable } from '../components/TableEditorComponents';
import ChoiceList from '../components/ChoiceList';
import UploadValuesetDialog from './UploadValuesetDialog';
import { useEditor } from '../editor';
import { getErrorSeverity, getItemErrorColor } from '../utils/ErrorUtils';
import { scrollToItem } from '../utils/ScrollUtils';
import { downloadValueSet } from '../utils/ParseUtils';
import { ErrorMessage } from '../components/ErrorComponents';
import { BoldedMessage } from '../intl/BoldedMessage';
import { useDocs } from '../utils/DocsUtils';
import { useSave } from './contexts/saving/useSave';
import { generateValueSetId } from './contexts/saving/reducer';

interface GlobalValueSet {
  id: string;
  label?: string;
  entries?: ValueSetEntry[];
}

const SaveButton: React.FC = () => {
  const { form, applyListChanges } = useComposer();
  const { savingState } = useSave();

  const hasChanges = React.useMemo(() => {
    if (!savingState.valueSets || !savingState.composerMetadata?.globalValueSets) {
      return false;
    }
    return JSON.stringify(savingState.valueSets) !== JSON.stringify(form.valueSets) ||
           JSON.stringify(savingState.composerMetadata?.globalValueSets) !== JSON.stringify(form.metadata.composer?.globalValueSets);
  }, [savingState, form.valueSets, form.metadata.composer?.globalValueSets]);
  
  const handleSave = () => {
    if (savingState.valueSets && savingState.composerMetadata?.globalValueSets) {
      applyListChanges(savingState);
    }
  }

  return (
    <Button
      variant="contained"
      color="primary"
      endIcon={<Check />}
      onClick={handleSave}
      disabled={!hasChanges}
    >
      <FormattedMessage id='buttons.save' />
    </Button>
  );
}

const GlobalListsDialog: React.FC<{ open: boolean, onClose: () => void }> = ({ open, onClose }) => {
  const { savingState, createValueSet, addValueSetEntry, setGlobalValueSetName, updateItem, deleteGlobalValueSet } = useSave();
  const { form } = useComposer();
  const { editor, setActiveList, setActivePage, setHighlightedItem } = useEditor();
  const docsUrl = useDocs('lists');
  const dialogOpen = open || editor.activeList !== undefined;
  const formLanguages = form.metadata.languages;
  const [globalValueSets, setGlobalValueSets] = React.useState<GlobalValueSet[] | undefined>(undefined);
  const [currentValueSet, setCurrentValueSet] = React.useState<ValueSet | undefined>(undefined);
  const [uploadDialogOpen, setUploadDialogOpen] = React.useState(false);
  const [anchorEl, setAnchorEl] = React.useState<HTMLElement | null>(null);
  const [name, setName] = React.useState<string | undefined>(undefined);
  const users = currentValueSet && Object.values(form.data).filter(i => i.valueSetId === currentValueSet?.id);
  const itemErrors = editor.errors?.filter(e => e.itemId === currentValueSet?.id);

  const getMappedGvs = () => {
    const gvs = savingState.composerMetadata?.globalValueSets;
    const valueSets = savingState.valueSets;
    return gvs?.map(gvs => {
      // eslint-disable-next-line @typescript-eslint/no-non-null-asserted-optional-chain
      const found = valueSets?.find(vs => vs.id === gvs.valueSetId)!;
      return { ...found, label: gvs.label }
    });
  }

  React.useEffect(() => {
    const activeList = savingState.valueSets?.find(vs => vs.id === editor.activeList);
    if (activeList) {
      setCurrentValueSet(activeList);
    }
  }, [editor.activeList, savingState.valueSets]);

  React.useEffect(() => {
    if (dialogOpen) {
      const mappedGvs = getMappedGvs();
      setGlobalValueSets(mappedGvs);
      if (!currentValueSet) {
        setCurrentValueSet(mappedGvs?.[0]);
      }
      setName(mappedGvs?.find(gvs => gvs.id === (currentValueSet ?? mappedGvs?.[0]).id)?.label || '');
    }
  }, [savingState, dialogOpen, currentValueSet]);

  React.useEffect(() => {
    if (currentValueSet && name && name !== '') {
      const mappedGvs = getMappedGvs();
      const gvsName = mappedGvs?.find(gvs => gvs.id === currentValueSet?.id)?.label;
      if (gvsName !== name) {
        setGlobalValueSetName(currentValueSet.id, name);
      }
    }
  }, [name])

  const handleClose = () => {
    setActiveList(undefined);
    setCurrentValueSet(undefined);
    onClose();
  }

  const addEntry = () => {
    if (currentValueSet) {
      if (!currentValueSet.entries) {
        const newEntry = {
          id: 'choice1',
          label: {}
        }
        addValueSetEntry(currentValueSet.id, newEntry);
        setCurrentValueSet({ ...currentValueSet, entries: [newEntry] });
      } else {
        const newEntry = {
          id: 'choice' + (currentValueSet.entries?.length + 1),
          label: {},
        };
        addValueSetEntry(currentValueSet.id, newEntry);
        setCurrentValueSet({ ...currentValueSet, entries: [...currentValueSet.entries, newEntry] });
      }
    }
  }

  const addNewList = () => {
    const newGvsIndex = savingState.composerMetadata?.globalValueSets?.length ?? 0;
    const newGvsName = 'untitled' + (newGvsIndex + 1);
    const newGvsId = generateValueSetId(savingState);
    createValueSet(null);
    if (newGvsId) {
      setGlobalValueSetName(newGvsId, newGvsName);
      setCurrentValueSet({ id: newGvsId, entries: [] });
    }
  }

  const convertToLocalList = (valueSet: ValueSet, item: DialobItem) => {
    const newId = generateValueSetId(savingState);
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
      <UploadValuesetDialog open={uploadDialogOpen} onClose={() => setUploadDialogOpen(false)}
        currentValueSet={currentValueSet} setCurrentValueSet={setCurrentValueSet} />
      <Dialog open={dialogOpen} onClose={handleClose} fullWidth maxWidth='xl' PaperProps={{ sx: { maxHeight: '60vh' } }}>
        <DialogTitle sx={{ display: 'flex', alignItems: 'center', fontWeight: 'bold' }}>
          <FormattedMessage id='dialogs.lists.global.title' />
          <Box flexGrow={1} />
          {globalValueSets && globalValueSets.length > 0 && <>
            <Typography sx={{ mr: 2 }}><BoldedMessage id='dialogs.lists.global.users' values={{ count: users ? users.length : 0 }} /></Typography>
            {users &&
              <>
                <Button onClick={(e) => setAnchorEl(e.currentTarget)} endIcon={<Visibility />}>
                  <FormattedMessage id='dialogs.lists.global.users.show' />
                </Button>
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
          <Button variant='outlined' sx={{ ml: 2 }} endIcon={<Help />}
            onClick={() => window.open(docsUrl, "_blank")}>
            <FormattedMessage id='buttons.help' />
          </Button>
          <Button onClick={addNewList} endIcon={<Add />} sx={{ ml: 2 }}><FormattedMessage id='dialogs.lists.global.add' /></Button>
        </DialogTitle>
        <DialogContent sx={{ borderTop: 1, borderBottom: 1, borderColor: 'divider', p: 0 }}>
          <Box sx={{ display: 'flex', height: '70vh', p: 3 }}>
            {globalValueSets && globalValueSets.length > 0 && <>
              <Stack sx={{ mr: 3 }}>
                {globalValueSets?.map(gvs => {
                  const errorColor = getItemErrorColor(editor.errors, gvs.id);
                  return <Button key={gvs.id} variant={gvs.id === currentValueSet?.id ? 'contained' : 'outlined'} color={errorColor} sx={{ textTransform: 'none' }}
                    onClick={() => setCurrentValueSet({ id: gvs.id, entries: gvs.entries })}>
                    {gvs.label}
                  </Button>
                })}
              </Stack>
              <Box sx={{ width: 1 }}>
                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', mb: 2 }}>
                  <TextField value={name || ''} onChange={(e) => setName(e.target.value)} sx={{ width: '70%' }} />
                  <Button endIcon={<Delete />} color='error' onClick={deleteList}><FormattedMessage id='dialogs.lists.global.delete' /></Button>
                </Box>
                <TableContainer>
                  <BorderedTable>
                    <TableHead>
                      <TableRow>
                        <TableCell width='15%' align='center'>
                          <IconButton sx={{ p: 0.25 }} onClick={addEntry}><Add color='success' /></IconButton>
                          <IconButton sx={{ p: 0.25 }} onClick={() => setUploadDialogOpen(true)}><Upload /></IconButton>
                          <IconButton sx={{ p: 0.25 }} onClick={() => downloadValueSet(currentValueSet)}><Download /></IconButton>
                        </TableCell>
                        <TableCell width='20%' sx={{ p: 0.5 }}><Typography fontWeight='bold'><FormattedMessage id='dialogs.options.key' /></Typography></TableCell>
                        {formLanguages?.map(lang => (
                          <TableCell key={lang} width={formLanguages ? `${65 / formLanguages.length}%` : 0} sx={{ p: 0.5 }}>
                            <Typography fontWeight='bold'>
                              <FormattedMessage id='dialogs.options.text' values={{ language: lang }} />
                            </Typography>
                          </TableCell>
                        ))}
                      </TableRow>
                    </TableHead>
                    <ChoiceList valueSet={currentValueSet} updateValueSet={setCurrentValueSet} isGlobal={true} />
                  </BorderedTable>
                </TableContainer>
                {itemErrors?.map((error, index) => <Alert key={index} severity={getErrorSeverity(error)} sx={{ mt: 2 }} icon={<Warning />}>
                  <Typography color={error.level.toLowerCase()}><ErrorMessage error={error} /></Typography>
                </Alert>)}
              </Box>
            </>}
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose} endIcon={<Close />}><FormattedMessage id='buttons.close' /></Button>
          <SaveButton />
        </DialogActions>
      </Dialog>
    </>
  );
}

export default GlobalListsDialog;
