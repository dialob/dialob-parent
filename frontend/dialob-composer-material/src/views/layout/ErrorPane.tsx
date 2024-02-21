import React from 'react';
import { Box, Card, CardActionArea, CardContent, Typography } from '@mui/material';
import { useEditor } from '../../editor';
import { ErrorMessage, ErrorType } from '../../utils/ErrorUtils';
import { useComposer } from '../../dialob';
import { scrollToItem } from '../../utils/ScrollUtils';


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
  const { editor, setActivePage } = useEditor();
  const { form } = useComposer();

  const handleScrollTo = (itemId?: string) => {
    if (!itemId) {
      return;
    }
    scrollToItem(itemId, Object.values(form.data), editor.activePage, setActivePage);
  }

  return (
    <Box sx={{ m: 1 }}>
      {editor.errors.map(error => (
        <Card key={error.itemId} sx={{ mb: 2 }}>
          <CardActionArea onClick={() => handleScrollTo(error.itemId)}>
            <CardContent sx={{ borderLeft: 2, borderColor: errorCardBorderColor(error.severity) }}>
              <Typography variant='subtitle1'><ErrorType error={error} /></Typography>
              <Typography variant='subtitle2' component='span'><ErrorMessage error={error} /></Typography>
              {error.itemId && <Typography component='span' variant='subtitle2'> at <b>{error.itemId}</b></Typography>}
            </CardContent>
          </CardActionArea>
        </Card>
      ))}
    </Box>
  );
};

export default ErrorPane;
