import React from 'react';
import { TableBody, TableCell, TableRow, alpha, useTheme } from '@mui/material';
import { Variable } from '../../dialob';
import { useEditor } from '../../editor';
import { useErrorColorSx } from '../../utils/ErrorUtils';
import { DeleteButton, DescriptionField, ExpressionField, NameField, PublishedSwitch, UsersField, VariableProps } from './VariableComponents';
import { BorderedTable } from '../TableEditorComponents';

const ExpressionVariableRow: React.FC<VariableProps> = ({ item, provided, onClose }) => {
  const { editor } = useEditor();
  const theme = useTheme();
  const variable = item.data.variable as Variable;
  const errorColorSx = useErrorColorSx(editor.errors, variable.name);
  const backgroundColor = errorColorSx ? errorColorSx : theme.palette.background.paper;

  return (
    <BorderedTable sx={{ tableLayout: 'fixed' }}
      ref={provided.innerRef}
      {...provided.draggableProps}
      {...provided.dragHandleProps}>
      <TableBody>
        <TableRow key={variable.name} sx={{ backgroundColor: alpha(backgroundColor, 0.1) }}>
          <TableCell width='5%' align='center' sx={{ p: 1 }}>
            <DeleteButton variable={variable} />
          </TableCell>
          <TableCell width='7%' align='center' sx={{ p: 1 }}>
            <PublishedSwitch variable={variable} />
          </TableCell>
          <TableCell width='25%' sx={{ p: 1 }}>
            <NameField variable={variable} />
          </TableCell>
          <TableCell width='35%' sx={{ p: 1 }}>
            <ExpressionField variable={variable} />
          </TableCell>
          <TableCell width='20%' sx={{ p: 1 }}>
            <DescriptionField />
          </TableCell>
          <TableCell width='8%' align='center' sx={{ p: 1 }}>
            <UsersField variable={variable} onClose={onClose} />
          </TableCell>
        </TableRow>
      </TableBody>
    </BorderedTable>
  );
}

export default ExpressionVariableRow;
