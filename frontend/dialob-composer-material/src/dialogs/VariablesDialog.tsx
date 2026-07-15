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
import { useBackend } from '../backend/useBackend';
import { ChangeIdResult } from '../backend/types';
import { ContextVariable, Variable } from '../types';

const SaveButton: React.FC = () => {
  const { form, applyVariableChanges, applyVariableList, setForm, setRevision } = useComposer();
  const { setErrors } = useEditor();
  const { savingState, clearPendingRenames, resetItems, resetVariables } = useSave();
  const { changeItemId } = useBackend();

  const hasChanges = React.useMemo(() => {
      const variablesChanged = savingState.variables && (JSON.stringify(savingState.variables) !== JSON.stringify(form.variables));
      const itemsChanged = savingState.items && (JSON.stringify(savingState.items) !== JSON.stringify(form.data));
      const hasRenames = savingState.pendingVariableRenames && savingState.pendingVariableRenames.length > 0;
      return variablesChanged || itemsChanged || hasRenames;
  }, [savingState, form.variables, form.data]);

  const handleSave = async () => {
    if (!savingState.variables) return;

    const renames = savingState.pendingVariableRenames ?? [];
    let currentForm = form;
    let latestRev: string | undefined;

    for (const rename of renames) {
      const response = await changeItemId(currentForm, rename.from, rename.to);
      if (response.success) {
        const result = response.result as ChangeIdResult;
        currentForm = result.form;
        latestRev = result.rev;
        setErrors(result.errors);
      } else if (response.apiError) {
        setErrors([{ level: 'FATAL', message: response.apiError.message }]);
        return;
      }
    }

    if (renames.length > 0) {
      const applyRenamesToExpression = (expression: string): string =>
        renames.reduce((expr, { from, to }) =>
          expr.replace(new RegExp(`\\b${from.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')}\\b`, 'g'), to),
          expression
        );

      // Build merged variables: user-staged properties take precedence, but for expressions
      // we must reconcile user edits with backend-applied renames:
      // - if the user edited the expression, apply rename substitutions to their staged version
      // - if the user left it untouched, use the API result (rename already applied by the backend)
      const mergedVariables: (ContextVariable | Variable)[] = (currentForm.variables ?? []).map(apiVar => {
        const stagingVar = savingState.variables!.find(sv => sv.name === apiVar.name);
        if (!stagingVar) return apiVar;

        if ('expression' in apiVar) {
          // Resolve the original name before any pending rename to look up the baseline expression.
          const originalName = renames.find(r => r.to === stagingVar.name)?.from ?? stagingVar.name;
          const originalExpression = (form.variables?.find(v => v.name === originalName) as Variable | undefined)?.expression ?? '';
          const stagedExpression = (stagingVar as Variable).expression;

          const mergedExpression = stagedExpression !== originalExpression
            ? applyRenamesToExpression(stagedExpression ?? '')
            : (apiVar as Variable).expression;

          return { ...stagingVar, expression: mergedExpression } as Variable;
        }
        return stagingVar;
      });

      setForm(currentForm);
      applyVariableList(mergedVariables);
      // Sync both baselines so hasChanges returns false after save.
      resetVariables(mergedVariables);
      resetItems(currentForm.data);
      if (latestRev) {
        setRevision(latestRev);
      }
    } else {
      applyVariableChanges(savingState);
    }

    clearPendingRenames();
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
    <SavingProvider savingState={{ variables: form.variables, items: structuredClone(form.data) }}>
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
