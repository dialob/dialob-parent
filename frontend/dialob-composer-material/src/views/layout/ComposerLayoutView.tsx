import React from 'react';
import { Drawer, Box, useTheme, CSSObject, Container } from '@mui/material';
import MenuBar from './MenuBar';
import NavigationPane from './NavigationPane';
import EditorArea from './EditorArea';
import ErrorPane from './ErrorPane';

const ComposerLayoutView: React.FC = () => {
  const theme = useTheme();
  const menuHeight = (theme.components?.MuiStack?.styleOverrides?.root as CSSObject)?.height;

  return (
    <Box display='flex'>
      <MenuBar />
      <Drawer variant="permanent">
        <Box sx={{ mt: `${menuHeight}px` }}>
          <NavigationPane />
        </Box>
      </Drawer>
      <Container>
        <Box sx={{ mt: `${menuHeight}px` }}>
          <EditorArea />
        </Box>
      </Container>
      <Drawer variant="permanent" anchor="right">
        <Box sx={{ mt: `${menuHeight}px` }}>
          <ErrorPane />
        </Box>
      </Drawer>
    </Box>
  );
};

export default ComposerLayoutView;
