import React from 'react';
import { Alert, Box, IconButton, TableCell, TableRow, Tooltip, Typography, alpha, useTheme } from '@mui/material';
import { Edit, PostAdd, Warning } from '@mui/icons-material';
import { FormattedMessage } from 'react-intl';
import { Variable } from '../../types';
import { useEditor } from '../../editor';
import { getErrorSeverity, useErrorColorSx } from '../../utils/ErrorUtils';
import { DeleteButton, DescriptionField, ExpressionField, NameField, PublishedSwitch, UsersField, VariableProps } from './VariableComponents';
import { ErrorMessage } from '../ErrorComponents';
import { DragHandle, DragHandleProps } from '../DragHandle';

type ExpandedField = 'name' | 'expression' | 'description' | null;

const ExpressionVariableRow: React.FC<VariableProps> = React.memo(function ExpressionVariableRow({
  index, item, onClose, onInsertBelow, setNodeRef, style, handleProps,
}) {
  const { editor } = useEditor();
  const theme = useTheme();
  const variable = item as Variable;
  const errorColorSx = useErrorColorSx(editor.errors, variable.name);
  const backgroundColor = errorColorSx ? errorColorSx : theme.palette.background.paper;
  const itemErrors = editor.errors?.filter(e => e.itemId === variable.name);
  const [expandedField, setExpandedField] = React.useState<ExpandedField>(null);

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
      <TableRow
        ref={setNodeRef}
        style={style}
        data-variable-row
        sx={{ backgroundColor: alpha(backgroundColor, 0.1) }}
      >
        <TableCell width='10%' align='center' sx={{ p: 1 }}>
          <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <DeleteButton variable={variable} />
            <Tooltip title={<FormattedMessage id='menus.insert.below' />}>
              <IconButton sx={{ p: 0.5 }} onClick={() => onInsertBelow(index)}>
                <PostAdd color='success' />
              </IconButton>
            </Tooltip>
            <DragHandle {...handleProps as DragHandleProps} />
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
