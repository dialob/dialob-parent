import React from "react";
import { Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, Tab, Tabs, Tooltip } from "@mui/material";
import { Close, Help, Translate, UploadFile, Warning } from "@mui/icons-material";
import { FormattedMessage } from "react-intl";
import Translations from "../components/translations";

type TranslationTabType = 'files' | 'languages' | 'missing';

const TranslationDialog: React.FC<{ open: boolean, onClose: () => void }> = ({ open, onClose }) => {
  const [activeTab, setActiveTab] = React.useState<TranslationTabType>('files');

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth='xl'>
      <DialogTitle sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', fontWeight: 'bold' }}>
        <FormattedMessage id='dialogs.translations.title' />
        <Button variant='outlined' endIcon={<Help />}
          onClick={() => window.open('https://docs.dialob.io/#/200_advanced_operations/500_translations', "_blank")}>
          <FormattedMessage id='buttons.help' />
        </Button>
      </DialogTitle>
      <DialogContent sx={{ height: '70vh', borderTop: 1, borderBottom: 1, borderColor: 'divider', p: 0, display: 'flex' }}>
        <Tabs value={activeTab} onChange={(e, v) => setActiveTab(v)} orientation='vertical' sx={{ borderRight: 1, borderColor: 'divider' }}>
          <Tab icon={<Tooltip placement='right' title={<FormattedMessage id='dialogs.translations.files.title' />}><UploadFile /></Tooltip>} value='files' />
          <Tab icon={<Tooltip placement='right' title={<FormattedMessage id='dialogs.translations.languages.title' />}><Translate /></Tooltip>} value='languages' />
          <Tab icon={<Tooltip placement='right' title={<FormattedMessage id='dialogs.translations.missing.title' />}><Warning /></Tooltip>} value='missing' />
        </Tabs>
        <Box sx={{ p: 3, width: 1 }}>
          {activeTab === 'files' && <Translations.Files />}
          {activeTab === 'languages' && <Translations.Languages />}
          {activeTab === 'missing' && <Translations.Missing />}
        </Box>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} endIcon={<Close />}><FormattedMessage id='buttons.close' /></Button>
      </DialogActions>
    </Dialog>
  );
}

export default TranslationDialog;
