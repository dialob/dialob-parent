import React from 'react';
import { Alert, Box, IconButton, TableCell, TableRow, Typography, alpha, useTheme } from '@mui/material';
import { Variable } from '../../types';
import { useEditor } from '../../editor';
import { getErrorSeverity, useErrorColorSx } from '../../utils/ErrorUtils';
import { DeleteButton, DescriptionField, ExpressionField, NameField, PublishedSwitch, UsersField, VariableProps } from './VariableComponents';
import { ArrowDownward, ArrowUpward, Edit, Warning } from '@mui/icons-material';
import { ErrorMessage } from '../ErrorComponents';
import { isContextVariable } from '../../utils/ItemUtils';
import { useSave } from '../../dialogs/contexts/saving/useSave';

type ExpandedField = 'name' | 'expression' | 'description' | null;

const ExpressionVariableRow: React.FC<VariableProps> = React.memo(function ExpressionVariableRow({ index, item, onClose }) {
  const { editor } = useEditor();
  const { savingState, moveVariable } = useSave();
  const theme = useTheme();
  const variable = item as Variable;
  const errorColorSx = useErrorColorSx(editor.errors, variable.name);
  const backgroundColor = errorColorSx ? errorColorSx : theme.palette.background.paper;
  const itemErrors = editor.errors?.filter(e => e.itemId === variable.name);
  const [expandedField, setExpandedField] = React.useState<ExpandedField>(null);
  const expressionVariables = savingState.variables?.filter(v => !isContextVariable(v));

  const handleMove = (direction: 'up' | 'down') => {
    const destination = direction === 'up' ? index - 1 : index + 1;
    const destinationVariable = expressionVariables?.[destination];
    if (destinationVariable) {
      moveVariable(variable, destinationVariable);
    }
  }

  const toggleField = (field: ExpandedField) => {
    setExpandedField(prev => prev === field ? null : field);
  };

  const truncate = (value: string | undefined, maxLen = 24) => {
    if (!value) return '';
    return value.length > maxLen ? value.substring(0, maxLen) + '…' : value;
  };

  const expandedBg = expandedField === 'name'
    ? alpha(theme.palette.warning.main, 0.06)
    : expandedField === 'expression'
      ? alpha(theme.palette.primary.main, 0.06)
      : undefined;

  const editBtnSx = (active: boolean, color: string) => ({
    p: 0.5, ml: 0.5,
    ...(active && { border: `1px solid ${color}` }),
  });

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
        <TableCell width='20%' sx={{ p: 1 }}>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <Typography variant='body2' noWrap sx={{ flex: 1 }}>{variable.name}</Typography>
            <IconButton size='small' sx={editBtnSx(expandedField === 'name', theme.palette.warning.main)} onClick={() => toggleField('name')}>
              <Edit fontSize='small' color={expandedField === 'name' ? 'warning' : 'inherit'} />
            </IconButton>
          </Box>
        </TableCell>
        <TableCell width='25%' sx={{ p: 1 }}>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <Typography variant='body2' noWrap sx={{ flex: 1 }}>{truncate(variable.expression)}</Typography>
            <IconButton size='small' sx={editBtnSx(expandedField === 'expression', theme.palette.primary.main)} onClick={() => toggleField('expression')}>
              <Edit fontSize='small' color={expandedField === 'expression' ? 'primary' : 'inherit'} />
            </IconButton>
          </Box>
        </TableCell>
        <TableCell width='20%' sx={{ p: 1 }}>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <Typography variant='body2' noWrap sx={{ flex: 1 }}>{truncate(variable.description)}</Typography>
            <IconButton size='small' sx={editBtnSx(expandedField === 'description', theme.palette.primary.main)} onClick={() => toggleField('description')}>
              <Edit fontSize='small' color='inherit' />
            </IconButton>
          </Box>
        </TableCell>
        <TableCell width='10%' align='center' sx={{ p: 1 }}>
          <UsersField variable={variable} onClose={onClose} />
        </TableCell>
      </TableRow>
      {expandedField && (
        <TableRow>
          <TableCell colSpan={6} sx={{ p: 1, backgroundColor: expandedBg }}>
            {expandedField === 'name' && <NameField variable={variable} />}
            {expandedField === 'expression' && <ExpressionField variable={variable} errors={itemErrors} />}
            {expandedField === 'description' && <DescriptionField variable={variable} />}
          </TableCell>
        </TableRow>
      )}
      {expandedField === 'expression' && itemErrors && itemErrors.length > 0 && (
        <TableRow>
          <TableCell colSpan={6}>
            {itemErrors.map((error, i) => (
              <Alert key={i} severity={getErrorSeverity(error)} sx={{ mt: 1 }} icon={<Warning />}>
                <Typography color={error.level.toLowerCase()}><ErrorMessage error={error} /></Typography>
              </Alert>
            ))}
          </TableCell>
        </TableRow>
      )}
    </>
  );
});

export default ExpressionVariableRow;
