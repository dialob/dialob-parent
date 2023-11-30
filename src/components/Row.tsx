import { ItemAction } from '@dialob/fill-api';
import { useFillActions, useFillSession } from '@dialob/fill-react';
import React, {useContext, useState} from 'react';
import { Grid, Button, Dialog, DialogTitle, DialogActions, Paper } from '@mui/material';
import { Remove } from '@mui/icons-material';
import { FormattedMessage } from 'react-intl';
import { RowGroupContext } from '../context/RowGroupContext';

interface ConfirmationProps {
  isOpen: boolean;
  onClose: () => void;
  onRemove: () => void;
}

const ConfirmationDialog: React.FC<ConfirmationProps> = ({ isOpen, onClose, onRemove }) => {

  return (
    <Dialog open={isOpen}>
      <DialogTitle><FormattedMessage id='row.remove.confirmation.title' /></DialogTitle>
      <DialogActions>
        <Button onClick={onClose} color='secondary'><FormattedMessage id='row.remove.confirmation.cancel' /></Button>
        <Button onClick={onRemove} color='primary' autoFocus><FormattedMessage id='row.remove.confirmation.confirm' /></Button>
      </DialogActions>
    </Dialog>
  );
};

export interface RowProps {
  row: ItemAction<'row'>['item'];
  children?: (itemId: string) => React.ReactNode;
};
export const Row: React.FC<RowProps> = ({ row, children }) => {
  const session = useFillSession();
  const {deleteRow} = useFillActions();
  const [isConfirmationOpen, setConfirmationOpen] = useState(false);
  const rowGroupContext = useContext(RowGroupContext);
  const removeRow = () => {
    setConfirmationOpen(false);
    deleteRow(row.id);
  };
  let responsiveProps = {};
  const spacesTop = parseInt(row.props?.spacesTop || 0);
  const spacesBottom = parseInt(row.props?.spacesBottom || 0);

  const itemIds = row?.items ? row.items.filter(itemId => session.getItem(itemId)) : [];

  let columns = rowGroupContext.rowGroup?.props?.columns || 1;
  columns = columns > 4 ? 4 : columns;
  //@ts-ignore
  const lg: ColumnType = columns > 1 ? Math.floor(12 / columns) : undefined;

  responsiveProps = {
    xs: 12,
    lg
  };

  return (
    <>
      <Paper variant='outlined' sx={{p: 1, marginTop: (theme) => theme.spacing(spacesTop), marginBottom: (theme) => theme.spacing(spacesBottom)}}>
        <Grid container spacing={1} justifyContent='center'>
          {children && itemIds.map(itemId => (
            <Grid item {...responsiveProps} key={itemId}>
              {children(itemId)}
            </Grid>
          ))}
        </Grid>
        <Grid container spacing={1} justifyContent='center'>
          <Grid item xs={1} sx={{mt: 1, textAlign: 'center'}} >
            <Button size='small' color='primary' variant='contained' startIcon={<Remove />} onClick={() => setConfirmationOpen(true)} disabled={!(row.allowedActions && row.allowedActions.includes('DELETE_ROW'))}><FormattedMessage id='row.remove.button' /></Button>
          </Grid>
        </Grid>
      </Paper>
      <ConfirmationDialog isOpen={isConfirmationOpen} onClose={() => setConfirmationOpen(false)} onRemove={removeRow}/>
    </>
  );
}
