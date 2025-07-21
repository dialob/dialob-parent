import React from 'react';
import { Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, Tab, Tabs } from '@mui/material';
import { Check, Close, Help } from '@mui/icons-material';
import { FormattedMessage } from 'react-intl';
import ExpressionVariables from '../components/variables/ExpressionVariables';
import ContextVariables from '../components/variables/ContextVariables';
import { VariableTabType, useEditor } from '../editor';
import { useDocs } from '../utils/DocsUtils';
import { SavingProvider } from './contexts/saving/SavingProvider';
import { useComposer } from '../dialob';
import { useSave } from './contexts/saving/useSave';


const SaveButton: React.FC = () => {
  const { form, applyVariableChanges } = useComposer();
  const { savingState } = useSave();

  const hasChanges = React.useMemo(() => {
      return savingState.variables && (JSON.stringify(savingState.variables) !== JSON.stringify(form.variables));
  }, [savingState, form.variables]);

  const handleSave = () => {
    if (savingState.variables) {
      applyVariableChanges(savingState);
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

const VariablesDialog: React.FC<{ open: boolean, onClose: () => void }> = ({ open, onClose }) => {
  const { form } = useComposer();
  const { editor, setActiveVariableTab } = useEditor();
  const docsUrl = useDocs('variables');
  const dialogOpen = open || editor.activeVariableTab !== undefined;
  const [activeTab, setActiveTab] = React.useState<VariableTabType>(editor.activeVariableTab || 'context');

  React.useEffect(() => {
    const type = editor.activeVariableTab || 'context';
    setActiveTab(type);
  }, [editor.activeVariableTab]);

  const handleClose = () => {
    onClose();
    setActiveVariableTab(undefined);
  }

  if (!dialogOpen) {
    return null;
  }

  return (
    <SavingProvider savingState={{ variables: form.variables }}>
      <Dialog open={dialogOpen} onClose={onClose} fullWidth maxWidth='lg' PaperProps={{ sx: { maxHeight: '60vh' } }}>
        <DialogTitle sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', fontWeight: 'bold' }}>
          <FormattedMessage id='dialogs.variables.title' />
          <Button variant='outlined' endIcon={<Help />}
            onClick={() => window.open(docsUrl, "_blank")}>
            <FormattedMessage id='buttons.help' />
          </Button>
        </DialogTitle>
        <DialogContent sx={{ height: '70vh', borderTop: 1, borderBottom: 1, borderColor: 'divider', p: 0 }}>
          <Tabs value={activeTab} onChange={(e, v) => setActiveTab(v)} sx={{ borderBottom: 1, borderColor: 'divider' }}>
            <Tab value='context' label={<FormattedMessage id='dialogs.variables.context.title' />} />
            <Tab value='expression' label={<FormattedMessage id='dialogs.variables.expression.title' />} />
          </Tabs>
          <Box sx={{ p: 3, width: 1 }}>
            {activeTab === 'context' && <ContextVariables onClose={onClose} />}
            {activeTab === 'expression' && <ExpressionVariables onClose={onClose} />}
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose} endIcon={<Close />}><FormattedMessage id='buttons.close' /></Button>
          <SaveButton />
        </DialogActions>
      </Dialog>
    </SavingProvider>
  )
}

export default VariablesDialog;
