import React, { PropsWithChildren, useMemo } from 'react';
import { Grid } from '@mui/material';
import { ConfigContext } from '../';
import { useFillItem } from '@dialob/fill-react';

export interface QuestionnaireProps {

};

export const Questionnaire: React.FC<PropsWithChildren<QuestionnaireProps>> = ({ children }) => {
  const { item: questionnaire } = useFillItem('questionnaire');
  const config = React.useContext(ConfigContext);

  const canNavigate = useMemo(() => {
    // Check if page navigation is allowed. If not we disable breadcrumb navigation also
    return (questionnaire?.allowedActions && (questionnaire?.allowedActions.includes('NEXT')
      || questionnaire?.allowedActions.includes('PREVIOUS')
      || questionnaire?.allowedActions.includes('COMPLETE')) || false);
  }, [questionnaire?.allowedActions]);

  return (
    <Grid xs={12} item>
      {config.breadCrumbs(questionnaire?.availableItems ? questionnaire?.availableItems : [], canNavigate, questionnaire?.activeItem)}
      {children}
    </Grid>
  );
}
