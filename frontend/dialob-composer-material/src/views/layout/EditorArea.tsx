import React from 'react';
import { Box } from '@mui/material';
import Editor from '../../components/Editor';

const EditorArea: React.FC = () => {
  return (
    <Box sx={{ p: 2 }}>
      <Editor />
    </Box>
  );
};

export default EditorArea;
