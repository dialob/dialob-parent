import React from 'react';
import { Paper, TableBody, TableCell, TableContainer, TableRow, alpha, useTheme } from '@mui/material';
import { Element } from 'react-scroll';
import { DialobItem } from '../types';
import { ConversionMenu, IdField, LabelField, Indicators, OptionsMenu, VisibilityField, StyledTable } from './ItemComponents';
import { useEditor } from '../editor';
import { useErrorColorSx } from '../utils/ErrorUtils';

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const SimpleField: React.FC<{ item: DialobItem } & Record<string, any>> = ({ item, ...props }) => {
  const theme = useTheme();
  const { editor } = useEditor();
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
      <TableContainer component={Paper} sx={{ my: 2, ...highlightedSx }} onClick={props?.onClick ? props.onClick : undefined}>
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
              <TableCell colSpan={hasIndicators ? 6 : 5}>
                <VisibilityField item={item} />
              </TableCell>
            </TableRow>
          </TableBody>
        </StyledTable>
      </TableContainer>
    </Element>
  );
};

export { SimpleField };
