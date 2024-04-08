import React from 'react';
import { Box, IconButton, TableBody, TableCell, TableRow, alpha, useTheme } from '@mui/material';
import { Variable } from '../../dialob';
import { useEditor } from '../../editor';
import { useErrorColorSx } from '../../utils/ErrorUtils';
import { DeleteButton, DescriptionField, ExpressionField, NameField, PublishedSwitch, UsersField, VariableProps } from './VariableComponents';
import { BorderedTable } from '../TableEditorComponents';
import { Edit } from '@mui/icons-material';

const ExpressionVariableRow: React.FC<VariableProps> = ({ item, provided, onClose }) => {
  const { editor } = useEditor();
  const theme = useTheme();
  const variable = item.data.variable as Variable;
  const errorColorSx = useErrorColorSx(editor.errors, variable.name);
  const backgroundColor = errorColorSx ? errorColorSx : theme.palette.background.paper;
  const [expanded, setExpanded] = React.useState<boolean>(false);

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
          <TableCell width='33%' sx={{ p: 1 }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              {variable.expression.substring(0, 45) + (variable.expression.length > 45 ? '...' : '')}
              <IconButton><Edit color={expanded ? 'primary' : 'inherit'} onClick={() => setExpanded(!expanded)} /></IconButton>
            </Box>
          </TableCell>
          <TableCell width='25%' sx={{ p: 1 }}>
            <DescriptionField />
          </TableCell>
          <TableCell width='5%' align='center' sx={{ p: 1 }}>
            <UsersField variable={variable} onClose={onClose} />
          </TableCell>
        </TableRow>
        {expanded && <TableRow>
          <TableCell colSpan={6}>
            <ExpressionField variable={variable} />
          </TableCell>
        </TableRow>}
      </TableBody>
    </BorderedTable>
  );
}

export default ExpressionVariableRow;
