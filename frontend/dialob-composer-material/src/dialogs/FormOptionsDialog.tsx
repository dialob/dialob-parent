import React from "react";
import {
  Dialog, DialogTitle, DialogContent, Button, Typography, Select, MenuItem,
  Alert, DialogActions, Checkbox, Box, TextField, Tooltip, IconButton
} from "@mui/material";
import { Close, ContentCopy } from "@mui/icons-material";
import { VisibilityType, useComposer } from "../dialob";
import { FormattedMessage } from "react-intl";

const visibilityModeOptions = [
  { value: 'ONLY_ENABLED' as VisibilityType, label: 'dialogs.form.options.visibility.ONLY_ENABLED' },
  { value: 'SHOW_DISABLED' as VisibilityType, label: 'dialogs.form.options.visibility.SHOW_DISABLED' },
  { value: 'ALL' as VisibilityType, label: 'dialogs.form.options.visibility.ALL' }
];

const CopyToClipboardButton: React.FC<{ text: string }> = ({ text }) => {
  return (
    <Tooltip title={<FormattedMessage id='buttons.copy.clip' />}>
      <IconButton size='small' onClick={() => navigator.clipboard.writeText(text)}>
        <ContentCopy fontSize='small' />
      </IconButton>
    </Tooltip>
  );
}

const FormOptionsDialog: React.FC<{ open: boolean, onClose: () => void }> = ({ open, onClose }) => {
  const { form, setMetadataValue } = useComposer();
  const [label, setLabel] = React.useState<string | undefined>();
  const [visibilityMode, setVisibilityMode] = React.useState<VisibilityType | undefined>();
  const [required, setRequired] = React.useState<boolean>(false);

  React.useEffect(() => {
    const visibility = form.metadata?.questionClientVisibility ||
      (form.metadata?.showDisabled ? 'SHOW_DISABLED' : 'ONLY_ENABLED');
    setVisibilityMode(visibility);
    setRequired(form.metadata?.answersRequiredByDefault || false);
    setLabel(form.metadata?.label);
  }, [form.metadata]);

  React.useEffect(() => {
    if (label) {
      const id = setTimeout(() => {
        setMetadataValue('label', label);
      }, 1000);
      return () => clearTimeout(id);
    }
  }, [label, setMetadataValue]);

  React.useEffect(() => {
    if (visibilityMode) {
      setMetadataValue('questionClientVisibility', visibilityMode);
    }
  }, [visibilityMode, setMetadataValue]);

  React.useEffect(() => {
    setMetadataValue('answersRequiredByDefault', required);
  }, [required, setMetadataValue]);

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth='md'>
      <DialogTitle fontWeight='bold'>
        <FormattedMessage id='dialogs.form.options.title' />
      </DialogTitle>
      <DialogContent sx={{ display: 'flex', borderTop: 1, borderBottom: 1, borderColor: 'divider', p: 0, height: '70vh' }}>
        <Box sx={{ width: '50%', p: 3 }}>
          <Typography fontWeight='bold'><FormattedMessage id='dialogs.form.options.label' /></Typography>
          <TextField value={label || ''} onChange={(e) => setLabel(e.target.value)} fullWidth />
          <Typography sx={{ mt: 2 }} fontWeight='bold'><FormattedMessage id='dialogs.form.options.visibility' /></Typography>
          <Select value={visibilityMode} onChange={(e) => setVisibilityMode(e.target.value as VisibilityType)} fullWidth>
            {visibilityModeOptions.map(option => <MenuItem key={option.value} value={option.value}>
              <FormattedMessage id={option.label} />
            </MenuItem>)}
          </Select>
          <Alert severity="info" variant="outlined" sx={{ mt: 2 }}>
            <Typography>
              <FormattedMessage id={`dialogs.form.options.visibility.${visibilityMode}.desc`} />
            </Typography>
          </Alert>
          <Box sx={{ mt: 2, display: 'flex', alignItems: 'center' }}>
            <Checkbox checked={required} onChange={(e) => setRequired(e.target.checked)} />
            <Typography fontWeight='bold'><FormattedMessage id='dialogs.form.options.required' /></Typography>
          </Box>
          <Alert severity="info" variant="outlined" sx={{ mt: 2 }}>
            <Typography>
              <FormattedMessage id='dialogs.form.options.required.desc' values={{ value: required ? 'false' : 'true' }} />
            </Typography>
          </Alert>
        </Box>
        <Box sx={{ width: '50%', p: 3 }}>
          <Typography fontWeight='bold'><FormattedMessage id='dialogs.form.options.technical.name' /></Typography>
          <Box sx={{ display: 'flex', alignItems: 'center' }}>
            <Typography>{form.name}</Typography>
            <CopyToClipboardButton text={form.name} />
          </Box>
          <Typography sx={{ mt: 2 }} fontWeight='bold'><FormattedMessage id='dialogs.form.options.id' /></Typography>
          <Box sx={{ display: 'flex', alignItems: 'center' }}>
            <Typography>{form._id}</Typography>
            <CopyToClipboardButton text={form._id} />
          </Box>
          <Typography sx={{ mt: 2 }} fontWeight='bold'><FormattedMessage id='dialogs.form.options.created' /></Typography>
          <Typography>{form?.metadata.created && new Date(form?.metadata.created).toLocaleString('en-GB')}</Typography>
          <Typography sx={{ mt: 2 }} fontWeight='bold'><FormattedMessage id='dialogs.form.options.saved' /></Typography>
          <Typography>{form?.metadata.lastSaved && new Date(form?.metadata.lastSaved).toLocaleString('en-GB')}</Typography>
        </Box>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} endIcon={<Close />}><FormattedMessage id='buttons.close' /></Button>
      </DialogActions>
    </Dialog>
  )
}

export default FormOptionsDialog;
