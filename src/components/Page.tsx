import React from 'react';
import { ItemAction } from '@dialob/fill-api';
import { Typography, Grid } from '@material-ui/core';

export interface PageProps {
  page: ItemAction<'group'>['item'];
};
export const Page: React.FC<PageProps> = ({ page, children }) => {
  return (
    <Grid container spacing={1}>
      <Grid item xs={12}>
        <Typography variant='h2'>
          {page.label}
        </Typography>
      </Grid>
      {React.Children.map(children, i =>
        <Grid item xs={12}>
          {i}
        </Grid>
      )}
    </Grid>
  );

}