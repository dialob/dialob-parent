import React from 'react';
import { FormattedMessage } from 'react-intl';
import Papa from 'papaparse';
import FileSaver from 'file-saver';
import { Add, Close, Download, Upload } from '@mui/icons-material';
import {
  Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, IconButton,
  Stack, TableCell, TableContainer, TableHead, TableRow, Typography
} from '@mui/material';
import { ValueSet, ValueSetEntry, useComposer } from '../dialob';
import { generateValueSetId } from '../dialob/reducer';
import { StyledTable } from '../components/TableEditorComponents';
import ChoiceList from '../components/ChoiceList';
import UploadValuesetDialog from './UploadValuesetDialog';

interface GlobalValueSet {
  id: string;
  label?: string;
  entries: ValueSetEntry[];
}

const GlobalListsDialog: React.FC<{ open: boolean, onClose: () => void }> = ({ open, onClose }) => {
  const { form, createValueSet, addValueSetEntry, setGlobalValueSetName } = useComposer();
  const formLanguages = form.metadata.languages;
  const [globalValueSets, setGlobalValueSets] = React.useState<GlobalValueSet[] | undefined>(undefined);
  const [currentValueSet, setCurrentValueSet] = React.useState<ValueSet | undefined>(undefined);
  const [uploadDialogOpen, setUploadDialogOpen] = React.useState(false);

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
  }, [form.metadata.composer?.globalValueSets, currentValueSet]);

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
      <Dialog open={open} onClose={onClose} fullWidth maxWidth='xl'>
        <DialogTitle sx={{ display: 'flex' }}>
          <Typography variant='h5' fontWeight='bold'>Global lists</Typography>
          <Box flexGrow={1} />
          <Button onClick={addGlobalList} endIcon={<Add />}>Add new list</Button>
        </DialogTitle>
        <DialogContent sx={{ display: 'flex', height: '70vh', borderTop: 1, borderBottom: 1, borderColor: 'divider', p: 0 }}>
          <Stack sx={{ p: 2 }}>
            {globalValueSets?.map(gvs => (
              <Button key={gvs.id} variant={gvs.id === currentValueSet?.id ? 'contained' : 'outlined'}
                onClick={() => setCurrentValueSet({ id: gvs.id, entries: gvs.entries })}>{gvs.label}</Button>
            ))}
          </Stack>
          <TableContainer sx={{ width: 1, p: 2, pl: 0 }}>
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
        </DialogContent>
        <DialogActions>
          <Button onClick={onClose} endIcon={<Close />}><FormattedMessage id='buttons.close' /></Button>
        </DialogActions>
      </Dialog>
    </>
  );
}

export default GlobalListsDialog;
