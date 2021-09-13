import { ItemAction } from '@dialob/fill-api';
import { useFillActions } from '@dialob/fill-react';
import React from 'react';
import { Typography, Grid, Button, } from '@mui/material';
import { Add } from '@mui/icons-material';
import { FormattedMessage } from 'react-intl';
import { Description } from './Description';
import { RowGroupContext } from '../context/RowGroupContext';

export interface RowGroupProps {
  rowGroup: ItemAction<'rowgroup'>['item'];
};

export const RowGroup: React.FC<RowGroupProps> = ({ rowGroup, children }) => {
  const {addRowToGroup} = useFillActions();
  return (
    <>
      <Grid item xs={12} style={{ marginBottom: '5px' }}>
        <Typography variant='h3'>
          {rowGroup.label || <span>&nbsp;</span>}
          <Description title={rowGroup.label} text={rowGroup.description} />
          <Button size='small' color='primary' variant='contained' sx={{float: 'right'}} onClick={() => addRowToGroup(rowGroup.id)} startIcon={<Add />} style={{ marginBottom: '3px' }}><FormattedMessage id='row.add.button' /></Button>
        </Typography>
      </Grid>
      <RowGroupContext.Provider value={{rowGroup}}>
        {children}
      </RowGroupContext.Provider>
    </>
  );
};
