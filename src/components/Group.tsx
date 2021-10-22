import React, { useContext } from 'react';
import { makeStyles, createStyles } from '@mui/styles';
import { Typography, Paper, Grid, Theme } from '@mui/material';

import { ItemProps } from './componentTypes';
import { DialobContext } from '../context/DialobContext';
import { GroupContext } from '../context/GroupContext';

import Markdown from 'react-markdown';

const useStyles = makeStyles((theme: Theme) => createStyles({
  paper: {
    padding: theme.spacing(2)
  }
})
);

const groupLevels = {
  1: 'h3',
  2: 'h4',
  3: 'h5',
  4: 'h6'
};

export const Group: React.FC<ItemProps> = ({ item }) => {
  const classes = useStyles();
  const dC = useContext(DialobContext);
  const groupCtx = useContext(GroupContext);
  const items = item.items ? item.items.map(id => dC.createItem(id)).filter(item => item) : null;
  if (!items || items.length === 0) {
    return null;
  }

  const description = dC.getTranslated(item.description);

  return (
    <GroupContext.Provider value={{ level: groupCtx.level < 4 ? groupCtx.level + 1 : groupCtx.level }}>
      <Paper data-type='group-paper' elevation={groupCtx.level} className={classes.paper}>
        <Grid data-type='group-grid' container spacing={2}>
          <Grid data-type='group-title' item xs={12}>
            <Typography variant={groupLevels[groupCtx.level]}>
              {dC.getTranslated(item.label)}
            </Typography>
          </Grid>

          {
            description && <Markdown source={description} escapeHtml />
          }

          {items.map((i, k) => <Grid data-type='group-item-grid' key={k} item xs={12}>{i}</Grid>)}
        </Grid>
      </Paper>
    </GroupContext.Provider>
  );

}