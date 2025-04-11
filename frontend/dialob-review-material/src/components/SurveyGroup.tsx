import React, { useContext } from 'react';
import { Typography, Paper, Grid, Box, Theme } from '@mui/material';
import { makeStyles, createStyles } from '@mui/styles';
import { ItemProps } from './componentTypes';
import { DialobContext } from '../context/DialobContext';
import { GroupContext } from '../context/GroupContext';

import Markdown from 'react-markdown';
import { Survey } from './Survey';

const useStyles = makeStyles((theme: Theme) => createStyles({
  paper: {
    padding: theme.spacing(2)
  },
  surveyContainer: {
    display: 'grid',
    alignItems: 'center',
    width: '100%'
  },
  vertical: {
    gridAutoFlow: 'column'
  },
  horizontal: {
    gridAutoFlow: 'row'
  },
  surveyHeader: {
    fontWeight: 'bold',
    padding: theme.spacing(1)
  }
})
);

const groupLevels = {
  1: 'h3',
  2: 'h4',
  3: 'h5',
  4: 'h6'
};

export const SurveyGroup: React.FC<ItemProps> = ({ item }) => {
  const classes = useStyles();
  const dC = useContext(DialobContext);
  const groupCtx = useContext(GroupContext);

  const surveys: string[] = [];
  const items: string[] = [];

  if (item.items) {
    for (const itemId of item.items) {
      const item = dC.getItem(itemId)
      if (!item) continue;
      if (item.type === 'survey') {
        surveys.push(itemId);
      } else {
        items.push(itemId);
      }
    }
  }

  const valueSet = dC.findValueSet(item.valueSetId);

  const vertical = item.view === 'verticalSurveygroup';
  const gridClass = vertical ? classes.vertical : classes.horizontal;
  const optionCount = valueSet?.entries.length || 0;

  const rowCount = vertical ? optionCount + 1 : surveys.length;
  const colCount = vertical ? surveys.length : optionCount;

  const normalItems = items.map(id => dC.createItem(id)).filter(item => item);

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
            description && <Markdown skipHtml>{description}</Markdown>
          }

          <Grid item xs={12}>
            <Box className={`${classes.surveyContainer} ${gridClass}`}
              style={{ gridTemplateRows: `repeat(${rowCount}, auto)`, gridTemplateColumns: `30% repeat(${colCount}, fit-content(30%))` }}
            >
              <Box></Box>
              {
                valueSet && valueSet.entries.map((entry, idx) => (
                  <Box className={classes.surveyHeader} key={entry.id}>{dC.getTranslated(entry.label)}</Box>
                ))
              }
              {
                valueSet && surveys.map((itemId, n) => (
                  <Survey key={itemId} id={itemId} valueSet={valueSet} even={n % 2 === 0} />
                ))
              }
            </Box>

          </Grid>

          {normalItems.map((i, k) => <Grid data-type='group-item-grid' key={k} item xs={12}>{i}</Grid>)}

        </Grid>
      </Paper>
    </GroupContext.Provider>
  );

}
