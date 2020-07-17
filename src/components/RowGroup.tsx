import React, { useContext } from 'react';
import { ItemProps } from './componentTypes';
import { DialobContext } from '../context/DialobContext';
import { RowGroupContext } from '../context/RowGroupContext';
import { GroupContext } from '../context/GroupContext';
import { makeStyles } from '@material-ui/core/styles';
import { Table, TableContainer, Paper, TableHead, TableRow, TableBody, TableCell, Grid, Typography } from '@material-ui/core';

const useStyles = makeStyles({
  head: {
    fontWeight: 'bold',
    whiteSpace: 'nowrap'
  }
});

export const RowGroup: React.FC<ItemProps> = ({ item }) => {
  const dC = useContext(DialobContext);
  const groupCtx = useContext(GroupContext);
  const classes = useStyles();

  const getRowIds = item => {
    let result:string[] = [];
    const answer = dC.getAnswer(item.id);
    if (answer) {
      answer.forEach(a => {
        result.push(`${item.id}.${a}`);
      });
    }
    return result;
  };

  const headers = item.items ? item.items.map(id =>
    <TableCell key={id} className={classes.head}>
        {dC.getTranslated(dC.getItem(id).label)}
    </TableCell>) : null;

  const rowIds = getRowIds(item);
  let rows: JSX.Element[] | null = null;
  if (rowIds && headers) {
    rows = rowIds.map(rowId => <TableRow data-type='group-table-row' key={rowId}>
      {item.items.map(id => <TableCell key={id}>
          {dC.createItem(id, `${rowId}.${id}`)}
        </TableCell>)}
    </TableRow> );
  }

  if (!rows || rows.length === 0) {
    return null;
  }

  const groupTable = (
    <TableContainer data-type='group-table' component={Paper}>
      <Table>
        <TableHead >
          <TableRow>
            {headers}
          </TableRow>
        </TableHead>
        <TableBody>
          {rows}
        </TableBody>
      </Table>
    </TableContainer>
  );

  const groupContent = (
    <Grid data-type='group-table-grid' container spacing={2}>
      <Grid item xs={12}>
      <Typography variant='h3'></Typography>
      </Grid>
      {groupTable}
    </Grid>
  );

  return (
    <GroupContext.Provider value={{level: groupCtx.level < 4 ? groupCtx.level + 1 : groupCtx.level}}>
      <RowGroupContext.Provider value={true}>
        {groupContent}
      </RowGroupContext.Provider>
    </GroupContext.Provider>
  );

}