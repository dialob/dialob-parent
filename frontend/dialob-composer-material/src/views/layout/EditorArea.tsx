import React from 'react';
import DebugFormView from '../DebugFormView';
import { Box } from '@mui/material';

const EditorArea: React.FC = () => {
  return (
    <Box sx={{ pt: 2 }}>
      <DebugFormView />
    </Box>
  );
};

export default EditorArea;
