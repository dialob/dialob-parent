import { ItemAction } from '@dialob/fill-api';
import { useFillActions, useFillSession } from '@dialob/fill-react';
import React, {useContext, useState} from 'react';
import { Grid, Button, Dialog, DialogTitle, DialogActions, Paper } from '@material-ui/core';
import { Theme } from '@material-ui/core/styles';
import { makeStyles } from '@material-ui/styles';
import { Remove } from '@material-ui/icons';
import { FormattedMessage } from 'react-intl';
import { RowGroupContext } from '../context/RowGroupContext';

const useStyles = makeStyles((theme: Theme) => ({
  rowSurface: {
    padding: theme.spacing(1),
    marginTop: theme.spacing(1)
  },
  'rowSurface:hover': {
    backgroundColor: '#ff0000'
  },
  removeButton: {
    marginTop: theme.spacing(1),
    textAlign: 'center'
  }
}));

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
  const classes = useStyles();
  const rowGroupContext = useContext(RowGroupContext);
  const removeRow = () => {
    setConfirmationOpen(false);
    deleteRow(row.id);
  };
  let responsiveProps = {};

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
      <Paper variant='outlined' className={classes.rowSurface}>
        <Grid container spacing={1} justifyContent='center'>
          {children && itemIds.map(itemId => (
            <Grid item {...responsiveProps} key={itemId}>
              {children(itemId)}
            </Grid>
          ))}
        </Grid>
        <Grid container spacing={1} justifyContent='center'>
          <Grid item xs={1} className={classes.removeButton} >
            <Button size='small' color='primary' variant='contained' startIcon={<Remove />} onClick={() => setConfirmationOpen(true)}><FormattedMessage id='row.remove.button' /></Button>
          </Grid>
        </Grid>
      </Paper>
      <ConfirmationDialog isOpen={isConfirmationOpen} onClose={() => setConfirmationOpen(false)} onRemove={removeRow}/>
    </>
  );
}
