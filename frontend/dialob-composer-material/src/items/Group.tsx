import React from 'react';
import { KeyboardArrowDown, KeyboardArrowRight } from '@mui/icons-material';
import { Box, IconButton, Paper, TableBody, TableCell, TableContainer, TableRow, alpha, styled, useTheme } from '@mui/material';
import { Element } from 'react-scroll';
import { useComposer } from '../dialob';
import { DialobItem, DialobItems } from '../types';
import { AddItemMenu, ConversionMenu, IdField, Indicators, LabelField, OptionsMenu, StyledTable, VisibilityField } from './ItemComponents';
import { itemFactory } from './ItemFactory';
import { useEditor } from '../editor';
import { useErrorColorSx } from '../utils/ErrorUtils';
import { ItemConfig } from '../defaults/types';
import { useBackend } from '../backend/useBackend';


const createChildren = (item: DialobItem, items: DialobItems, itemConfig: ItemConfig) => {
  return item.items && item.items
    .map(itemId => items[itemId])
    .map(item => itemFactory(item, itemConfig));
}

const GroupPaper = styled(Paper)(({ theme }) => ({
  border: 1,
  borderColor: theme.palette.grey[500],
  borderStyle: 'solid',
  borderRadius: 4
}));

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const Group: React.FC<{ item: DialobItem } & Record<string, any>> = ({ item, ...props }) => {
  const theme = useTheme();
  const { form } = useComposer();
  const { editor } = useEditor();
  const { config } = useBackend();
  const [expanded, setExpanded] = React.useState<boolean>(true);
  const children = createChildren(item, form.data, config.itemEditors);
  const centeredCellSx = { textAlign: 'center' };
  const errorBorderColor = useErrorColorSx(editor.errors, item.id);
  const hasIndicators = item.description || item.valueSetId || item.validations || item.required || item.defaultValue;
  const [highlighted, setHighlighted] = React.useState<boolean>(false);
  const highlightedSx = highlighted ?
    { border: 1, borderColor: 'mainContent.contrastText', backgroundColor: alpha(theme.palette.mainContent.contrastText, 0.1) } : {};

  React.useEffect(() => {
    if (editor?.highlightedItem?.id === item.id) {
      setHighlighted(true);
    } else {
      setHighlighted(false);
    }
  }, [editor.highlightedItem, item.id])

  return (
    <Element name={item.id}>
      <TableContainer component={GroupPaper} sx={{ my: 2, ...highlightedSx }} onClick={props?.onClick ? props.onClick : undefined}>
        <StyledTable errorBorderColor={errorBorderColor}>
          <TableBody>
            <TableRow>
              <TableCell width='5%' sx={centeredCellSx}>
                <IconButton onClick={() => setExpanded(!expanded)}>
                  {expanded ? <KeyboardArrowDown /> : <KeyboardArrowRight />}
                </IconButton>
              </TableCell>
              <TableCell width='20%'>
                <IdField item={item} group />
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
                  <TableCell colSpan={hasIndicators ? 6 : 5}>
                    <VisibilityField item={item} />
                  </TableCell>
                </TableRow>
                <TableRow>
                  <TableCell colSpan={hasIndicators ? 6 : 5}>
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
    </Element>
  );
};

export { Group };
