import React from 'react';
import { Box } from '@mui/material';
import { SortableTree } from './SortableTree';

const NavigationTreeView: React.FC = () => {

  return (
    <Box sx={{ p: 1 }}>
      <SortableTree />
    </Box>
  )
};

export default NavigationTreeView;
