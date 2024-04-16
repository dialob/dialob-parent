import React from 'react';
import { Box, Card, CardActionArea, CardContent, Typography } from '@mui/material';
import { EditorError, VariableTabType, useEditor } from '../../editor';
import { isContextVariable, useComposer } from '../../dialob';
import { scrollToItem } from '../../utils/ScrollUtils';
import { ErrorMessage, ErrorType } from '../../components/ErrorComponents';
import { FormattedMessage } from 'react-intl';
import { BoldedMessage } from '../../utils/LocalizationUtils';


const errorCardBorderColor = (severity: string) => {
  switch (severity) {
    case 'ERROR':
      return 'error.main';
    case 'WARNING':
      return 'warning.main';
    default:
      return 'info.main';
  }
};

const ErrorPane: React.FC = () => {
  const { editor, setActivePage, setActiveList, setActiveVariableTab } = useEditor();
  const { form } = useComposer();

  const handleScrollTo = (itemId?: string) => {
    if (!itemId) {
      return;
    }
    scrollToItem(itemId, Object.values(form.data), editor.activePage, setActivePage);
  }

  const handleEditList = (listId?: string) => {
    if (listId) {
      setActiveList(listId);
    }
  }

  const handleClick = (error: EditorError) => {
    if (error.itemId) {
      if (error.itemId.startsWith('vs')) {
        handleEditList(error.itemId);
      } else if (error.type === 'VARIABLE') {
        const variable = form.variables?.find(v => v.name === error.itemId);
        if (!variable) {
          return;
        }
        const type: VariableTabType = isContextVariable(variable) ? 'context' : 'expression';
        setActiveVariableTab(type);
      } else {
        handleScrollTo(error.itemId);
      }
    }
  }

  return (
    <Box sx={{ m: 1 }}>
      {editor.errors.map(error => (
        <Card key={error.itemId} sx={{ mb: 2 }}>
          <CardActionArea onClick={() => handleClick(error)}>
            <CardContent sx={{ borderLeft: 2, borderColor: errorCardBorderColor(error.severity) }}>
              <Typography variant='subtitle1'><ErrorType error={error} /></Typography>
              <Typography variant='subtitle2' component='span'><ErrorMessage error={error} /></Typography>
              {error.itemId && <Typography component='span' variant='subtitle2'><BoldedMessage id='errors.at' values={{ itemId: error.itemId }} /></Typography>}
            </CardContent>
          </CardActionArea>
        </Card>
      ))}
    </Box>
  );
};

export default ErrorPane;
