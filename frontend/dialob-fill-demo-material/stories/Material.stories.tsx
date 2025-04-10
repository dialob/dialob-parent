import React from 'react';
import createSession from './session';
import { Dialob } from '../src/dialob/Dialob';


import { IntlProvider } from 'react-intl'
import { Session } from '@dialob/fill-api';


export default { title: 'Material Components' };

export const DialobSession = () => {
  const [locale,] = React.useState<string>("en");
  const onComplete = (session: Session) => {
    console.log('Session completed callback:', session.id);
  }

  const session = createSession();

  return (
    <IntlProvider locale={locale}>
      <Dialob key={session?.id} session={session} locale={locale} onComplete={onComplete} />
    </IntlProvider>
  );
}

