import { Box, Fade, Grid, Paper, Theme, Typography } from '@material-ui/core';
import { makeStyles, createStyles } from '@material-ui/styles';
import { ItemAction } from '@dialob/fill-api';
import { Description } from './Description';
import { GroupContext } from '../context/GroupContext';
import { useFillSession, useFillValueSet } from '@dialob/fill-react';
import React, { useContext } from 'react';
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

export interface SurveyGroupProps {
  surveyGroup: ItemAction<'surveygroup'>['item'];
};

export const SurveyGroup: React.FC<SurveyGroupProps> = ({ surveyGroup, children }) => {
  const valueSet = useFillValueSet(surveyGroup.valueSetId);
  const session = useFillSession();
  const groupCtx = useContext(GroupContext);
  const classes = useStyles();

  const surveys: string[] = [];
  const items: string[] = [];

  if (surveyGroup.items) {
    for (const itemId of surveyGroup.items) {
      const item = session.getItem(itemId);
      if (!item) continue;
      if (item.type === 'survey') {
        surveys.push(itemId);
      } else {
        items.push(itemId);
      }
    }
  }

  const vertical = surveyGroup.view === 'verticalSurveygroup';
  const gridClass = vertical ? classes.vertical : classes.horizontal;
  const optionCount = valueSet?.entries.length || 0;

  const rowCount = vertical ? optionCount + 1 : surveys.length;
  const colCount = vertical ? surveys.length : optionCount;

  return (
    <GroupContext.Provider value={{ level: groupCtx.level < 6 ? groupCtx.level + 1 : groupCtx.level }}>
      <Fade in={true}>
        <Paper elevation={groupCtx.level} className={classes.paper}>
          <Grid container spacing={2}>
            <Grid item xs={12}>
              <Typography variant='h3'>
                {surveyGroup.label || <span>&nbsp;</span>}
              </Typography>
              <Description title={surveyGroup.label} text={surveyGroup.description} />
            </Grid>

            <Grid item xs={12}>
              <Box className={`${classes.surveyContainer} ${gridClass}`}
                style={{ gridTemplateRows: `repeat(${rowCount}, auto)`, gridTemplateColumns: `30% repeat(${colCount}, fit-content(30%))` }}
              >
                <Box></Box>
                {
                  valueSet && valueSet.entries.map(entry => (
                    <Box className={classes.surveyHeader} key={entry.key}>{entry.value}</Box>
                  ))
                }
                {
                  valueSet && surveys.map((itemId, n) => (
                    <Survey key={itemId} id={itemId} valueSet={valueSet} even={n % 2 === 0} />
                  ))
                }
              </Box>
            </Grid>

            {
              items.length > 0 && React.Children.map(children, i => {
                const item = i as any;
                if (item && items.indexOf(item.props.id) > -1) { // Yes, this is a kludge
                  return (<Grid item xs={12}>{i}</Grid>)
                } else {
                  return null;
                }
              })
            }

          </Grid>
        </Paper>
      </Fade>

    </GroupContext.Provider>
  );
}
