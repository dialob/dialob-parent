import React, { useState } from 'react';
import ReactDOM from 'react-dom';
import App from './App';
import messages from './intl';
import CssBaseline from '@material-ui/core/CssBaseline';
import { IntlProvider } from 'react-intl';
import DialobFill, { Config, Session as DialobSession } from '@dialob/fill-api';
import { Dialob } from './dialob/Dialob';
import { MapboxContextProvider } from '@resys/mapbox-connector';

const DEFAULT_LOCALE = 'en';

interface AppRootProps {
  session: DialobSession | null;
}

const AppRoot: React.FC<AppRootProps> = ({ session }) => {
  const [locale, setLocale] = useState<string>(DEFAULT_LOCALE);

  const onComplete = (session: DialobSession) => {
    console.log('Session completed callback:', session.id);
  }
  return (
    <>
      <CssBaseline />
      <IntlProvider locale={locale} messages={messages[locale]}>
        <App setLocale={setLocale} >
          <MapboxContextProvider token={process.env.REACT_APP_MAPBOX_TOKEN}>
            <Dialob key={session?.id} session={session} locale={locale} onComplete={onComplete} />
          </MapboxContextProvider>
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
