import React from 'react'
import ReactDOM from 'react-dom/client'
import App  from './App'
import { CssBaseline, ThemeProvider } from '@mui/material'
import { siteTheme } from './theme/siteTheme'
import { BackendProvider, DialobComposerConfig } from './backend/BackendContext'



const renderDialobComposer = (targetElement: HTMLElement, appConfig: any) => { // TODO typings?

  const FORM_ID = appConfig.formId;

  const DIALOB_COMPOSER_CONFIG: DialobComposerConfig = {
  transport: {
    csrf: appConfig.csrfHeader ? {
      headerName: appConfig.csrfHeader,
      token: appConfig.csrf
    } : undefined,
    apiUrl: appConfig.backend_api_url,
    // previewUrl: appConfig.filling_app_url,
    tenantId: appConfig.tenantId || undefined,
    credentialMode: appConfig.credentialMode || undefined
  },
 
 // closeHandler : () => window.location.href = appConfig.adminAppUrl
};

  ReactDOM.createRoot(targetElement!).render(
    <React.StrictMode>
      <ThemeProvider theme={siteTheme}>
        <CssBaseline />
        <BackendProvider config={DIALOB_COMPOSER_CONFIG.transport} formId={FORM_ID}>
          <App />
        </BackendProvider>
      </ThemeProvider>
    </React.StrictMode>,
  )

};

// @ts-ignore
window.renderDialobComposer = renderDialobComposer;
