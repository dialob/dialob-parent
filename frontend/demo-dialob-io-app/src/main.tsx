import React from 'react';
import ReactDOM from 'react-dom/client';
import { CssBaseline, ThemeProvider } from '@mui/material';
import App from './App';
import { TenantProvider } from './context/TenantContext';
import { siteTheme } from './theme/siteTheme';
import { AppConfig } from './types';
import { IntlProvider } from 'react-intl';
import messages from './intl';

const renderDialobApp = (targetElement: HTMLElement, appConfig: AppConfig) => {

  ReactDOM.createRoot(targetElement).render(
    <React.StrictMode>
      <ThemeProvider theme={siteTheme}>
        <CssBaseline />
        <TenantProvider appConfig={appConfig}>
          <IntlProvider locale="en" messages={messages['en']}>
            <App appConfig={appConfig} />
          </IntlProvider>
        </TenantProvider>
      </ThemeProvider>
    </React.StrictMode>
  );
};

declare global {
  interface Window {
    renderDialobApp: (targetElement: HTMLElement, appConfig: AppConfig) => void;
  }
}

window.renderDialobApp = renderDialobApp;