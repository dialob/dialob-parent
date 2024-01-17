import React from 'react';
import { Paper, TableBody, TableCell, TableContainer, TableRow } from '@mui/material';
import { DialobItem } from '../dialob';
import { ConversionMenu, IdField, LabelField, Indicators, OptionsMenu, VisibilityField, StyledTable } from './ItemComponents';
import { useEditor, getErrorColor } from '../editor';


const SimpleField: React.FC<{ item: DialobItem, props?: any }> = ({ item, props }) => {
  const { editor } = useEditor();
  const centeredCellSx = { textAlign: 'center' };
  const errorBorderColor = getErrorColor(editor.errors, item);
  const hasIndicators = item.description || item.valueSetId || item.validations;

  return (
    <>
      <TableContainer component={Paper} sx={{ my: 2 }}>
        <StyledTable errorBorderColor={errorBorderColor}>
          <TableBody>
            <TableRow>
              <TableCell width='20%'>
                <IdField item={item} />
              </TableCell>
              <TableCell>
                <LabelField item={item} />
              </TableCell>
              {hasIndicators && <TableCell width='15%'>
                <Indicators item={item} />
              </TableCell>}
              <TableCell width='15%' sx={centeredCellSx}>
                <ConversionMenu item={item} />
              </TableCell>
              <TableCell width='5%' sx={centeredCellSx}>
                <OptionsMenu item={item} />
              </TableCell>
            </TableRow>
            <TableRow>
              <TableCell colSpan={5}>
                <VisibilityField item={item} />
              </TableCell>
            </TableRow>
          </TableBody>
        </StyledTable>
      </TableContainer>
    </>
  );
};

export { SimpleField };
