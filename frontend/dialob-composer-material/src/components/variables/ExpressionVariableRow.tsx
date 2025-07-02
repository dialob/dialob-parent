import React from 'react';
import { Alert, Box, IconButton, TableCell, TableRow, Typography, alpha, useTheme } from '@mui/material';
import { Variable } from '../../types';
import { useEditor } from '../../editor';
import { getErrorSeverity, useErrorColorSx } from '../../utils/ErrorUtils';
import { DeleteButton, DescriptionField, ExpressionField, NameField, PublishedSwitch, UsersField, VariableProps } from './VariableComponents';
import { ArrowDownward, ArrowUpward, Edit, Warning } from '@mui/icons-material';
import { ErrorMessage } from '../ErrorComponents';
import { useComposer } from '../../dialob';
import { isContext } from 'vm';
import { isContextVariable } from '../../utils/ItemUtils';

const ExpressionVariableRow: React.FC<VariableProps> = ({ index, item, onClose }) => {
  const { editor } = useEditor();
  const { form, moveVariable } = useComposer();
  const theme = useTheme();
  const variable = item as Variable;
  const errorColorSx = useErrorColorSx(editor.errors, variable.name);
  const backgroundColor = errorColorSx ? errorColorSx : theme.palette.background.paper;
  const itemErrors = editor.errors?.filter(e => e.itemId === variable.name);
  const [expanded, setExpanded] = React.useState<boolean>(false);
  const expressionVariables = form.variables?.filter(v => !isContextVariable(v));

  const handleMove = (direction: 'up' | 'down') => {
    const destination = direction === 'up' ? index - 1 : index + 1;
    const destinationVariable = expressionVariables?.[destination];
    if (destinationVariable) {
      moveVariable(variable, destinationVariable);
    }
  }

  return (
    <>
      <TableRow key={variable.name} sx={{ backgroundColor: alpha(backgroundColor, 0.1) }}>
        <TableCell width='10%' align='center' sx={{ p: 1 }}>
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
              disabled={index === (expressionVariables?.length || 0) - 1}
              onClick={() => handleMove('down')}>
                <ArrowDownward /> 
            </IconButton>
          </Box>
        </TableCell>
        <TableCell width='10%' align='center' sx={{ p: 1 }}>
          <PublishedSwitch variable={variable} />
        </TableCell>
        <TableCell width='25%' sx={{ p: 1 }}>
          <NameField variable={variable} />
        </TableCell>
        <TableCell width='20%' sx={{ p: 1 }}>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            {variable.expression.substring(0, 20) + (variable.expression.length > 20 ? '...' : '')}
            <IconButton><Edit color={expanded ? 'primary' : 'inherit'} onClick={() => setExpanded(!expanded)} /></IconButton>
          </Box>
        </TableCell>
        <TableCell width='20%' sx={{ p: 1 }}>
          <DescriptionField variable={variable} />
        </TableCell>
        <TableCell width='10%' align='center' sx={{ p: 1 }}>
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
            {itemErrors?.map((error, index) => <Alert key={index} severity={getErrorSeverity(error)} sx={{ mt: 2 }} icon={<Warning />}>
              <Typography color={error.level.toLowerCase()}><ErrorMessage error={error} /></Typography>
            </Alert>)}
          </TableCell>
        </TableRow>
      </>}
    </>
  );
}

export default ExpressionVariableRow;
