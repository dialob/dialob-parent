import React from "react";
import Papa from "papaparse";
import { Dialog, DialogTitle, DialogContent, Button, CircularProgress, Typography, Select, MenuItem, Alert } from "@mui/material";
import { Upload } from "@mui/icons-material";
import { DialogActionButtons } from "./DialogComponents";
import { LocalizedString, ValueSet, ValueSetEntry, useComposer } from "../dialob";

type UploadMode = 'replace' | 'append' | 'update';

const UploadValuesetDialog: React.FC<{
  open: boolean,
  onClose: () => void,
  currentValueSet: ValueSet | undefined,
  setCurrentValueSet: React.Dispatch<React.SetStateAction<ValueSet | undefined>>
}> = ({ open, onClose, currentValueSet, setCurrentValueSet }) => {
  const { addValueSetEntry, setValueSetEntries } = useComposer();
  const [error, setError] = React.useState<string | null>(null);
  const [selectedFile, setSelectedFile] = React.useState<File | null>(null);
  const [uploadMode, setUploadMode] = React.useState<UploadMode>('replace');
  const [loading, setLoading] = React.useState<boolean>(false);
  const ref = React.useRef<HTMLInputElement>(null);

  if (!currentValueSet) {
    return null;
  }

  const onFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setError(null);
    setSelectedFile(e.target.files ? e.target.files[0] : null);
  };

  const handleClose = () => {
    setSelectedFile(null);
    setError(null);
    setLoading(false);
    setUploadMode('replace');
    onClose();
  }

  const parse = (inputFile: File): Promise<Papa.ParseResult<any>> => {
    return new Promise((resolve, reject) => {
      Papa.parse(inputFile, {
        header: true,
        transformHeader: h => h.trim(),
        skipEmptyLines: true,
        error: (error) => {
          console.error('CSV Parse error', error);
          reject(error);
        },
        complete: (results: Papa.ParseResult<any>) => {
          resolve(results);
        }
      });
    });
  };

  const handleUpload = async () => {
    if (!selectedFile) {
      setError('No file selected');
      return;
    }
    setLoading(true);
    setError(null);

    const csvResult = await parse(selectedFile);
    if (!csvResult.meta.fields) {
      setError('Invalid CSV format');
      setLoading(false);
      return;
    }

    // Data validation
    if (
      csvResult.meta.fields.length < 2 ||
      csvResult.meta.fields[0] !== 'ID' ||
      csvResult.meta.fields.filter(f => f.length !== 2).length !== 0 ||
      csvResult.data.length === 0
    ) {
      setError('Invalid CSV format');
    } else {
      const newEntries: ValueSetEntry[] = csvResult.data.map(d => {
        let label: LocalizedString = {};
        csvResult.meta.fields?.filter(f => f !== 'ID').forEach(f => {
          label[f] = d[f];
        });

        return ({
          id: d.ID.trim(),
          label
        });
      });

      // Apply
      if (uploadMode === 'replace') {
        setCurrentValueSet({ ...currentValueSet, entries: newEntries });
        setValueSetEntries(currentValueSet.id, newEntries);
      } else if (uploadMode === 'append') {
        setCurrentValueSet({ ...currentValueSet, entries: [...currentValueSet.entries, ...newEntries] });
        newEntries.forEach(e => addValueSetEntry(currentValueSet.id, e));
      } else if (uploadMode === 'update') {
        const currentEntries = currentValueSet.entries;
        const appliedEntries: ValueSetEntry[] = [];
        currentEntries.forEach(e => {
          const newEntry = newEntries.find(ne => ne.id === e.id);
          if (newEntry) {
            appliedEntries.push(newEntry);
          } else {
            appliedEntries.push(e);
          }
        });
        console.log('appliedEntries', appliedEntries)
        setCurrentValueSet({ ...currentValueSet, entries: appliedEntries });
        setValueSetEntries(currentValueSet.id, appliedEntries);
      }
      handleClose();
    }
    setLoading(false);
  };

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth='md'>
      <DialogTitle>
        Upload Valueset
      </DialogTitle>
      <DialogContent>
        <Button variant='contained' color='inherit' startIcon={<Upload />} onClick={() => ref.current?.click()} sx={{ mb: 2 }}>
          Choose File
        </Button>
        <input type='file' hidden ref={ref} accept='text/csv' onChange={onFileChange} />
        {selectedFile && <Typography sx={{ mb: 2 }}>{selectedFile.name}</Typography>}
        <Select value={uploadMode} onChange={(e) => setUploadMode(e.target.value as UploadMode)} fullWidth>
          <MenuItem value='replace'>Replace</MenuItem>
          <MenuItem value='append'>Append</MenuItem>
          <MenuItem value='update'>Update</MenuItem>
        </Select>
        {error && <Alert severity="error" sx={{ mt: 2 }}><Typography color='error'>{error}</Typography></Alert>}
      </DialogContent>
      {loading ? <CircularProgress /> : <DialogActionButtons handleClick={handleUpload} handleClose={handleClose} />}
    </Dialog>
  )
}

export default UploadValuesetDialog;
