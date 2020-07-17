import React, { useContext } from 'react';
import { DialobContext } from '../context/DialobContext';
import { ItemProps } from './componentTypes';
import { Grid } from '@material-ui/core';

interface QuestionnaireItemProps extends ItemProps {
  title: string;
}

export const Questionnaire: React.FC<QuestionnaireItemProps> = ({ item, title }) => {
  const dC = useContext(DialobContext);

  const items = item.items ? item.items.map(id => dC.createItem(id, null, true)) : null;
  return (
      <Grid container spacing={1}>
        {items}
      </Grid>
  );

}
