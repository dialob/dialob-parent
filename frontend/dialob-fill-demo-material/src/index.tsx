import React, { useState } from 'react';
import ReactDOM from 'react-dom';
import App from './App';
import messages from './intl';
import {CssBaseline} from '@mui/material';
import { IntlProvider } from 'react-intl';
import DialobFill, { Config, Session as DialobSession } from '@dialob/fill-api';
import { Dialob } from './dialob/Dialob';

const DEFAULT_LOCALE = 'en';

interface AppRootProps {
  session: DialobSession | null;
}

const AppRoot: React.FC<AppRootProps> = ({ session }) => {
  const [locale, setLocale] = useState<string>(DEFAULT_LOCALE);
  const [themeIndex, setThemeIndex] = useState<number>(1);

  const onComplete = (session: DialobSession) => {
    console.log('Session completed callback:', session.id);
  }

  return (
    <>
      <CssBaseline />
      <IntlProvider locale={locale} messages={messages[locale]}>
        <App setLocale={setLocale} setThemeIndex={setThemeIndex} themeIndex={themeIndex} >
            <Dialob key={session?.id} session={session} locale={locale} onComplete={onComplete} />
        </App>
      </IntlProvider>
    </>
  );
}

const renderDialob = (target: HTMLElement, sessionId: string, config: Config) => {
  const session = sessionId ? DialobFill.newSession(sessionId, config) : null;
  ReactDOM.render(<AppRoot session={session} />, target);
};

// @ts-ignore
window.renderDialob = renderDialob;
