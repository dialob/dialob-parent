import React, { useState } from 'react';
import { createRoot } from 'react-dom/client';
import type { DialobAdminConfig } from '../src/types';
import { DialobAdmin } from '../src'
import { Box, CssBaseline, ThemeProvider } from '@mui/material';
import { THEMES } from './theme';
import { AppHeader } from './AppHeader';

const conf: DialobAdminConfig = {
  csrf: {
    key: 'X-CSRF-TOKEN',
    value: '6cf7b545-6d09-4a95-b8ad-85afad13af7c'
  },
  dialobApiUrl: 'http://localhost:8081',
  setLoginRequired: () => console.log("..."),
  setTechnicalError: () => console.log("..."),
  language: "en"
}

const App: React.FC = () => {
  const [themeIndex, setThemeIndex] = useState<number>(1);

  return (
    <React.StrictMode>
      <ThemeProvider theme={THEMES[themeIndex].theme}>
        <CssBaseline />
        <AppHeader themeIndex={themeIndex} setThemeIndex={setThemeIndex} />
        <Box p={2}>
          <DialobAdmin
            config={conf}
          />
        </Box>
      </ThemeProvider>
    </React.StrictMode>
  );
}

const container: any = document.getElementById('root');
const root = createRoot(container);

root.render(
  <App />
);
