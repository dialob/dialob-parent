import React from 'react';
import { Box, Card, CardContent, Typography } from '@mui/material';
import { translateErrorMessage, translateErrorType, useEditor } from '../../editor';


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
  const { editor } = useEditor();
  return (
    <Box sx={{ m: 1 }}>
      {editor.errors.map(error => (
        <Card key={error.itemId} sx={{ mb: 2, cursor: 'pointer', ':hover': { backgroundColor: 'uiElements.dark' } }}>
          <CardContent sx={{ borderLeft: 2, borderColor: errorCardBorderColor(error.severity) }}>
            <Typography variant='subtitle1'>{translateErrorType(error)} error</Typography>
            <Typography variant='subtitle2' component='span'>{translateErrorMessage(error)}</Typography>
            {error.itemId && <Typography component='span' variant='subtitle2'> at <b>{error.itemId}</b></Typography>}
          </CardContent>
        </Card>
      ))}
    </Box>
  );
};

export default ErrorPane;
