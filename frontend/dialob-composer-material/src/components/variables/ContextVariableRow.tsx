import React from 'react';
import { Table, TableBody, TableCell, TableRow, alpha, useTheme } from '@mui/material';
import { ContextVariable } from '../../dialob';
import { useEditor } from '../../editor';
import { useErrorColorSx } from '../../utils/ErrorUtils';
import {
  ContextTypeMenu, DefaultValueField, DeleteButton, DescriptionField,
  NameField, PublishedSwitch, UsersField, VariableProps
} from './VariableComponents';
import { BorderedTable } from '../TableEditorComponents';


const ContextVariableRow: React.FC<VariableProps> = ({ item, provided, onClose }) => {
  const { editor } = useEditor();
  const theme = useTheme();
  const variable = item.data.variable as ContextVariable;
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
          <TableCell width='30%' sx={{ p: 1 }}>
            <NameField variable={variable} />
          </TableCell>
          <TableCell width='10%' sx={{ p: 1 }}>
            <ContextTypeMenu variable={variable} />
          </TableCell>
          <TableCell width='20%' sx={{ p: 1 }}>
            <DefaultValueField variable={variable} />
          </TableCell>
          <TableCell width='20%' sx={{ p: 1 }}>
            <DescriptionField variable={variable} />
          </TableCell>
          <TableCell width='8%' align='center' sx={{ p: 1 }}>
            <UsersField variable={variable} onClose={onClose} />
          </TableCell>
        </TableRow>
      </TableBody>
    </BorderedTable>
  );
}

export default ContextVariableRow;
