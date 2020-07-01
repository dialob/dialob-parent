import React from 'react';
import { Grid } from '@material-ui/core';

export interface QuestionnaireProps {};

export const Questionnaire: React.FC<QuestionnaireProps> = ({children}) => {
  return (
    <Grid xs={12} item>
      {children}
    </Grid>
  );
}