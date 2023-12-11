import React, { useState } from 'react';
import { IconButton, Dialog, DialogTitle, Typography, DialogContent, useTheme, useMediaQuery } from '@mui/material';
import InfoOutlinedIcon from '@mui/icons-material/InfoOutlined';
import CloseIcon from '@mui/icons-material/Close';

import { ConfigContext } from '../';

export interface DescriptionProps {
  title?: string;
  text?: string;
}

export const Description: React.FC<DescriptionProps> = ({ title, text }) => {
  const config = React.useContext(ConfigContext);
  const [open, setOpen] = useState(false);
  const theme = useTheme();
  const fullScreen = useMediaQuery(theme.breakpoints.down('sm'));

  if (!text) {
    return null;
  }

  return (
    <>
      <IconButton onClick={() => setOpen(true)} color='secondary' size='small'>
        <InfoOutlinedIcon />
      </IconButton>
      <Dialog open={open} onClose={() => setOpen(false)} maxWidth='lg' fullWidth fullScreen={fullScreen}>
        <DialogTitle sx={{m: 0, p: 2}}>
            <Typography variant='h6'>{title || <span>&nbsp;</span>}</Typography>
            <IconButton arial-label='close' sx={{position: 'absolute', right: 8, top: 8, color: (theme) => theme.palette.grey[500]}} onClick={() => setOpen(false)}>
              <CloseIcon />
            </IconButton>
        </DialogTitle>
        <DialogContent dividers>
          {config.description(text)}
        </DialogContent>
      </Dialog>
    </>
  )

};
