import React from 'react';
import { Dialog, DialogTitle, DialogContent, Button, Box, Typography, Tabs, Tab, DialogActions } from '@mui/material';
import { Rule, Gavel } from "@mui/icons-material";
import { useEditor } from '../editor';
import { DEFAULT_ITEMTYPE_CONFIG } from '../defaults';
import ChoiceEditor from '../components/ChoiceEditor';
import PropertiesEditor from '../components/PropertiesEditor';

const ItemOptionsDialog: React.FC = () => {
  const { editor, setActiveItem, setItemOptionsDialogOpen, setRuleEditDialogType, setValidationRuleEditDialogOpen } = useEditor();
  const item = editor.activeItem;
  const open = item && editor.itemOptionsDialogOpen || false;
  const canHaveChoices = item && (item.type === 'list' || item.type === 'multichoice');
  const [activeTab, setActiveTab] = React.useState<'choices' | 'properties' | 'styles'>(canHaveChoices ? 'choices' : 'properties');
  const canHaveRules = item && DEFAULT_ITEMTYPE_CONFIG.categories.find(c => c.type === 'input')?.items.some(i => i.config.type === item.type);

  const handleClose = () => {
    setItemOptionsDialogOpen(false);
    setActiveItem(undefined);
  }

  const handleRequirementClick = () => {
    setRuleEditDialogType('requirement');
    setItemOptionsDialogOpen(false);
  }

  const handleValidationClick = () => {
    setValidationRuleEditDialogOpen(true);
    setItemOptionsDialogOpen(false);
  }

  React.useEffect(() => {
    setActiveTab(canHaveChoices ? 'choices' : 'properties');
  }, [canHaveChoices]);

  if (!item) {
    return null;
  }

  return (
    <Dialog open={open} onClose={handleClose} maxWidth='md' fullWidth>
      <DialogTitle sx={{ display: 'flex', flexDirection: 'row', alignItems: 'center' }}>
        <Typography>Item options for <b>{item.id}</b></Typography>
        <Box flexGrow={1} />
        {canHaveRules && <Box sx={{ display: 'flex', width: 0.3, justifyContent: 'space-between' }}>
          <Button color='inherit' variant='contained' endIcon={<Rule fontSize='small' />} onClick={handleValidationClick}>Validation</Button>
          <Button color='inherit' variant='contained' endIcon={<Gavel fontSize='small' />} onClick={handleRequirementClick}>Requirement</Button>
        </Box>}
      </DialogTitle>
      <DialogContent>
        <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
          <Tabs value={activeTab} onChange={(e, value) => setActiveTab(value)} aria-label="basic tabs example">
            {canHaveChoices && <Tab label="Choices" value='choices' />}
            <Tab label="Properties" value='properties' />
            <Tab label="Style classes" value='styles' />
          </Tabs>
        </Box>
        <Box sx={{ mt: 2 }}>
          {activeTab === 'choices' && <ChoiceEditor />}
          {activeTab === 'properties' && <PropertiesEditor />}
          {activeTab === 'styles' && <>Styles</>}
        </Box>
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose} color="primary">Close</Button>
      </DialogActions>
    </Dialog>
  );
};

export default ItemOptionsDialog;
