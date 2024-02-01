import React from 'react';
import { Paper, Table, TableBody, TableCell, TableContainer, TableRow } from '@mui/material';
import { Element } from 'react-scroll';
import { DialobItem } from '../dialob';
import { IdField, Indicators, NoteField, OptionsMenu } from './ItemComponents';

const Note: React.FC<{ item: DialobItem, props?: any }> = ({ item, props }) => {
  const centeredCellSx = { textAlign: 'center' };
  const hasIndicators = item.description || item.valueSetId || item.validations;

  return (
    <Element name={item.id}>
      <TableContainer component={Paper} sx={{ my: 2 }}>
        <Table>
          <TableBody>
            <TableRow>
              <TableCell>
                <IdField item={item} />
              </TableCell>
              {hasIndicators && <TableCell width='15%'>
                <Indicators item={item} />
              </TableCell>}
              <TableCell width='5%' sx={centeredCellSx}>
                <OptionsMenu item={item} />
              </TableCell>
            </TableRow>
            <TableRow>
              <TableCell colSpan={5}>
                <NoteField item={item} />
              </TableCell>
            </TableRow>
          </TableBody>
        </Table>
      </TableContainer>
    </Element>
  );
};

export { Note };
