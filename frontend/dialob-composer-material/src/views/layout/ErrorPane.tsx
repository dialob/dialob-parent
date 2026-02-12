import React from 'react';
import { Box, Card, CardActionArea, CardContent, Typography } from '@mui/material';
import { EditorError, useEditor } from '../../editor';
import { useComposer } from '../../dialob';
import { scrollToItem } from '../../utils/ScrollUtils';
import { ErrorMessage, ErrorType } from '../../components/ErrorComponents';
import { BoldedMessage } from '../../intl/BoldedMessage';
import { isContextVariable, isPage } from '../../utils/ItemUtils';
import { parseVariableItemId, parseRowgroupIdFromVariableItemId } from '../../utils/ErrorUtils';
import { DialobItem } from '../../types';


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
  const { editor, setActivePage, setActiveList, setActiveVariableTab, setActiveVariable, setHighlightedItem } = useEditor();
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
        const variableName = parseVariableItemId(error.itemId);
        const rowgroupId = parseRowgroupIdFromVariableItemId(error.itemId);
        
        const variable = form.variables?.find(v => v.name === variableName);
        if (!variable) {
          return;
        }
        
        // Check if it's a context variable
        if (isContextVariable(variable)) {
          setActiveVariableTab('context');
        } else {
          // For expression variables, check if it's scoped to a rowgroup
          // First check if we have a rowgroup ID from the error format
          let parentRowgroup = rowgroupId ? form.data[rowgroupId] : undefined;
          
          // If not found via error format, search for it in all rowgroups
          if (!parentRowgroup) {
            parentRowgroup = Object.values(form.data).find(item => 
              item.type === 'rowgroup' && item.items?.includes(variableName)
            );
          }
          
          if (parentRowgroup) {
            // Scroll to the variable itself and open the expression variable dialog for scoped variables
            const pseudoItem = { id: variableName } as DialobItem;
            setHighlightedItem(pseudoItem);
            scrollToItem(variableName, Object.values(form.data), editor.activePage, setActivePage);
            setActiveVariable(variableName);
          } else {
            // Open the variables dialog for global variables
            setActiveVariableTab('expression');
          }
        }
      } else if (error.itemId.includes('vs') || error.itemId.includes('valueset')) {
        const item = Object.values(form.data).find(item => item.valueSetId === error.itemId);
        if (item) {
          handleScrollTo(item.id);
          setHighlightedItem(form.data[item.id]);
        }
      } else if (isPage(form.data, form.data[error.itemId])) {
        window.scrollTo(0, 0);
        setActivePage(form.data[error.itemId]);
        setHighlightedItem(form.data[error.itemId]);
      } else {
        handleScrollTo(error.itemId);
        setHighlightedItem(form.data[error.itemId]);
      }
    }
  }

  return (
    <Box sx={{ m: 1 }}>
      {editor.errors?.map((error, index) => {
        // For variable errors with rowgroup-scoped format, display the variable name
        const displayItemId = error.type === 'VARIABLE' && error.itemId 
          ? parseVariableItemId(error.itemId) 
          : error.itemId;
        
        return (
          <Card key={index} sx={{ mb: 2 }}>
            <CardActionArea onClick={() => handleClick(error, gvs)}>
              <CardContent sx={{ borderLeft: 2, borderColor: errorCardBorderColor(error.level) }}>
                <Typography variant='subtitle1'><ErrorType error={error} /></Typography>
                <Typography variant='subtitle2' component='span'><ErrorMessage error={error} /></Typography>
                {displayItemId && <Typography component='span' variant='subtitle2'><BoldedMessage id='errors.at' values={{ itemId: displayItemId }} /></Typography>}
              </CardContent>
            </CardActionArea>
          </Card>
        );
      })}
    </Box>
  );
};

export default ErrorPane;
