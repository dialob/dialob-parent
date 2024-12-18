import React from 'react';
import { FormattedMessage } from 'react-intl';
import {
  Dialog, DialogTitle, DialogContent, Button, Box, Tabs, Tab,
  DialogActions, Tooltip, styled, TextField, IconButton
} from '@mui/material';
import { Rule, Edit, EditNote, Dns, List, Visibility, Delete, Description, Label, Check, Close, Gavel, Help } from "@mui/icons-material";
import { OptionsTabType, useEditor } from '../editor';
import { DEFAULT_ITEMTYPE_CONFIG } from '../defaults';
import { ConversionMenu } from '../items/ItemComponents';
import Editors from '../components/editors';
import { useBackend } from '../backend/useBackend';
import { useComposer } from '../dialob';
import { ChangeIdResult } from '../backend/types';
import { validateId } from '../utils/ValidateUtils';
import { useDocs } from '../utils/DocsUtils';

const StyledButtonContainer = styled(Box)(({ theme }) => ({
  '& .MuiButton-root': {
    border: '0.05rem solid',
    borderRadius: theme.spacing(0.5),
    marginLeft: theme.spacing(1),
  },
}));

const ItemOptionsDialog: React.FC = () => {
  const { editor, setActiveItem, setItemOptionsActiveTab, setConfirmationDialogType, setErrors } = useEditor();
  const { form, setForm, setRevision } = useComposer();
  const { changeItemId, config } = useBackend();
  const item = editor.activeItem;
  const open = item && editor.itemOptionsActiveTab !== undefined || false;
  const canHaveChoices = item && (item.type === 'list' || item.type === 'multichoice' || item.type === 'surveygroup');
  const [activeTab, setActiveTab] = React.useState<OptionsTabType>('label');
  const [editMode, setEditMode] = React.useState(false);
  const [id, setId] = React.useState<string>(item?.id || '');
  const [idError, setIdError] = React.useState<boolean>(false);
  const resolvedConfig = config.itemTypes ?? DEFAULT_ITEMTYPE_CONFIG;
  const isInputType = item && resolvedConfig.categories.find(c => c.type === 'input')?.items.some(i => i.config.type === item.type);
  const docsUrl = useDocs(activeTab);

  React.useEffect(() => {
    if (editor.itemOptionsActiveTab) {
      if (editor.itemOptionsActiveTab === 'id') {
        setEditMode(true);
      } else {
        setActiveTab(editor.itemOptionsActiveTab);
      }
    } else {
      setEditMode(false);
      setActiveTab('label');
    }
  }, [editor.itemOptionsActiveTab, open]);

  React.useEffect(() => {
    setId(item?.id || '');
  }, [item?.id]);

  const handleClose = () => {
    setItemOptionsActiveTab(undefined);
    setActiveItem(undefined);
  }

  const handleDelete = () => {
    setItemOptionsActiveTab(undefined);
    setConfirmationDialogType('delete');
  }

  const handleChangeId = () => {
    if (item && id !== item.id) {
      if (validateId(id, form.data, form.variables)) {
        changeItemId(form, item.id, id).then((response) => {
          const result = response.result as ChangeIdResult;
          if (response.success) {
            setForm(result.form);
            setErrors(result.errors);
            setIdError(false);
            setRevision(result.rev);
            setEditMode(false);
            setActiveItem({ ...item, id: id });
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

  const handleCloseChange = () => {
    setEditMode(false);
    setIdError(false);
    setId(item?.id || '');
  }

  if (!item) {
    return null;
  }

  return (
    <Dialog open={open} onClose={handleClose} fullWidth maxWidth='md' PaperProps={{ sx: { maxHeight: '60vh' } }}>
      <DialogTitle sx={{ display: 'flex', flexDirection: 'row', alignItems: 'center' }}>
        {editMode ? <TextField value={id} autoFocus={editMode} onChange={(e) => setId(e.target.value)} error={idError}
          helperText={<FormattedMessage id='dialogs.change.id.tip' />} InputProps={{
            endAdornment: (
              <>
                <IconButton onClick={handleChangeId}><Check color='success' /></IconButton>
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
          <ConversionMenu item={item} />
          <Button color='error' endIcon={<Delete />} onClick={handleDelete}><FormattedMessage id='buttons.delete' /></Button>
        </StyledButtonContainer>
      </DialogTitle>
      <DialogContent sx={{ padding: 0, borderTop: 1, borderBottom: 1, borderColor: 'divider' }}>
        <Box sx={{ display: 'flex', height: '70vh' }}>
          <Tabs value={activeTab} onChange={(e, value) => setActiveTab(value)} orientation='vertical' sx={{ borderRight: 1, borderColor: 'divider' }}>
            <Tab icon={<Tooltip placement='right' title={<FormattedMessage id='tooltips.label' />}><Label /></Tooltip>} value='label' />
            <Tab icon={<Tooltip placement='right' title={<FormattedMessage id='tooltips.description' />}><Description /></Tooltip>} value='description' />
            <Tab icon={
              <Tooltip placement='right' title={<FormattedMessage id='tooltips.rules' />}>
                <Box sx={{ display: 'flex', alignItems: 'center', width: 1 }}>
                  <Visibility />
                  <Box flexGrow={1} />
                  <Gavel />
                </Box>
              </Tooltip>
            } value='rules' />
            {isInputType && <Tab icon={<Tooltip placement='right' title={<FormattedMessage id='tooltips.validations' />}><Rule /></Tooltip>} value='validations' />}
            {isInputType && <Tab icon={<Tooltip placement='right' title={<FormattedMessage id='tooltips.default' />}><EditNote /></Tooltip>} value='defaults' />}
            {canHaveChoices && <Tab icon={<Tooltip placement='right' title={<FormattedMessage id='tooltips.choices' />}><List /></Tooltip>} value='choices' />}
            <Tab icon={<Tooltip placement='right' title={<FormattedMessage id='tooltips.properties' />}><Dns /></Tooltip>} value='properties' />
          </Tabs>
          <Box sx={{ p: 2, width: 1 }}>
            {activeTab === 'label' && <Editors.Label />}
            {activeTab === 'description' && <Editors.Description />}
            {activeTab === 'rules' && <Editors.Rules />}
            {activeTab === 'validations' && <Editors.Validations />}
            {activeTab === 'defaults' && <Editors.Defaults />}
            {activeTab === 'choices' && <Editors.Choice />}
            {activeTab === 'properties' && <Editors.Properties />}
          </Box>
        </Box>
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose} color="primary" endIcon={<Close />}><FormattedMessage id='buttons.close' /></Button>
      </DialogActions>
    </Dialog>
  );
};

export default ItemOptionsDialog;
