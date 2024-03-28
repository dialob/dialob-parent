import React from 'react';
import { FormattedMessage } from 'react-intl';
import {
  Dialog, DialogTitle, DialogContent, Button, Box, Typography, Tabs, Tab,
  DialogActions, Tooltip, styled, TextField, IconButton
} from '@mui/material';
import { Rule, Edit, EditNote, Dns, List, Visibility, Delete, Description, Label, Check, Close, Gavel } from "@mui/icons-material";
import { OptionsTabType, useEditor } from '../editor';
import { DEFAULT_ITEMTYPE_CONFIG } from '../defaults';
import { ConversionMenu } from '../items/ItemComponents';
import Editors from '../components/editors';

const StyledButtonContainer = styled(Box)(({ theme }) => ({
  '& .MuiButton-root': {
    border: '0.05rem solid',
    borderRadius: theme.spacing(0.5),
    marginLeft: theme.spacing(1),
  },
}));

const ItemOptionsDialog: React.FC = () => {
  const { editor, setActiveItem, setItemOptionsActiveTab, setConfirmationDialogType } = useEditor();
  const item = editor.activeItem;
  const open = item && editor.itemOptionsActiveTab !== undefined || false;
  const canHaveChoices = item && (item.type === 'list' || item.type === 'multichoice');
  const [activeTab, setActiveTab] = React.useState<OptionsTabType>('label');
  const [editMode, setEditMode] = React.useState(false);
  const [id, setId] = React.useState<string>(item?.id || '');
  const isInputType = item && DEFAULT_ITEMTYPE_CONFIG.categories.find(c => c.type === 'input')?.items.some(i => i.config.type === item.type);

  const handleClose = () => {
    setItemOptionsActiveTab(undefined);
    setActiveItem(undefined);
  }

  const handleDelete = () => {
    setItemOptionsActiveTab(undefined);
    setConfirmationDialogType('delete');
  }

  React.useEffect(() => {
    if (editor.itemOptionsActiveTab !== undefined) {
      if (editor.itemOptionsActiveTab === 'id') {
        setEditMode(true);
      } else {
        setActiveTab(editor.itemOptionsActiveTab);
      }
    } else {
      setEditMode(false);
      setActiveTab('label');
    }
    setId(item?.id || '');
  }, [editor.itemOptionsActiveTab, open, item?.id]);

  if (!item) {
    return null;
  }

  return (
    <Dialog open={open} onClose={handleClose} fullWidth maxWidth='xl'>
      <DialogTitle sx={{ display: 'flex', flexDirection: 'row', alignItems: 'center' }}>
        {editMode ? <TextField value={id} autoFocus={editMode} onChange={(e) => setId(e.target.value)} InputProps={{
          endAdornment: (
            <>
              <IconButton onClick={() => setEditMode(false)}><Check color='success' /></IconButton>
              <IconButton onClick={() => setEditMode(false)}><Close color='error' /></IconButton>
            </>
          )
        }} /> :
          <Button variant='text' sx={{ color: 'inherit', textTransform: 'none' }} endIcon={<Edit color='primary' />} onClick={() => setEditMode(true)}>
            <Typography variant='h5' fontWeight='bold'>{id}</Typography>
          </Button>}
        <Box flexGrow={1} />
        <StyledButtonContainer>
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
