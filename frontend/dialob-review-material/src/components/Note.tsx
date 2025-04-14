import React, { useContext } from 'react';
import { makeStyles, createStyles } from '@mui/styles';
import { Paper, Theme } from '@mui/material';
import { ItemProps } from './componentTypes';
import { DialobContext } from '../context/DialobContext';

import { MarkdownView } from './MarkdownView';

const useStyles = makeStyles((theme: Theme) => createStyles({
  paper: {
    padding: theme.spacing(1)
  }
})
);

export const Note: React.FC<ItemProps> = ({ item }) => {
  const dC = useContext(DialobContext);
  const classes = useStyles();
  const label = dC.getTranslated(item.label);
  return (
    <Paper variant='outlined' className={classes.paper}>
      <MarkdownView text={dC.substituteVariables(label)} />
    </Paper>
  );

}
