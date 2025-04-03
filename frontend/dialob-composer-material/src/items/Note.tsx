import React from 'react';
import { Paper, Table, TableBody, TableCell, TableContainer, TableRow, alpha, useTheme } from '@mui/material';
import { Element } from 'react-scroll';
import { DialobItem } from '../dialob';
import { IdField, Indicators, NoteField, OptionsMenu } from './ItemComponents';
import { useEditor } from '../editor';

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const Note: React.FC<{ item: DialobItem } & Record<string, any>> = ({ item, ...props }) => {
  const theme = useTheme();
  const { editor } = useEditor();
  const centeredCellSx = { textAlign: 'center' };
  const hasIndicators = item.description || item.valueSetId || item.validations || item.required || item.defaultValue || item.activeWhen;
  const [highlighted, setHighlighted] = React.useState<boolean>(false);
  const highlightedSx = highlighted ?
    { border: 1, borderColor: 'mainContent.contrastText', backgroundColor: alpha(theme.palette.mainContent.contrastText, 0.1) } : {};

  React.useEffect(() => {
    if (editor?.highlightedItem?.id === item.id) {
      setHighlighted(true);
    }
    const id = setTimeout(() => {
      setHighlighted(false);
    }, 3000);
    return () => clearTimeout(id);
  }, [editor.highlightedItem, item.id])

  return (
    // @ts-ignore
    <Element name={item.id}>
      <TableContainer component={Paper} sx={{ my: 2, ...highlightedSx }} onClick={props?.onClick ? props.onClick : undefined}>
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
              <TableCell colSpan={hasIndicators ? 6 : 5}>
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
