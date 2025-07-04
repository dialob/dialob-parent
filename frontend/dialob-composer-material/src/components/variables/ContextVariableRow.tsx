import React from 'react';
import { Box, IconButton, TableCell, TableRow, alpha, useTheme } from '@mui/material';
import { ContextVariable } from '../../types';
import { useEditor } from '../../editor';
import { useErrorColorSx } from '../../utils/ErrorUtils';
import {
  ContextTypeMenu, DefaultValueField, DeleteButton, DescriptionField,
  NameField, PublishedSwitch, UsersField, VariableProps
} from './VariableComponents';
import { ArrowDownward, ArrowUpward } from '@mui/icons-material';
import { isContextVariable } from '../../utils/ItemUtils';
import { useSave } from '../../dialogs/contexts/saving/useSave';


const ContextVariableRow: React.FC<VariableProps> = ({ index, item, onClose }) => {
  const { editor } = useEditor();
  const { savingState, moveVariable } = useSave();
  const theme = useTheme();
  const variable = item as ContextVariable;
  const errorColorSx = useErrorColorSx(editor.errors, variable.name);
  const backgroundColor = errorColorSx ? errorColorSx : theme.palette.background.paper;
  const contextVariables = savingState.variables?.filter(v => isContextVariable(v));
 
  const handleMove = (direction: 'up' | 'down') => {
    const destination = direction === 'up' ? index - 1 : index + 1;
    const destinationVariable = contextVariables?.[destination];
    if (destinationVariable) {
      moveVariable(variable, destinationVariable);
    }
  }

  return (
    <TableRow key={variable.name} sx={{ backgroundColor: alpha(backgroundColor, 0.1) }}>
      <TableCell width='10%' align='center' sx={{ p: 0.5 }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <DeleteButton variable={variable} />
          <IconButton
            sx={{ p: 0.5 }}
            disabled={index === 0}
            onClick={() => handleMove('up')}>
              <ArrowUpward />
            </IconButton>
          <IconButton
            sx={{ p: 0.5 }}
            disabled={index === (contextVariables?.length || 0) - 1}
            onClick={() => handleMove('down')}>
              <ArrowDownward /> 
          </IconButton>
        </Box>
      </TableCell>
      <TableCell width='10%' align='center' sx={{ p: 0.5 }}>
        <PublishedSwitch variable={variable} />
      </TableCell>
      <TableCell width='25%' sx={{ p: 0.5 }}>
        <NameField variable={variable} />
      </TableCell>
      <TableCell width='10%' sx={{ p: 0.5 }}>
        <ContextTypeMenu variable={variable} />
      </TableCell>
      <TableCell width='15%' sx={{ p: 0.5 }}>
        <DefaultValueField variable={variable} />
      </TableCell>
      <TableCell width='20%' sx={{ p: 0.5 }}>
        <DescriptionField variable={variable} />
      </TableCell>
      <TableCell width='10%' align='center' sx={{ p: 0.5 }}>
        <UsersField variable={variable} onClose={onClose} />
      </TableCell>
    </TableRow>
  );
}

export default ContextVariableRow;
