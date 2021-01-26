import React, { useContext } from 'react';
import { Box, createStyles, makeStyles, Theme } from '@material-ui/core';
import { DialobContext } from '../context/DialobContext';
import { RadioButtonChecked, RadioButtonUnchecked } from '@material-ui/icons';

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
  valueSet: any;
  even: boolean;
};

export const Survey: React.FC<SurveyProps> = ({ id, valueSet, even }) => {
  const classes = useStyles();
  const dC = useContext(DialobContext);
  const survey = dC.getItem(id)
  const answer = dC.getAnswer(id);

  return (
    <>
      <Box className={classes.questionLabel}>{dC.getTranslated(survey.label)} </Box>
      { valueSet && valueSet.entries.map((entry) => (
        <Box key={entry.id} className={`${classes.questionItem} ${even ? classes.even : ''}`}>
          { entry.id === answer ? <RadioButtonChecked /> : <RadioButtonUnchecked />}
        </Box>
      ))}
    </>
  );

}
