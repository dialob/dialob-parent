import React, { useState } from 'react';
import { Theme, IconButton, Dialog, DialogTitle, Typography, DialogContent, useTheme, useMediaQuery } from '@material-ui/core';
import { makeStyles, createStyles } from '@material-ui/styles';
import InfoOutlinedIcon from '@material-ui/icons/InfoOutlined';
import CloseIcon from '@material-ui/icons/Close';

import { ConfigContext } from '../';

const useStyles = makeStyles((theme: Theme) => createStyles({
  dialogTitle: {
    margin: 0,
    padding: theme.spacing(2),
  },
  dialogClose: {
    position: 'absolute',
    right: theme.spacing(1),
    top: theme.spacing(1),
    color: theme.palette.grey[500],
  }
})
);

export interface DescriptionProps {
  title?: string;
  text?: string;
}

export const Description: React.FC<DescriptionProps> = ({ title, text }) => {
  const classes = useStyles();
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
        <DialogTitle className={classes.dialogTitle}>
            <Typography variant='h2'>{title || <span>&nbsp;</span>}</Typography>
            <IconButton arial-label='close' className={classes.dialogClose} onClick={() => setOpen(false)}>
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
