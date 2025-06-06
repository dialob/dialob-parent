import { ItemAction } from '@dialob/fill-api';
import { useFillActions } from '@dialob/fill-react';
import React, { PropsWithChildren } from 'react';
import { Typography, Grid, Button, Box } from '@mui/material';
import { Add } from '@mui/icons-material';
import { FormattedMessage } from 'react-intl';
import { Description } from './Description';
import { RowGroupContext } from '../context/RowGroupContext';

export interface RowGroupProps {
  rowGroup: ItemAction<'rowgroup'>['item'];
};

export const RowGroup: React.FC<PropsWithChildren<RowGroupProps>> = ({ rowGroup, children }) => {
  const { addRowToGroup } = useFillActions();
  const indent = parseInt(rowGroup.props?.indent ?? undefined);

  return (
    <>
      <Grid item xs={12} style={{ marginBottom: '5px' }}>
        <Typography variant='h3'>
          {rowGroup.label || <span>&nbsp;</span>}
          <Description title={rowGroup.label} text={rowGroup.description} />
          <Button size='small' color='primary' variant='contained' sx={{ float: 'right' }} onClick={() => addRowToGroup(rowGroup.id)} startIcon={<Add />} style={{ marginBottom: '3px' }} disabled={!(rowGroup.allowedActions && rowGroup.allowedActions.includes('ADD_ROW'))}><FormattedMessage id='row.add.button' /></Button>
        </Typography>
      </Grid>
      <Box sx={indent ? { paddingLeft: indent } : undefined}>
        <RowGroupContext.Provider value={{ rowGroup }}>
          {children}
        </RowGroupContext.Provider>
      </Box>
    </>
  );
};
