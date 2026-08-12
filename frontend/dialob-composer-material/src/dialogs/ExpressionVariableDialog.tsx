import React from 'react';
import { Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, IconButton, Switch, TextField, Typography, Alert, styled } from '@mui/material';
import { Check, Close, Help, Warning, Edit, Delete } from '@mui/icons-material';
import { FormattedMessage } from 'react-intl';
import { EditorError, useEditor } from '../editor';
import { useDocs } from '../utils/DocsUtils';
import { SavingProvider } from './contexts/saving/SavingProvider';
import { useComposer } from '../dialob';
import { useSave } from './contexts/saving/useSave';
import { DialobItem, Variable } from '../types';
import { isContextVariable } from '../utils/ItemUtils';
import CodeMirror from '../components/code/CodeMirror';
import { useBackend } from '../backend/useBackend';
import { validateId } from '../utils/ValidateUtils';
import { ChangeIdResult } from '../backend/types';
import { getErrorSeverity, parseVariableItemId } from '../utils/ErrorUtils';
import { ErrorMessage } from '../components/ErrorComponents';
import { MAX_VARIABLE_DESCRIPTION_LENGTH } from '../defaults';
import { ExpressionVariableBaselineContext, ExpressionVariableBaseline } from './contexts/saving/ExpressionVariableBaselineContext';
import {
  mergeAllItemsAfterRename,
  mergeVariablesAfterRename,
} from '../utils/RenameMergeUtils';

const StyledButtonContainer = styled(Box)(({ theme }) => ({
  '& .MuiButton-root': {
    border: '0.05rem solid',
    borderRadius: theme.spacing(0.5),
    marginLeft: theme.spacing(1),
  },
}));

const SaveButton: React.FC = () => {
  const { form, applyVariableChanges } = useComposer();
  const { savingState } = useSave();
  const { setActiveVariable } = useEditor();

  const hasChanges = React.useMemo(() => {
    const variablesChanged = savingState.variables && (JSON.stringify(savingState.variables) !== JSON.stringify(form.variables));
    const itemsChanged = savingState.items && (JSON.stringify(savingState.items) !== JSON.stringify(form.data));
    return variablesChanged || itemsChanged;
  }, [savingState, form.variables, form.data]);

  const handleSave = () => {
    if (savingState.variables) {
      applyVariableChanges(savingState);
      setActiveVariable(undefined);
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

const SaveIdButton: React.FC<{ 
  id: string, 
  originalId: string,
  setIdError: React.Dispatch<React.SetStateAction<boolean>>, 
  setEditMode: React.Dispatch<React.SetStateAction<boolean>> 
}> = ({ id, originalId, setIdError, setEditMode }) => {
  const { form, setForm, setRevision } = useComposer();
  const { changeItemId } = useBackend();
  const { savingState, applyIdRenameMerge } = useSave();
  const { setErrors, setActiveVariable } = useEditor();
  const baseline = React.useContext(ExpressionVariableBaselineContext);

  const handleChangeId = () => {
    if (id !== originalId && baseline) {
      if (validateId(id, form.data, form.variables)) {
        changeItemId(form, originalId, id).then((response) => {
          const result = response.result as ChangeIdResult;
          if (response.success) {
            const mergedVariables = mergeVariablesAfterRename(
              savingState.variables,
              baseline.variables,
              result.form.variables,
              originalId,
              id
            );
            const mergedItems = mergeAllItemsAfterRename(
              savingState.items,
              baseline.items,
              result.form.data,
              originalId,
              id
            );

            setForm(result.form);
            setErrors(result.errors);
            setIdError(false);
            setRevision(result.rev);
            setEditMode(false);
            setActiveVariable(id);
            applyIdRenameMerge({ mergedVariables, mergedItems });
          } else if (response.apiError) {
            setErrors([{ level: 'FATAL', message: response.apiError.message }]);
            setEditMode(false);
          }
        });
      } else {
        setIdError(true);
      }
    }
  }

  return (
    <IconButton onClick={handleChangeId}><Check color='success' /></IconButton>
  );
}


const ExpressionField: React.FC<{ variable: Variable, errors?: EditorError[] }> = ({ variable, errors }) => {
  const { updateExpressionVariable } = useSave();

  return (
    <Box>
      <Typography fontWeight='bold' sx={{ mb: 1 }}>
        <FormattedMessage id='dialogs.variables.expression' />
      </Typography>
      <CodeMirror 
        value={variable.expression ?? ''} 
        onChange={(e) => updateExpressionVariable(variable.name, e)} 
        errors={errors} 
      />
    </Box>
  );
}

const DescriptionField: React.FC<{ variable: Variable }> = ({ variable }) => {
  const { updateVariableDescription } = useSave();

  return (
    <Box>
      <Typography fontWeight='bold' sx={{ mb: 1 }}>
        <FormattedMessage id='dialogs.variables.description' />
      </Typography>
      <TextField
        value={variable.description || ''}
        onChange={(e) => updateVariableDescription(variable.name, e.target.value)}
        variant='outlined'
        inputProps={{ maxLength: MAX_VARIABLE_DESCRIPTION_LENGTH }}
        fullWidth
      />
    </Box>
  );
}

const PublishedSwitch: React.FC<{ variable: Variable }> = ({ variable }) => {
  const { updateVariablePublishing } = useSave();
  
  return (
    <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
      <Typography fontWeight='bold'>
        <FormattedMessage id='dialogs.variables.published' />
      </Typography>
      <Switch 
        checked={variable.published ?? false} 
        onChange={(e) => updateVariablePublishing(variable.name, e.target.checked)} 
      />
    </Box>
  );
}

const ExpressionVariableDialogContent: React.FC = () => {
  const { editor } = useEditor();
  const { savingState } = useSave();
  
  const variable = React.useMemo(() => {
    if (!editor.activeVariable || !savingState.variables) return undefined;
    return savingState.variables.find(v => !isContextVariable(v) && v.name === editor.activeVariable) as Variable | undefined;
  }, [editor.activeVariable, savingState.variables]);

  const itemErrors = editor.errors?.filter(e => {
    if (!e.itemId || !variable) return false;
    if (e.itemId === variable.name) return true;
    const parsedVariableName = parseVariableItemId(e.itemId);
    return parsedVariableName === variable.name;
  });

  if (!variable) {
    return null;
  }

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3, mt: 3 }}>
      <ExpressionField variable={variable} errors={itemErrors} />
      <DescriptionField variable={variable} />
      <PublishedSwitch variable={variable} />
      {itemErrors && itemErrors.length > 0 && (
        <Box>
          {itemErrors.map((error, index) => (
            <Alert key={index} severity={getErrorSeverity(error)} sx={{ mt: 1 }} icon={<Warning />}>
              <Typography color={error.level.toLowerCase()}>
                <ErrorMessage error={error} />
              </Typography>
            </Alert>
          ))}
        </Box>
      )}
    </Box>
  );
}

const ExpressionVariableDialog: React.FC = () => {
  const { form } = useComposer();
  const { editor, setActiveVariable, setConfirmationDialogType, setConfirmationActiveItem } = useEditor();
  const docsUrl = useDocs('variables');
  const dialogOpen = editor.activeVariable !== undefined;
  const [editMode, setEditMode] = React.useState(false);
  const [id, setId] = React.useState<string>(editor.activeVariable || '');
  const [idError, setIdError] = React.useState<boolean>(false);

  const baseline = React.useMemo<ExpressionVariableBaseline | null>(() => {
    if (!dialogOpen) {
      return null;
    }
    return {
      variables: structuredClone(form.variables ?? []),
      items: structuredClone(form.data),
    };
  }, [dialogOpen, editor.activeVariable]);

  React.useEffect(() => {
    setId(editor.activeVariable || '');
    setEditMode(editor.activeVariableIdEditMode ?? false);
    setIdError(false);
  }, [editor.activeVariable, editor.activeVariableIdEditMode]);

  const handleClose = () => {
    setActiveVariable(undefined);
  }

  const handleDelete = () => {
    if (editor.activeVariable) {
      const pseudoItem = { id: editor.activeVariable, type: 'variable' } as DialobItem;
      setConfirmationActiveItem(pseudoItem);
      setConfirmationDialogType('deleteVariable');
      setActiveVariable(undefined);
    }
  }

  const handleCloseChange = () => {
    setEditMode(false);
    setIdError(false);
    setId(editor.activeVariable || '');
  }

  if (!dialogOpen || !baseline) {
    return null;
  }

  return (
    <ExpressionVariableBaselineContext.Provider value={baseline}>
      <SavingProvider savingState={{ variables: form.variables, items: structuredClone(form.data) }}>
        <Dialog open={dialogOpen} onClose={handleClose} fullWidth maxWidth='md' PaperProps={{ sx: { maxHeight: '70vh' } }}>
          <DialogTitle sx={{ display: 'flex', flexDirection: 'row', alignItems: 'center' }}>
            {editMode ? <TextField value={id} autoFocus={editMode} onChange={(e) => setId(e.target.value)} error={idError}
              helperText={<FormattedMessage id='dialogs.change.id.tip' />} InputProps={{
                endAdornment: (
                  <>
                    <SaveIdButton id={id} originalId={editor.activeVariable || ''} setIdError={setIdError} setEditMode={setEditMode} />
                    <IconButton onClick={handleCloseChange}><Close color='error' /></IconButton>
                  </>
                )
              }} /> :
              <Button variant='text' sx={{ color: 'inherit', textTransform: 'none', fontWeight: 'bold', fontSize: 'h5.fontSize' }}
                endIcon={<Edit color='primary' />} onClick={() => setEditMode(true)}>
                {id}
              </Button>}
            <Box flexGrow={1} />
            <StyledButtonContainer>
              <Button variant='outlined' endIcon={<Help />}
                onClick={() => window.open(docsUrl, "_blank")}>
                <FormattedMessage id='buttons.help' />
              </Button>
              <Button color='error' endIcon={<Delete />} onClick={handleDelete}>
                <FormattedMessage id='buttons.delete' />
              </Button>
            </StyledButtonContainer>
          </DialogTitle>
          <DialogContent sx={{ borderTop: 1, borderBottom: 1, borderColor: 'divider', p: 3 }}>
            <ExpressionVariableDialogContent />
          </DialogContent>
          <DialogActions>
            <Button onClick={handleClose} endIcon={<Close />}><FormattedMessage id='buttons.close' /></Button>
            <SaveButton />
          </DialogActions>
        </Dialog>
      </SavingProvider>
    </ExpressionVariableBaselineContext.Provider>
  )
}

export default ExpressionVariableDialog;
