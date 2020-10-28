import { ItemAction } from '@resys/dialob-fill-api';
import { useFillActions, useFillSession } from '@resys/dialob-fill-react';
import React, {useState} from 'react';
import { Grid, Button, Dialog, DialogTitle, DialogActions, Paper } from '@material-ui/core';
import { makeStyles, Theme } from '@material-ui/core/styles';
import { Remove } from '@material-ui/icons';
import { FormattedMessage } from 'react-intl';

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
  const removeRow = () => {
    setConfirmationOpen(false);
    deleteRow(row.id);
  };
  let responsiveProps = {};

  const itemIds = row?.items ? row.items.filter(itemId => session.getItem(itemId)) : [];

  responsiveProps = {
    xs: 12
  };

  return (
    <>
      <Paper variant='outlined' className={classes.rowSurface}>
        <Grid container spacing={1} justify='center'>
          {children && itemIds.map(itemId => (
            <Grid item {...responsiveProps} key={itemId}>
              {children(itemId)}
            </Grid>
          ))}
        </Grid>
        <Grid container spacing={1} justify='center'>
          <Grid item xs={1} className={classes.removeButton} >
            <Button size='small' color='primary' variant='contained' startIcon={<Remove />} onClick={() => setConfirmationOpen(true)}><FormattedMessage id='row.remove.button' /></Button>
          </Grid>
        </Grid>
      </Paper>
      <ConfirmationDialog isOpen={isConfirmationOpen} onClose={() => setConfirmationOpen(false)} onRemove={removeRow}/>
    </>
  );
}
