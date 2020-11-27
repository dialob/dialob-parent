import { ValueSetAction } from '@dialob/fill-api';
import React from 'react';
import { Box, createStyles, makeStyles, Radio, Theme } from '@material-ui/core';
import { useFillItem, useFillActions } from '@dialob/fill-react';
import { Description } from './Description';

const useStyles = makeStyles((theme: Theme) => createStyles({
  questionLabel: {
    fontWeight: 'bold',
    padding: theme.spacing(1),
  },
  questionItem: {
    textAlign: 'center',
    padding: theme.spacing(1)
  },
  even: {
    backgroundColor: theme.palette.background.default
  }
})
);

export interface SurveyProps {
  id: string;
  valueSet: ValueSetAction['valueSet'];
  even: boolean;
};

export const Survey: React.FC<SurveyProps> = ({ id, valueSet, even }) => {
  const { item: survey } = useFillItem(id);
  const classes = useStyles();
  const { setAnswer } = useFillActions();

  if (!survey) {
    return null;
  }

  return (
    <>
      <Box className={classes.questionLabel}>{survey?.label} <Description title={survey?.label} text={survey.description} /></Box>
      { valueSet && valueSet.entries.map((entry) => (
        <Box key={entry.key} className={`${classes.questionItem} ${even ? classes.even : ''}`}>
          <Radio checked={entry.key === survey?.value} onChange={() => setAnswer(survey?.id, entry.key)} />
        </Box>
      ))}
    </>
  );

}
