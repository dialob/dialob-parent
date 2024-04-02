import React from 'react';
import { TableCell, TableRow, alpha, useTheme } from '@mui/material';
import { ContextVariable } from '../../dialob';
import { useEditor } from '../../editor';
import { useErrorColorSx } from '../../utils/ErrorUtils';
import { ContextTypeMenu, DefaultValueField, DeleteButton, DescriptionField, NameField, PublishedSwitch, UsersField } from './VariableComponents';

const ContextVariableRow: React.FC<{ variable: ContextVariable, onClose: () => void }> = ({ variable, onClose }) => {
  const { editor } = useEditor();
  const theme = useTheme();
  const errorColorSx = useErrorColorSx(editor.errors, variable.name);
  const backgroundColor = errorColorSx ? errorColorSx : theme.palette.background.paper;

  return (
    <TableRow key={variable.name} sx={{ backgroundColor: alpha(backgroundColor, 0.1) }}>
      <TableCell align='center'>
        <DeleteButton variable={variable} />
      </TableCell>
      <TableCell align='center'>
        <PublishedSwitch variable={variable} />
      </TableCell>
      <TableCell>
        <NameField variable={variable} />
      </TableCell>
      <TableCell>
        <ContextTypeMenu variable={variable} />
      </TableCell>
      <TableCell>
        <DefaultValueField variable={variable} />
      </TableCell>
      <TableCell>
        <DescriptionField variable={variable} />
      </TableCell>
      <TableCell align='center'>
        <UsersField variable={variable} onClose={onClose} />
      </TableCell>
    </TableRow>
  );
}

export default ContextVariableRow;
