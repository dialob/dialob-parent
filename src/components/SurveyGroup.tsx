import { Box, Fade, Grid, Paper, Typography } from '@mui/material';
import { ItemAction } from '@dialob/fill-api';
import { Description } from './Description';
import { GroupContext } from '../context/GroupContext';
import { useFillSession, useFillValueSet } from '@dialob/fill-react';
import React, { useContext } from 'react';
import { Survey } from './Survey';

export interface SurveyGroupProps {
  surveyGroup: ItemAction<'surveygroup'>['item'];
};

export const SurveyGroup: React.FC<SurveyGroupProps> = ({ surveyGroup, children }) => {
  const valueSet = useFillValueSet(surveyGroup.valueSetId);
  const session = useFillSession();
  const groupCtx = useContext(GroupContext);

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
  const optionCount = valueSet?.entries.length || 0;

  const rowCount = vertical ? optionCount + 1 : surveys.length;
  const colCount = vertical ? surveys.length : optionCount;

  return (
    <GroupContext.Provider value={{ level: groupCtx.level < 6 ? groupCtx.level + 1 : groupCtx.level }}>
      <Fade in={true}>
        <Paper elevation={groupCtx.level} sx={{p: 2}}>
          <Grid container spacing={2}>
            <Grid item xs={12}>
              <Typography variant='h3'>
                {surveyGroup.label || <span>&nbsp;</span>}
              </Typography>
              <Description title={surveyGroup.label} text={surveyGroup.description} />
            </Grid>

            <Grid item xs={12}>
              <Box sx={{display: 'grid', alignItems: 'center', width: '100%', gridAutoFlow: vertical ? 'column': 'row', gridTemplateRows: `repeat(${rowCount}, auto)`, gridTemplateColumns: `30% repeat(${colCount}, fit-content(30%))` }}>
                <Box></Box>
                {
                  valueSet && valueSet.entries.map(entry => (
                    <Box sx={{p: 1, fontWeight: 'bold'}} key={entry.key}>{entry.value}</Box>
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
