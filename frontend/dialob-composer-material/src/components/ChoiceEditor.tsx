import { Add, Close, Download, Upload, Visibility } from '@mui/icons-material';
import {
  Box, Button, Divider, IconButton, MenuItem, Select, Table, TableBody, TableCell, TableContainer, TableHead,
  TableRow, TextField, Typography, styled
} from '@mui/material';
import React from 'react';
import { useEditor } from '../editor';
import { ValueSet, ValueSetEntry, useComposer } from '../dialob';
import ChoiceRuleEditDialog from '../dialogs/ChoiceRuleEditDialog';
import ChoiceTextEditDialog from '../dialogs/ChoiceTextEditDialog';
import { generateValueSetId } from '../dialob/reducer';
import { StyledTable, StyledTextField } from './TableEditorComponents';
import { FormattedMessage } from 'react-intl';


const MAX_CHOICE_LABEL_LENGTH = 40;

const LabelButton = styled(Button)(({ theme }) => ({
  padding: theme.spacing(1.5),
  width: '100%',
  justifyContent: 'flex-start',
  textTransform: 'none',
}));

const getLabel = (entry: ValueSetEntry, language: string) => {
  const localizedLabel = entry.label[language] ? entry.label[language] : undefined;
  if (!localizedLabel) {
    return <Typography color='text.hint'>Label</Typography>;
  }
  if (localizedLabel.length > MAX_CHOICE_LABEL_LENGTH) {
    return <Typography>{localizedLabel.substring(0, MAX_CHOICE_LABEL_LENGTH) + '...'}</Typography>;
  }
  return <Typography>{localizedLabel}</Typography>;
}

const ChoiceEditor: React.FC = () => {
  const { form, createValueSet, addValueSetEntry, moveValueSetEntry,
    deleteValueSetEntry, updateValueSetEntry, setGlobalValueSetName, updateItem } = useComposer();
  const { editor, setActiveItem } = useEditor();
  const item = editor.activeItem;
  const globalValueSets = form.metadata.composer?.globalValueSets;
  const [activeValueSetEntry, setActiveValueSetEntry] = React.useState<ValueSetEntry | undefined>(undefined);
  const [activeDialog, setActiveDialog] = React.useState<'rule' | 'text' | undefined>(undefined);
  const [choiceType, setChoiceType] = React.useState<'global' | 'local' | undefined>(undefined);
  const [currentValueSet, setCurrentValueSet] = React.useState<ValueSet | undefined>(undefined);

  React.useEffect(() => {
    const hasValueSet = item?.valueSetId !== undefined;
    if (!hasValueSet) {
      setChoiceType('global');
      return;
    }
    const itemValueSet = form.valueSets?.find(v => v.id === item.valueSetId);
    const isGlobal = globalValueSets !== undefined && itemValueSet !== undefined && globalValueSets.some(gvs => gvs.valueSetId === itemValueSet.id);
    console.log('isglobal', isGlobal, globalValueSets, form.valueSets, itemValueSet)
    setCurrentValueSet(itemValueSet);
    setChoiceType(isGlobal ? 'global' : 'local');
  }, [item?.valueSetId, globalValueSets, form.valueSets]);

  const handleEditRule = (entry: ValueSetEntry) => {
    setActiveValueSetEntry(entry);
    setActiveDialog('rule');
  }

  const handleEditText = (entry: ValueSetEntry) => {
    setActiveValueSetEntry(entry);
    setActiveDialog('text');
  }

  const handleCloseChoiceDialog = () => {
    setActiveValueSetEntry(undefined);
    setActiveDialog(undefined);
  }

  const updateValueSetEntryId = (entry: ValueSetEntry, id: string) => {
    if (currentValueSet) {
      const newEntry = { ...entry, id };
      const idx = currentValueSet.entries.findIndex(e => e.id === entry.id);
      updateValueSetEntry(currentValueSet.id, idx, newEntry);
    }
  }

  const updateValueSetEntryLabel = (entry: ValueSetEntry, label: string, language: string) => {
    if (currentValueSet) {
      const newEntry = { ...entry, label: { ...entry.label, [language]: label } };
      const idx = currentValueSet.entries.findIndex(e => e.id === entry.id);
      updateValueSetEntry(currentValueSet.id, idx, newEntry);
    }
  }

  const updateValueSetEntryRule = (entry: ValueSetEntry, rule: string) => {
    if (currentValueSet) {
      const newEntry = { ...entry, when: rule };
      const idx = currentValueSet.entries.findIndex(e => e.id === entry.id);
      updateValueSetEntry(currentValueSet.id, idx, newEntry);
    }
  }

  const handleAddValueSetEntry = () => {
    if (currentValueSet) {
      const newEntry = {
        id: 'choice' + (currentValueSet.entries.length + 1),
        label: {},
      };
      addValueSetEntry(currentValueSet.id, newEntry);
    }
  }

  const handleDeleteValueSetEntry = (entry: ValueSetEntry) => {
    if (currentValueSet) {
      const idx = currentValueSet.entries.findIndex(e => e.id !== entry.id);
      deleteValueSetEntry(currentValueSet.id, idx);
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
      createValueSet(null, currentValueSet.entries);
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

  if (!item) {
    return null;
  }


  return (
    <>
      <ChoiceRuleEditDialog open={activeDialog === 'rule'} valueSetEntry={activeValueSetEntry}
        onUpdate={updateValueSetEntryRule} onClose={handleCloseChoiceDialog} />
      <ChoiceTextEditDialog open={activeDialog === 'text'} valueSetEntry={activeValueSetEntry}
        onUpdate={updateValueSetEntryLabel} onClose={handleCloseChoiceDialog} />
      {choiceType === 'local' ? <Box>
        <TableContainer>
          <StyledTable>
            <TableHead>
              <TableRow>
                <TableCell width='20%' align='center'>
                  <IconButton onClick={handleAddValueSetEntry}><Add color='success' /></IconButton>
                  <IconButton><Upload /></IconButton>
                  <IconButton><Download /></IconButton>
                </TableCell>
                <TableCell width='40%' sx={{ p: 1 }}><Typography fontWeight='bold'><FormattedMessage id='dialogs.options.key' /></Typography></TableCell>
                <TableCell width='40%' sx={{ p: 1 }}><Typography fontWeight='bold'><FormattedMessage id='dialogs.options.text' /></Typography></TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {currentValueSet?.entries.map(entry => <TableRow key={entry.id}>
                <TableCell align='center'>
                  <IconButton onClick={() => handleDeleteValueSetEntry(entry)}><Close color='error' /></IconButton>
                  <IconButton onClick={() => handleEditRule(entry)}><Visibility color={entry.when ? 'primary' : 'inherit'} /></IconButton>
                </TableCell>
                <TableCell>
                  <StyledTextField variant='standard' InputProps={{
                    disableUnderline: true,
                  }} value={entry.id} onChange={(e) => updateValueSetEntryId(entry, e.target.value)} />
                </TableCell>
                <TableCell>
                  <LabelButton variant='text' color='inherit' onClick={() => handleEditText(entry)}>
                    {getLabel(entry, editor.activeFormLanguage)}
                  </LabelButton>
                </TableCell>
              </TableRow>)}
            </TableBody>
          </StyledTable>
        </TableContainer>
        <Button color='inherit' variant='contained' onClick={convertToGlobalList} sx={{ mt: 2 }}>
          <FormattedMessage id='dialogs.options.choices.convert.global' />
        </Button>
      </Box> : <Box>
        <Typography><FormattedMessage id='dialogs.options.choices.select.global' /></Typography>
        <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
          <Select sx={{ width: 0.75 }} value={currentValueSet?.id || ''} onChange={e => selectGlobalValueSet(e.target.value as string)}>
            {form.metadata.composer?.globalValueSets?.map(v => <MenuItem key={v.valueSetId} value={v.valueSetId}>{v.label}</MenuItem>)}
          </Select>
          <Button color='inherit' variant='contained' onClick={convertToLocalList} disabled={currentValueSet?.id === ''}>
            <FormattedMessage id='dialogs.options.choices.convert.local' />
          </Button>
        </Box>
        <Divider sx={{ my: 2 }}><Typography><FormattedMessage id='dialogs.options.choices.divider' /></Typography></Divider>
        <Button color='inherit' variant='contained' onClick={createLocalList}>
          <FormattedMessage id='dialogs.options.choices.create.local' />
        </Button>
      </Box>}
    </>
  );
}

export default ChoiceEditor;
