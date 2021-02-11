import { ItemAction } from '@dialob/fill-api';
import { useFillActions } from '@dialob/fill-react';
import React from 'react';
import { Typography, Grid, Button, } from '@material-ui/core';
import { makeStyles, Theme } from '@material-ui/core/styles';
import { Add } from '@material-ui/icons';
import { FormattedMessage } from 'react-intl';
import { Description } from './Description';
import { RowGroupContext } from '../context/RowGroupContext';

const useStyles = makeStyles((theme: Theme) => ({
  addButton: {
   float: 'right'
  }
}));

export interface RowGroupProps {
  rowGroup: ItemAction<'rowgroup'>['item'];
};

export const RowGroup: React.FC<RowGroupProps> = ({ rowGroup, children }) => {
  const {addRowToGroup} = useFillActions();
  const classes = useStyles();
  return (
    <>
      <Grid item xs={12} style={{ marginBottom: '5px' }}>
        <Typography variant='h3'>
          {rowGroup.label || <span>&nbsp;</span>}
          <Description title={rowGroup.label} text={rowGroup.description} />
          <Button size='small' color='primary' variant='contained' className={classes.addButton} onClick={() => addRowToGroup(rowGroup.id)} startIcon={<Add />} style={{ marginBottom: '3px' }}><FormattedMessage id='row.add.button' /></Button>
        </Typography>
      </Grid>
      <RowGroupContext.Provider value={{rowGroup}}>
        {children}
      </RowGroupContext.Provider>
    </>
  );
};
