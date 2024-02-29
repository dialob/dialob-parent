import React from 'react';
import { FormattedMessage } from 'react-intl';
import Papa from 'papaparse';
import FileSaver from 'file-saver';
import { Add, Download, Edit, Refresh, Upload } from '@mui/icons-material';
import { Box, Button, Divider, IconButton, MenuItem, Select, TableCell, TableContainer, TableHead, TableRow, Typography } from '@mui/material';
import { useEditor } from '../../editor';
import { ValueSet, useComposer } from '../../dialob';
import { generateValueSetId } from '../../dialob/reducer';
import { StyledTable } from '../TableEditorComponents';
import ChoiceList from '../ChoiceList';
import ConvertConfirmationDialog from '../../dialogs/ConvertConfirmationDialog';
import UploadValuesetDialog from '../../dialogs/UploadValuesetDialog';
import GlobalList from '../GlobalList';


const ChoiceEditor: React.FC = () => {
  const { form, createValueSet, addValueSetEntry, setGlobalValueSetName, updateItem } = useComposer();
  const { editor, setActiveItem } = useEditor();
  const item = editor.activeItem;
  const globalValueSets = form.metadata.composer?.globalValueSets;
  const formLanguages = form.metadata.languages;
  const [choiceType, setChoiceType] = React.useState<'global' | 'local' | undefined>(undefined);
  const [currentValueSet, setCurrentValueSet] = React.useState<ValueSet | undefined>(undefined);
  const [dialogType, setDialogType] = React.useState<'global' | 'local' | undefined>(undefined);
  const [uploadDialogOpen, setUploadDialogOpen] = React.useState(false);

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
      const newEntry = {
        id: 'choice' + (currentValueSet.entries.length + 1),
        label: {},
      };
      addValueSetEntry(currentValueSet.id, newEntry);
      setCurrentValueSet({ ...currentValueSet, entries: [...currentValueSet.entries, newEntry] });
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
      const newEntries = currentValueSet.entries.map(entry => { return { id: entry.id, label: entry.label } });
      createValueSet(null, newEntries);
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

  if (!item) {
    return null;
  }

  return (
    <>
      <UploadValuesetDialog open={uploadDialogOpen} onClose={() => setUploadDialogOpen(false)} currentValueSet={currentValueSet} setCurrentValueSet={setCurrentValueSet} />
      <ConvertConfirmationDialog
        type={dialogType}
        onClick={dialogType === 'global' ? convertToGlobalList : convertToLocalList}
        onClose={() => setDialogType(undefined)} />
      {choiceType === 'local' ? <>
        <Box sx={{ display: 'flex', flexDirection: 'row', justifyContent: 'flex-end', mb: 2 }}>
          <Button onClick={() => setDialogType('global')} sx={{ mt: 2 }} endIcon={<Refresh />}>
            <FormattedMessage id='dialogs.options.choices.convert.global' />
          </Button>
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
            <ChoiceList valueSet={currentValueSet} updateValueSet={setCurrentValueSet} />
          </StyledTable>
        </TableContainer>
      </> : <Box>
        <Typography><FormattedMessage id='dialogs.options.choices.select.global' /></Typography>
        <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
          <Select sx={{ width: 0.7 }} value={currentValueSet?.id || ''} onChange={e => selectGlobalValueSet(e.target.value as string)}>
            {form.metadata.composer?.globalValueSets?.map(v => <MenuItem key={v.valueSetId} value={v.valueSetId}>{v.label}</MenuItem>)}
          </Select>
          <Box flexGrow={1} />
          <Button onClick={() => setDialogType('local')} disabled={currentValueSet?.id === ''} sx={{ mr: 2 }} endIcon={<Edit />}>
            <FormattedMessage id='dialogs.options.choices.edit.global' />
          </Button>
          <Button onClick={() => setDialogType('local')} disabled={currentValueSet?.id === ''} endIcon={<Refresh />}>
            <FormattedMessage id='dialogs.options.choices.convert.local' />
          </Button>
        </Box>
        <GlobalList entries={currentValueSet?.entries} />
        <Divider sx={{ my: 2 }}><Typography><FormattedMessage id='dialogs.options.choices.divider' /></Typography></Divider>
        <Button onClick={createLocalList} endIcon={<Add />}>
          <FormattedMessage id='dialogs.options.choices.create.local' />
        </Button>
      </Box>}
    </>
  );
}

export default ChoiceEditor;
