import React from 'react';
import { ThemeProvider, Container } from '@mui/material';
import { THEMES } from './theme';
import { AppHeader } from './components/AppHeader';

interface AppProps {
  setLocale: (locale: string) => void;
  setThemeIndex: (index: number) => void;
  themeIndex: number;
}

export const App: React.FC<AppProps> = ({ setLocale, setThemeIndex, themeIndex, children }) => {
  return (
      <ThemeProvider theme={THEMES[themeIndex].theme}>
        <AppHeader setLocale={setLocale} themeIndex={themeIndex} setThemeIndex={setThemeIndex} />
        <Container maxWidth='xl'>
          {children || {}}
        </Container>
      </ThemeProvider>
  );
}

export default App;
