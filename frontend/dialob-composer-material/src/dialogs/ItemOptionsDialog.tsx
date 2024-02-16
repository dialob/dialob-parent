import React from 'react';
import { Dialog, DialogTitle, DialogContent, Button, Box, Typography, Tabs, Tab, DialogActions } from '@mui/material';
import { Rule, Gavel } from "@mui/icons-material";
import { useEditor } from '../editor';
import { DEFAULT_ITEMTYPE_CONFIG } from '../defaults';
import ChoiceEditor from '../components/ChoiceEditor';
import PropertiesEditor from '../components/PropertiesEditor';
import { FormattedMessage } from 'react-intl';
import DefaultValueEditor from '../components/DefaultValueEditor';

type OptionsTabType = 'choices' | 'properties' | 'styles' | 'defaults';

const ItemOptionsDialog: React.FC = () => {
  const { editor, setActiveItem, setItemOptionsDialogOpen, setRuleEditDialogType, setValidationRuleEditDialogOpen } = useEditor();
  const item = editor.activeItem;
  const open = item && editor.itemOptionsDialogOpen || false;
  const canHaveChoices = item && (item.type === 'list' || item.type === 'multichoice');
  const [activeTab, setActiveTab] = React.useState<OptionsTabType>(canHaveChoices ? 'choices' : 'properties');
  const isInputType = item && DEFAULT_ITEMTYPE_CONFIG.categories.find(c => c.type === 'input')?.items.some(i => i.config.type === item.type);

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
    let newActiveTab: OptionsTabType = 'properties';
    if (isInputType) {
      newActiveTab = 'defaults';
    }
    if (canHaveChoices) {
      newActiveTab = 'choices';
    }
    setActiveTab(newActiveTab);
  }, [canHaveChoices, isInputType]);

  if (!item) {
    return null;
  }

  return (
    <Dialog open={open} onClose={handleClose} maxWidth='md' fullWidth>
      <DialogTitle sx={{ display: 'flex', flexDirection: 'row', alignItems: 'center' }}>
        <Typography><FormattedMessage id='dialogs.options.title' values={{ itemId: item.id }} /></Typography>
        <Box flexGrow={1} />
        {isInputType && <Box sx={{ display: 'flex', width: 0.3, justifyContent: 'space-between' }}>
          <Button color='inherit' variant='contained' endIcon={<Rule fontSize='small' />} onClick={handleValidationClick}>
            <FormattedMessage id='dialogs.options.buttons.validation' />
          </Button>
          <Button color='inherit' variant='contained' endIcon={<Gavel fontSize='small' />} onClick={handleRequirementClick}>
            <FormattedMessage id='dialogs.options.buttons.requirement' />
          </Button>
        </Box>}
      </DialogTitle>
      <DialogContent>
        <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
          <Tabs value={activeTab} onChange={(e, value) => setActiveTab(value)}>
            {canHaveChoices && <Tab label={<FormattedMessage id='dialogs.options.tabs.choices' />} value='choices' />}
            {isInputType && <Tab label={<FormattedMessage id='dialogs.options.tabs.default' />} value='defaults' />}
            <Tab label={<FormattedMessage id='dialogs.options.tabs.properties' />} value='properties' />
            <Tab label={<FormattedMessage id='dialogs.options.tabs.styles' />} value='styles' />
          </Tabs>
        </Box>
        <Box sx={{ mt: 2 }}>
          {activeTab === 'choices' && <ChoiceEditor />}
          {activeTab === 'defaults' && <DefaultValueEditor />}
          {activeTab === 'properties' && <PropertiesEditor />}
          {activeTab === 'styles' && <>Styles</>}
        </Box>
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose} color="primary"><FormattedMessage id='buttons.close' /></Button>
      </DialogActions>
    </Dialog>
  );
};

export default ItemOptionsDialog;
