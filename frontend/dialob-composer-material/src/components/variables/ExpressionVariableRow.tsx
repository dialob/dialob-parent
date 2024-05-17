import React from 'react';
import { Alert, Box, IconButton, TableBody, TableCell, TableRow, Typography, alpha, useTheme } from '@mui/material';
import { Variable } from '../../dialob';
import { useEditor } from '../../editor';
import { getErrorSeverity, useErrorColorSx } from '../../utils/ErrorUtils';
import { DeleteButton, DescriptionField, ExpressionField, NameField, PublishedSwitch, UsersField, VariableProps } from './VariableComponents';
import { BorderedTable } from '../TableEditorComponents';
import { Edit, Warning } from '@mui/icons-material';
import { ErrorMessage } from '../ErrorComponents';

const ExpressionVariableRow: React.FC<VariableProps> = ({ item, provided, onClose }) => {
  const { editor } = useEditor();
  const theme = useTheme();
  const variable = item.data.variable as Variable;
  const errorColorSx = useErrorColorSx(editor.errors, variable.name);
  const backgroundColor = errorColorSx ? errorColorSx : theme.palette.background.paper;
  const itemErrors = editor.errors.filter(e => e.itemId === variable.name);
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
              {/* eslint-disable-next-line formatjs/no-literal-string-in-jsx */}
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
        {expanded && <>
          <TableRow>
            <TableCell colSpan={6}>
              <ExpressionField variable={variable} errors={itemErrors} />
            </TableCell>
          </TableRow>
          <TableRow>
            <TableCell colSpan={6}>
              {itemErrors.length > 0 && itemErrors.map((error, index) => <Alert severity={getErrorSeverity(error)} sx={{ mt: 2 }} icon={<Warning />}>
                <Typography key={index} color={error.level.toLowerCase()}><ErrorMessage error={error} /></Typography>
              </Alert>)}
            </TableCell>
          </TableRow>
        </>}
      </TableBody>
    </BorderedTable>
  );
}

export default ExpressionVariableRow;
