import React from 'react';
import { FormattedMessage } from 'react-intl';
import { Add, Download, Edit, KeyboardArrowDown, Refresh, Upload, Warning } from '@mui/icons-material';
import {
  Alert, Box, Button, Divider, IconButton, List, ListItemButton, MenuItem,
  Popover, Select, TableCell, TableContainer, TableHead, TableRow, Typography
} from '@mui/material';
import { useEditor } from '../../editor';
import { ValueSet, useComposer } from '../../dialob';
import { generateValueSetId } from '../../dialob/reducer';
import { BorderedTable } from '../TableEditorComponents';
import ChoiceList from '../ChoiceList';
import ConvertConfirmationDialog from '../../dialogs/ConvertConfirmationDialog';
import UploadValuesetDialog from '../../dialogs/UploadValuesetDialog';
import GlobalList from '../GlobalList';
import { downloadValueSet } from '../../utils/ParseUtils';
import { ErrorMessage } from '../ErrorComponents';
import { getErrorSeverity } from '../../utils/ErrorUtils';
import { scrollToChoiceItem } from '../../utils/ScrollUtils';


const ChoiceEditor: React.FC = () => {
  const { form, createValueSet, addValueSetEntry, setGlobalValueSetName, updateItem, deleteLocalValueSet } = useComposer();
  const { editor, setActiveItem, setItemOptionsActiveTab, setActiveList } = useEditor();
  const item = editor.activeItem;
  const globalValueSets = form.metadata.composer?.globalValueSets;
  const formLanguages = form.metadata.languages;
  const itemErrors = editor.errors?.filter(e => e.itemId === item?.valueSetId);
  const [choiceType, setChoiceType] = React.useState<'global' | 'local' | undefined>(undefined);
  const [currentValueSet, setCurrentValueSet] = React.useState<ValueSet | undefined>(undefined);
  const [dialogType, setDialogType] = React.useState<'global' | 'local' | undefined>(undefined);
  const [uploadDialogOpen, setUploadDialogOpen] = React.useState(false);
  const [anchorEl, setAnchorEl] = React.useState<HTMLElement | null>(null);

  React.useEffect(() => {
    const hasValueSet = item?.valueSetId !== undefined;
    if (!hasValueSet) {
      setChoiceType('global');
      return;
    }
    const itemValueSet = form.valueSets?.find(v => v.id === item.valueSetId);
    const isGlobal = globalValueSets !== undefined && itemValueSet !== undefined && globalValueSets.some(gvs => gvs.valueSetId === itemValueSet.id);
    setCurrentValueSet(itemValueSet);
    setChoiceType(isGlobal ? 'global' : 'local');
  }, [item?.valueSetId, globalValueSets, form.valueSets]);

  const handleAddValueSetEntry = () => {
    if (currentValueSet) {
      if (!currentValueSet.entries) {
        const newEntry = {
          id: 'choice1',
          label: {}
        }
        addValueSetEntry(currentValueSet.id, newEntry);
        setCurrentValueSet({ ...currentValueSet, entries: [newEntry] });
        scrollToChoiceItem();
      } else {
        const newEntry = {
          id: 'choice' + (currentValueSet.entries?.length + 1),
          label: {},
        };
        addValueSetEntry(currentValueSet.id, newEntry);
        setCurrentValueSet({ ...currentValueSet, entries: [...currentValueSet.entries, newEntry] });
        scrollToChoiceItem();
      }
    }
  }

  const convertToLocalList = () => {
    if (currentValueSet && item) {
      const newId = generateValueSetId(form);
      createValueSet(item?.id, currentValueSet.entries);
      updateItem(item?.id, 'valueSetId', newId);
      setActiveItem({ ...item, valueSetId: newId });
    }
  }

  const convertToGlobalList = () => {
    if (currentValueSet && item) {
      const newGvsIndex = form.metadata.composer?.globalValueSets?.length ?? 0;
      const newGvsName = 'untitled' + (newGvsIndex + 1);
      const newGvsId = generateValueSetId(form);
      // remove rules when converting to global list
      const newEntries = currentValueSet.entries?.map(entry => { return { id: entry.id, label: entry.label } });
      createValueSet(null, newEntries);
      deleteLocalValueSet(currentValueSet.id);
      if (newGvsId) {
        setGlobalValueSetName(newGvsId, newGvsName);
        updateItem(item?.id, 'valueSetId', newGvsId);
        setActiveItem({ ...item, valueSetId: newGvsId });
      }
    }
  }

  const createLocalList = () => {
    if (item) {
      const newValueSetId = generateValueSetId(form);
      createValueSet(item.id);
      if (newValueSetId) {
        updateItem(item.id, 'valueSetId', newValueSetId);
        setActiveItem({ ...item, valueSetId: newValueSetId });
      }
    }
  }

  const selectGlobalValueSet = (id: string) => {
    if (item) {
      updateItem(item.id, 'valueSetId', id);
      setActiveItem({ ...item, valueSetId: id });
    }
  }

  const editGlobalList = (listId?: string) => {
    setActiveItem(undefined);
    setItemOptionsActiveTab(undefined);
    if (listId) {
      setActiveList(listId);
    } else if (globalValueSets && globalValueSets.length > 0) {
      setActiveList(globalValueSets[0].valueSetId);
    } else {
      setActiveList('list');
    }
  }

  if (!item) {
    return null;
  }

  return (
    <>
      <UploadValuesetDialog open={uploadDialogOpen} onClose={() => setUploadDialogOpen(false)}
        currentValueSet={currentValueSet} setCurrentValueSet={setCurrentValueSet} />
      <ConvertConfirmationDialog
        type={dialogType}
        onClick={dialogType === 'global' ? convertToGlobalList : convertToLocalList}
        onClose={() => setDialogType(undefined)} />
      {choiceType === 'local' ? <>
        <Box sx={{ display: 'flex', flexDirection: 'row', justifyContent: 'flex-end', mb: 2 }}>
          <Button onClick={(e) => setAnchorEl(e.currentTarget)} sx={{ mt: 2, mr: 2 }} endIcon={<KeyboardArrowDown />}>
            <FormattedMessage id='dialogs.options.choices.select.global' />
          </Button>
          <Popover open={Boolean(anchorEl)} anchorEl={anchorEl} onClose={() => setAnchorEl(null)} anchorOrigin={{
            vertical: 'bottom',
            horizontal: 'left',
          }}>
            <List>
              {globalValueSets && globalValueSets.length > 0 && <>
                {globalValueSets.map(gvs => (
                  <ListItemButton key={gvs.valueSetId}
                    sx={{ justifyContent: 'flex-start', color: 'text.primary' }}
                    onClick={() => {
                      selectGlobalValueSet(gvs.valueSetId);
                      setAnchorEl(null);
                    }}
                  >
                    {gvs.label}
                  </ListItemButton>
                ))}
                <Divider />
              </>}
              <ListItemButton onClick={() => {
                editGlobalList();
                setAnchorEl(null);
              }}>
                <Add fontSize='small' color='success' />
                <Typography><FormattedMessage id='dialogs.options.choices.create.global' /></Typography>
              </ListItemButton>
            </List>
          </Popover>
          <Button onClick={() => setDialogType('global')} sx={{ mt: 2 }} endIcon={<Refresh />}>
            <FormattedMessage id='dialogs.options.choices.convert.global' />
          </Button>
        </Box>
        <TableContainer>
          <BorderedTable>
            <TableHead>
              <TableRow>
                <TableCell width='20%' align='center'>
                  <IconButton onClick={handleAddValueSetEntry}><Add color='success' /></IconButton>
                  <IconButton onClick={() => setUploadDialogOpen(true)}><Upload /></IconButton>
                  <IconButton onClick={() => downloadValueSet(currentValueSet)}><Download /></IconButton>
                </TableCell>
                <TableCell width='30%' sx={{ p: 1 }}>
                  <Typography fontWeight='bold'><FormattedMessage id='dialogs.options.key' /></Typography>
                </TableCell>
                {formLanguages?.map(lang => (
                  <TableCell key={lang} width={formLanguages ? `${50 / formLanguages.length}%` : 0} sx={{ p: 1 }}>
                    <Typography fontWeight='bold'>
                      <FormattedMessage id='dialogs.options.text' values={{ language: lang }} />
                    </Typography>
                  </TableCell>
                ))}
              </TableRow>
            </TableHead>
            <ChoiceList valueSet={currentValueSet} updateValueSet={setCurrentValueSet} />
          </BorderedTable>
        </TableContainer>
      </> : <Box>
        <Typography><FormattedMessage id='dialogs.options.choices.select.global' /></Typography>
        <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
          <Select sx={{ width: 0.7 }} value={currentValueSet?.id || ''} onChange={e => selectGlobalValueSet(e.target.value as string)}>
            {form.metadata.composer?.globalValueSets?.map(v => <MenuItem key={v.valueSetId} value={v.valueSetId}>{v.label}</MenuItem>)}
          </Select>
          <Box flexGrow={1} />
          {currentValueSet && <Button onClick={() => editGlobalList(currentValueSet.id)} sx={{ mr: 2 }} endIcon={<Edit />}>
            <FormattedMessage id='dialogs.options.choices.edit.global' />
          </Button>}
          {currentValueSet && <Button onClick={() => setDialogType('local')} endIcon={<Refresh />}>
            <FormattedMessage id='dialogs.options.choices.convert.local' />
          </Button>}
        </Box>
        <GlobalList entries={currentValueSet?.entries} />
        <Divider sx={{ my: 2 }}><Typography><FormattedMessage id='dialogs.options.choices.divider' /></Typography></Divider>
        <Button onClick={createLocalList} endIcon={<Add />}>
          <FormattedMessage id='dialogs.options.choices.create.local' />
        </Button>
      </Box>
      }
      {itemErrors?.map((error, index) => <Alert severity={getErrorSeverity(error)} sx={{ mt: 2 }} icon={<Warning />}>
        <Typography key={index} color={error.level.toLowerCase()}><ErrorMessage error={error} /></Typography>
      </Alert>)}
    </>
  );
}

export { ChoiceEditor };
