import React from 'react';
import { Box, Card, CardActionArea, CardContent, Typography } from '@mui/material';
import { EditorError, VariableTabType, useEditor } from '../../editor';
import { isContextVariable, useComposer } from '../../dialob';
import { scrollToItem } from '../../utils/ScrollUtils';
import { ErrorMessage, ErrorType } from '../../components/ErrorComponents';
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
  const gvs = form.metadata.composer?.globalValueSets;

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

  const handleClick = (error: EditorError, gvs?: {
    label?: string | undefined;
    valueSetId: string;
  }[]) => {
    if (error.itemId) {
      if (gvs?.map(gvs => gvs.valueSetId).includes(error.itemId)) {
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
      {editor.errors.map((error, index) => (
        <Card key={index} sx={{ mb: 2 }}>
          <CardActionArea onClick={() => handleClick(error, gvs)}>
            <CardContent sx={{ borderLeft: 2, borderColor: errorCardBorderColor(error.level) }}>
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
