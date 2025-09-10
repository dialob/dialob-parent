import React from "react";
import {
  Dialog, DialogTitle, DialogContent, Button, Typography, Select, MenuItem,
  Alert, DialogActions, Checkbox, Box, TextField, Tooltip, IconButton, Chip,
  List, ListItem, styled
} from "@mui/material";
import { Add, Check, Close, ContentCopy, Help } from "@mui/icons-material";
import { useComposer } from "../dialob";
import { FormattedMessage } from "react-intl";
import { version } from "../../package.json";
import { useBackend } from "../backend/useBackend";
import { useDocs } from "../utils/DocsUtils";
import { VisibilityType } from "../types";
import { isContextVariable } from "../utils/ItemUtils";
import { useSave } from "./contexts/saving/useSave";
import { SavingProvider } from "./contexts/saving/SavingProvider";

const visibilityModeOptions = [
  { value: 'ONLY_ENABLED' as VisibilityType, label: 'dialogs.form.options.visibility.ONLY_ENABLED' },
  { value: 'SHOW_DISABLED' as VisibilityType, label: 'dialogs.form.options.visibility.SHOW_DISABLED' },
  { value: 'ALL' as VisibilityType, label: 'dialogs.form.options.visibility.ALL' }
];

const StyledListItem = styled(ListItem)(({ theme }) => ({
  padding: 0,
}));

const CopyToClipboardButton: React.FC<{ text: string }> = ({ text }) => {
  return (
    <Tooltip title={<FormattedMessage id='buttons.copy.clip' />}>
      <IconButton size='small' onClick={() => navigator.clipboard.writeText(text)}>
        <ContentCopy fontSize='small' />
      </IconButton>
    </Tooltip>
  );
}

const SaveButton: React.FC = () => {
  const { form, applyFormChanges } = useComposer();
  const { savingState } = useSave();

  const hasChanges = React.useMemo(() => {
      return savingState.formMetadata && (JSON.stringify(savingState.formMetadata) !== JSON.stringify(form.metadata));
  }, [savingState, form.metadata]);

  const handleSave = () => {
    if (savingState.formMetadata) {
      applyFormChanges(savingState);
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

const FormName: React.FC = () => {
  const { savingState, setMetadataValue } = useSave();

  return (
    <>
      <Typography fontWeight='bold'><FormattedMessage id='dialogs.form.options.name' /></Typography>
      <TextField value={savingState.formMetadata?.label || ''} onChange={(e) => setMetadataValue('label', e.target.value)} fullWidth />
    </>
  )
}

const FormLabels: React.FC = () => {
  const { savingState, setMetadataValue } = useSave();
  const formLabels = savingState.formMetadata?.labels || [];
  const [label, setLabel] = React.useState<string>('');

  const handleAdd = () => {
    if (label && label.length > 0 && !formLabels.includes(label)) {
      setMetadataValue('labels', [...formLabels, label]);
      setLabel('');
    }
  };

  const handleDelete = (label: string) => {
    const filteredLabels = formLabels.filter(l => l !== label);
    setMetadataValue('labels', filteredLabels);
  };

  return (
    <>
      <Typography fontWeight='bold' sx={{ mt: 2 }}><FormattedMessage id='dialogs.form.options.labels' /></Typography>
      {formLabels.length > 0 && <Box sx={{ mb: 1 }}>
        {formLabels.map((label, index) => (
          <Chip key={index} sx={{ mr: 1 }} label={label} onDelete={() => handleDelete(label)} />
        ))}
      </Box>}
      <TextField
        value={label}
        label={<FormattedMessage id='dialogs.form.options.labels.add' />}
        onChange={(e) => setLabel(e.target.value)} fullWidth
        InputProps={{
          endAdornment: <IconButton onClick={handleAdd}><Add /></IconButton>
        }} />
    </>
  )
}

const FormVisibility: React.FC = () => {
  const { savingState, setMetadataValue } = useSave();
  const visibilityMode = savingState.formMetadata?.questionClientVisibility || (savingState.formMetadata?.showDisabled ? 'SHOW_DISABLED' : 'ONLY_ENABLED');

  return (
    <>
      <Typography sx={{ mt: 2 }} fontWeight='bold'><FormattedMessage id='dialogs.form.options.visibility' /></Typography>
      <Select value={visibilityMode || ''} onChange={(e) => setMetadataValue('questionClientVisibility', e.target.value as VisibilityType)} fullWidth>
        {visibilityModeOptions.map(option => <MenuItem key={option.value} value={option.value}>
          <FormattedMessage id={option.label} />
        </MenuItem>)}
      </Select>
      {visibilityMode !== undefined && <Alert severity="info" variant="outlined" sx={{ mt: 2 }}>
        <Typography>
          <FormattedMessage id={`dialogs.form.options.visibility.${visibilityMode}.desc`} />
        </Typography>
      </Alert>}
    </>
  )
}

const FormRequired: React.FC = () => {
  const { savingState, setMetadataValue } = useSave();
  const required = savingState.formMetadata?.answersRequiredByDefault || false;

  return (
    <>
      <Box sx={{ mt: 2, display: 'flex', alignItems: 'center' }}>
        <Checkbox checked={required} onChange={(e) => setMetadataValue('answersRequiredByDefault', e.target.checked)} />
        <Typography fontWeight='bold'><FormattedMessage id='dialogs.form.options.required' /></Typography>
      </Box>
      <Alert severity="info" variant="outlined" sx={{ mt: 2 }}>
        <Typography>
          <FormattedMessage id='dialogs.form.options.required.desc' values={{ value: required ? 'false' : 'true' }} />
        </Typography>
      </Alert>
    </>
  )
}

const FormStatistics: React.FC = () => {
  const { form } = useComposer();
  const items = form?.data || {};
  const valueSets = form?.valueSets || [];
  const variables = form?.variables || [];
  const questionTypesCount: Record<string, number> = {};
  let numNotes = 0;

  Object.values(items).forEach(item => {
    const type = item.type;
    const inputTypes = ['text', 'number', 'decimal', 'boolean', 'time', 'date', 'list', 'multichoice'];
    if (type === 'note') {
      numNotes++;
    } else if (inputTypes.includes(type)) {
      questionTypesCount[type] = (questionTypesCount[type] || 0) + 1;
    }
  });

  const numLists = valueSets.length;
  const numContextVars = variables.filter((v) => isContextVariable(v)).length;
  const numExprVars = variables.length - numContextVars;
  const numQuestions = Object.values(questionTypesCount).reduce((sum, count) => sum + count, 0);

  return (
    <Box sx={{ mt: 2 }}>
      <Typography fontWeight='bold'>
        <FormattedMessage id='dialogs.form.options.statistics' />
      </Typography>

      <List>
        <StyledListItem>
          <Typography>
            <FormattedMessage id="dialogs.form.options.statistics.notes" values={{ count: numNotes }} />
          </Typography>
        </StyledListItem>
        <StyledListItem>
          <Typography>
            <FormattedMessage id="dialogs.form.options.statistics.uniqueLists" values={{ count: numLists }} />
          </Typography>
        </StyledListItem>
        <StyledListItem>
          <Typography>
            <FormattedMessage id="dialogs.form.options.statistics.expressionVariables" values={{ count: numExprVars }} />
          </Typography>
        </StyledListItem>
        <StyledListItem>
          <Typography>
            <FormattedMessage id="dialogs.form.options.statistics.contextVariables" values={{ count: numContextVars }} />
          </Typography>
        </StyledListItem>
        <StyledListItem>
          <Typography>
            <FormattedMessage id="dialogs.form.options.statistics.questions" values={{ count: numQuestions }} />
          </Typography>
        </StyledListItem>
        {Object.entries(questionTypesCount).map(([type, count]) => (
          <ListItem key={type} sx={{ pl: 2 }}>
            <Typography>
              <FormattedMessage id="dialogs.form.options.statistics.typeCount" values={{ type: type.charAt(0).toUpperCase() + type.slice(1).toLowerCase(), count }} />
            </Typography>
          </ListItem>
        ))}
      </List>
    </Box>
  );
};

const FormOptionsDialog: React.FC<{ open: boolean, onClose: () => void }> = ({ open, onClose }) => {
  const { form } = useComposer();
  const { savingState } = useSave();
  const { config } = useBackend();
  const docsUrl = useDocs('options');

  if (!open) {
    return null;
  }

  return (
    <SavingProvider savingState={{ formMetadata: form.metadata }}>
      <Dialog open={true} onClose={onClose} fullWidth maxWidth='md' PaperProps={{ sx: { maxHeight: '60vh' } }}>
        <DialogTitle sx={{ fontWeight: 'bold', display: 'flex', justifyContent: 'space-between' }}>
          <FormattedMessage id='dialogs.form.options.title' />
          <Button variant='outlined' endIcon={<Help />}
            onClick={() => window.open(docsUrl, "_blank")}>
            <FormattedMessage id='buttons.help' />
          </Button>
        </DialogTitle>
        <DialogContent sx={{ display: 'flex', borderTop: 1, borderBottom: 1, borderColor: 'divider', p: 0, height: '70vh' }}>
          <Box sx={{ width: '50%', p: 3 }}>
            <FormName />
            <FormLabels />
            <FormVisibility />
            <FormRequired />
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
              <CopyToClipboardButton text={form._id + ''} />
            </Box>
            <Typography sx={{ mt: 2 }} fontWeight='bold'><FormattedMessage id='dialogs.form.options.created' /></Typography>
            <Typography>{savingState.formMetadata?.created && new Date(savingState.formMetadata?.created).toLocaleString('en-GB')}</Typography>
            <Typography sx={{ mt: 2 }} fontWeight='bold'><FormattedMessage id='dialogs.form.options.saved' /></Typography>
            <Typography>{savingState.formMetadata?.lastSaved && new Date(savingState.formMetadata?.lastSaved).toLocaleString('en-GB')}</Typography>
            <Typography sx={{ mt: 2 }} fontWeight='bold'><FormattedMessage id='dialogs.form.options.version.composer' /></Typography>
            <Typography>{version}</Typography>
            <Typography sx={{ mt: 2 }} fontWeight='bold'><FormattedMessage id='dialogs.form.options.version.backend' /></Typography>
            <Typography>{config.backendVersion}</Typography>
            <FormStatistics />
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={onClose} endIcon={<Close />}><FormattedMessage id='buttons.close' /></Button>
          <SaveButton />
        </DialogActions>
      </Dialog>
    </SavingProvider>
  )
}

export default FormOptionsDialog;
