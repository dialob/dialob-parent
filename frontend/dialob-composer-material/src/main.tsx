import React from 'react'
import ReactDOM from 'react-dom/client'
import { CssBaseline, ThemeProvider } from '@mui/material'
import { siteTheme } from './theme/siteTheme'
import { AppConfig, DialobComposerConfig } from './backend/types'
import DialobComposer from './dialob/DialobComposer'


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
        <DialobComposer config={DIALOB_COMPOSER_CONFIG} formId={FORM_ID} />
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
