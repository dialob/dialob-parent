import React from 'react';
import { FormattedMessage } from 'react-intl';
import { Box, CircularProgress, Dialog, DialogContent, DialogTitle, Typography } from '@mui/material';

interface TranslationProgressDialogProps {
  open: boolean;
  current: number;
  total: number;
  title?: string;
}

const TranslationProgressDialog: React.FC<TranslationProgressDialogProps> = ({
  open,
  current,
  total,
  title
}) => {
  return (
    <Dialog open={open}>
      <DialogTitle>
        {title || <FormattedMessage id='dialogs.translating' />}
      </DialogTitle>
      <DialogContent>
        <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 2, py: 2 }}>
          <CircularProgress />
          <Typography>
            <FormattedMessage 
              id='dialogs.translating.progress' 
              values={{ current, total }}
            />
          </Typography>
        </Box>
      </DialogContent>
    </Dialog>
  );
};

export default TranslationProgressDialog;
