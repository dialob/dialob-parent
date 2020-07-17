import React, { useContext } from 'react';
import { ItemProps } from './componentTypes';
import { DialobContext } from '../context/DialobContext';
import { Typography, Grid } from '@material-ui/core';

export const Page: React.FC<ItemProps> = ({ item }) => {
  const dC = useContext(DialobContext);
  const items = item.items ? item.items.map(id => dC.createItem(id)) : null;
  const label = dC.getTranslated(item.label);
  return (
    <>
    <Grid container spacing={1}>
      <Grid item xs={12}>
       <Typography variant='h2'>{label}</Typography>
     </Grid>
      {items.map((i, k) => <Grid key={k} item xs={12}>{i}</Grid>)}
    </Grid>

    </>
  );

}