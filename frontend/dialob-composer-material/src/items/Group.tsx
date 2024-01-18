import React from 'react';
import { KeyboardArrowDown, KeyboardArrowRight } from '@mui/icons-material';
import { Box, IconButton, Paper, TableBody, TableCell, TableContainer, TableRow } from '@mui/material';
import { DialobItem, DialobItems, useComposer } from '../dialob';
import { AddItemMenu, ConversionMenu, IdField, Indicators, LabelField, OptionsMenu, StyledTable, VisibilityField } from './ItemComponents';
import { itemFactory } from './ItemFactory';
import { useEditor } from '../editor';
import { useErrorColor } from '../utils/ErrorUtils';


const createChildren = (item: DialobItem, items: DialobItems) => {
  return item.items && item.items
    .map(itemId => items[itemId])
    .map(item => itemFactory(item));
}

const Group: React.FC<{ item: DialobItem, props?: any }> = ({ item, props }) => {
  const { form } = useComposer();
  const { editor } = useEditor();
  const [expanded, setExpanded] = React.useState<boolean>(true);
  const children = createChildren(item, form.data);
  const centeredCellSx = { textAlign: 'center' };
  const errorBorderColor = useErrorColor(editor.errors, item);
  const hasIndicators = item.description || item.valueSetId || item.validations;

  return (
    <>
      <TableContainer component={Paper} sx={{ my: 2, }}>
        <StyledTable errorBorderColor={errorBorderColor}>
          <TableBody>
            <TableRow>
              <TableCell width='5%' sx={centeredCellSx}>
                <IconButton onClick={() => setExpanded(!expanded)}>
                  {expanded ? <KeyboardArrowDown /> : <KeyboardArrowRight />}
                </IconButton>
              </TableCell>
              <TableCell width='20%'>
                <IdField item={item} />
              </TableCell>
              <TableCell>
                <LabelField item={item} />
              </TableCell>
              {hasIndicators && <TableCell width='15%'>
                <Indicators item={item} />
              </TableCell>}
              <TableCell width='10%' sx={centeredCellSx}>
                <ConversionMenu item={item} />
              </TableCell>
              <TableCell width='5%' sx={centeredCellSx}>
                <OptionsMenu item={item} />
              </TableCell>
            </TableRow>
            {expanded &&
              <>
                <TableRow>
                  <TableCell colSpan={5}>
                    <VisibilityField item={item} />
                  </TableCell>
                </TableRow>
                <TableRow>
                  <TableCell colSpan={5}>
                    <Box sx={{ m: 1 }}>
                      {children}
                      <AddItemMenu item={item} />
                    </Box>
                  </TableCell>
                </TableRow>
              </>
            }
          </TableBody>
        </StyledTable>
      </TableContainer>
    </>
  );
};

export { Group };
