import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App'
import { CssBaseline, ThemeProvider } from '@mui/material'
import { siteTheme } from './theme/siteTheme'
import { BackendProvider } from './backend/BackendContext'
import { EditorProvider } from './editor'
import { AppConfig, DialobComposerConfig } from './backend/types'


const renderDialobComposer = (targetElement: HTMLElement, appConfig: AppConfig) => {

  const FORM_ID = appConfig.formId;

  const baseUrl = window.location.origin;

  const DIALOB_COMPOSER_CONFIG: DialobComposerConfig = {
    transport: {
      csrf: appConfig.csrfHeader ? {
        headerName: appConfig.csrfHeader,
        token: appConfig.csrf
      } : undefined,
      apiUrl: appConfig.backend_api_url.includes('://') ? appConfig.backend_api_url : baseUrl + appConfig.backend_api_url,
      previewUrl: appConfig.filling_app_url,
      tenantId: appConfig.tenantId || undefined,
      credentialMode: appConfig.credentialMode || undefined,
    },
    documentationUrl: 'https://github.com/dialob/dialob-parent/wiki/',
    closeHandler: () => window.location.href = appConfig.adminAppUrl,
  };

  ReactDOM.createRoot(targetElement!).render(
    <React.StrictMode>
      <ThemeProvider theme={siteTheme}>
        <CssBaseline />
        <BackendProvider config={DIALOB_COMPOSER_CONFIG} formId={FORM_ID}>
          <EditorProvider>
            <App />
          </EditorProvider>
        </BackendProvider>
      </ThemeProvider>
    </React.StrictMode>,
  )
};

declare global {
  interface Window {
    renderDialobComposer: (targetElement: HTMLElement, appConfig: AppConfig) => void;
  }
}

window.renderDialobComposer = renderDialobComposer;
