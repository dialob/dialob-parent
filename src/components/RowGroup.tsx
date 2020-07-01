import { ItemAction } from '@resys/dialob-fill-api';
import { useFillSession } from '@resys/dialob-fill-react';
import React from 'react';
import { Typography, Grid, Button, } from '@material-ui/core';
import { makeStyles, Theme } from '@material-ui/core/styles';
import { Add } from '@material-ui/icons';
import { FormattedMessage } from 'react-intl';
import { Description } from './Description';

const useStyles = makeStyles((theme: Theme) => ({
  addButton: {
   float: 'right'
  }
}));

export interface RowGroupProps {
  rowGroup: ItemAction<'rowgroup'>['item'];
};

export const RowGroup: React.FC<RowGroupProps> = ({ rowGroup, children }) => {
  const session = useFillSession();
  const classes = useStyles();
  return (
    <>
      <Grid item xs={12} style={{ marginBottom: '5px' }}>
        <Typography variant='h3'>
          {rowGroup.label || <span>&nbsp;</span>}
          <Description title={rowGroup.label} text={rowGroup.description} />
          <Button size='small' color='primary' variant='contained' className={classes.addButton} onClick={() => session.addRowToGroup(rowGroup.id)} startIcon={<Add />} style={{ marginBottom: '3px' }}><FormattedMessage id='row.add.button' /></Button>
        </Typography>
      </Grid>
      {children}
    </>
  );
};
