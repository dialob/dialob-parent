import React from 'react';
import { Paper, TableBody, TableCell, TableContainer, TableRow, alpha, useTheme, Box, IconButton, Typography, Button, styled } from '@mui/material';
import { Element } from 'react-scroll';
import { DialobItem, Variable } from '../types';
import { useComposer } from '../dialob';
import { useEditor } from '../editor';
import { useErrorColorSx } from '../utils/ErrorUtils';
import { Delete, Functions } from '@mui/icons-material';
import { StyledTable } from './ItemComponents';
import { FormattedMessage } from 'react-intl';

interface ExpressionVariableProps {
  variableId: string;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  [key: string]: any;
}

const FullWidthButton = styled(Button)(({ theme }) => ({
  padding: theme.spacing(1),
  paddingLeft: theme.spacing(2),
  justifyContent: 'space-between',
  textTransform: 'none',
  width: '100%',
}));

const ExpressionVariable: React.FC<ExpressionVariableProps> = ({ variableId, ...props }) => {
  const theme = useTheme();
  const { form } = useComposer();
  const { editor, setConfirmationDialogType, setConfirmationActiveItem, setActiveVariable } = useEditor();
  const [highlighted, setHighlighted] = React.useState<boolean>(false);
  
  const variable = React.useMemo(() => 
    form.variables?.find(v => v.name === variableId) as Variable | undefined,
    [form.variables, variableId]
  );

  const errorBorderColor = useErrorColorSx(editor.errors, variableId);
  const backgroundColor = errorBorderColor ? 
    alpha(theme.palette.error.main, 0.1) : 
    (highlighted ? alpha(theme.palette.mainContent.contrastText, 0.1) : alpha(theme.palette.info.light, 0.05));
  
  const highlightedSx = highlighted ?
    { border: 1, borderColor: 'mainContent.contrastText', backgroundColor: alpha(theme.palette.mainContent.contrastText, 0.1) } : {};

  React.useEffect(() => {
    if (editor?.highlightedItem?.id === variableId) {
      setHighlighted(true);
    } else {
      setHighlighted(false);
    }
  }, [editor.highlightedItem, variableId]);

  if (!variable) {
    return null;
  }

  const handleDelete = (e: React.MouseEvent<HTMLElement>) => {
    e.stopPropagation();
    // Create a pseudo-item for the confirmation dialog
    const pseudoItem = { id: variableId, type: 'variable' } as DialobItem;
    setConfirmationActiveItem(pseudoItem);
    setConfirmationDialogType('deleteVariable');
  };

  const handleEdit = (e: React.MouseEvent<HTMLElement>, idEditMode: boolean) => {
    e.stopPropagation();
    setActiveVariable(variableId, idEditMode);
  };

  return (
    <Element name={variableId}>
      <TableContainer 
        component={Paper} 
        sx={{ 
          my: 1, 
          ...highlightedSx,
          borderLeft: 3,
          borderColor: 'info.main'
        }} 
        onClick={props?.onClick ? props.onClick : undefined}
      >
        <StyledTable errorBorderColor={errorBorderColor} backgroundColor={backgroundColor}>
          <TableBody>
            <TableRow>
              <TableCell width='5%'>
                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                  <Functions color='info' fontSize='small' />
                </Box>
              </TableCell>
              <TableCell width='25%'>
                <FullWidthButton onClick={(e) => handleEdit(e, true)} variant='text' color='inherit'>
                  <Typography fontFamily='monospace'>
                    {variable.name}
                  </Typography>
                </FullWidthButton>
              </TableCell>
              <TableCell width='65%'>
                <FullWidthButton onClick={(e) => handleEdit(e, false)} variant='text' color='inherit'>
                  <Typography fontFamily='monospace'>
                    {variable.expression || <FormattedMessage id='placeholders.expression.empty' />}
                  </Typography>
                </FullWidthButton>
              </TableCell>
              <TableCell width='5%' sx={{ textAlign: 'center' }}>
                <IconButton size='small' onClick={handleDelete}>
                  <Delete fontSize='small' color='error' />
                </IconButton>
              </TableCell>
            </TableRow>
          </TableBody>
        </StyledTable>
      </TableContainer>
    </Element>
  );
};

export { ExpressionVariable };
