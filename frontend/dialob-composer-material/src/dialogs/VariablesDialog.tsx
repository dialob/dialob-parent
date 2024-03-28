import React from 'react';
import { Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, Tab, Tabs, Typography } from '@mui/material';
import { Close, Help } from '@mui/icons-material';
import { FormattedMessage } from 'react-intl';
import ExpressionVariables from '../components/variables/ExpressionVariables';
import ContextVariables from '../components/variables/ContextVariables';

const VariablesDialog: React.FC<{ open: boolean, onClose: () => void }> = ({ open, onClose }) => {
  const [activeTab, setActiveTab] = React.useState<'context' | 'expression'>('context');
  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth='xl'>
      <DialogTitle sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <Typography fontWeight='bold'><FormattedMessage id='dialogs.variables.title' /></Typography>
        <Button variant='outlined' endIcon={<Help />}
          onClick={() => window.open('https://docs.dialob.io/#/200_advanced_operations/700_custom_variables', "_blank")}>
          <FormattedMessage id='help' />
        </Button>
      </DialogTitle>
      <DialogContent sx={{ height: '70vh', borderTop: 1, borderBottom: 1, borderColor: 'divider', p: 0 }}>
        <Tabs value={activeTab} onChange={(e, v) => setActiveTab(v)} sx={{ borderBottom: 1, borderColor: 'divider' }}>
          <Tab value='context' label={<FormattedMessage id='dialogs.variables.context.title' />} />
          <Tab value='expression' label={<FormattedMessage id='dialogs.variables.expression.title' />} />
        </Tabs>
        <Box sx={{ p: 3, width: 1 }}>
          {activeTab === 'context' && <ContextVariables />}
          {activeTab === 'expression' && <ExpressionVariables />}
        </Box>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} endIcon={<Close />}>Close</Button>
      </DialogActions>
    </Dialog>
  )
}

export default VariablesDialog;
