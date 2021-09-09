import React from 'react';
import { ThemeProvider, Container } from '@material-ui/core';
import { StyledEngineProvider } from '@material-ui/core/styles';
import { appTheme } from './theme';
import { AppHeader } from './components/AppHeader';

interface AppProps {
  setLocale: (locale: string) => void;
}

export const App: React.FC<AppProps> = ({ setLocale, children }) => {
  //const basename = process.env.REACT_APP_BASENAME || '/';
  return (
    <StyledEngineProvider injectFirst>
      <ThemeProvider theme={appTheme}>
        <AppHeader setLocale={setLocale} />
        <Container>
          {children || {}}
        </Container>
      </ThemeProvider>
    </StyledEngineProvider>
  );
}

export default App;
