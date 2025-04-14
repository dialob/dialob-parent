import { ValueSetAction } from '@dialob/fill-api';
import React from 'react';
import { Box, Radio } from '@mui/material';

import { useFillItem, useFillActions } from '@dialob/fill-react';
import { Description } from './Description';


export interface SurveyProps {
  id: string;
  valueSet: ValueSetAction['valueSet'];
  even: boolean;
};

export const Survey: React.FC<SurveyProps> = ({ id, valueSet, even }) => {
  const { item: survey } = useFillItem(id);
  const { setAnswer } = useFillActions();

  if (!survey) {
    return null;
  }

  return (
    <>
      <Box sx={{ p: 1, fontWeight: 'bold' }}>{survey?.label} <Description title={survey?.label} text={survey.description} /></Box>
      {valueSet && valueSet.entries.map((entry) => (
        <Box key={entry.key} sx={{ p: 1, textAlign: 'center', bgcolor: even ? 'background.default' : undefined }} >
          <Radio checked={entry.key === survey?.value} onChange={() => setAnswer(survey?.id, entry.key)} />
        </Box>
      ))}
    </>
  );

}
