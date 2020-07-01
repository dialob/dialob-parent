import React from 'react';
import { Session as DialobSession } from '@resys/dialob-fill-api';
import { Session } from '@resys/dialob-fill-react';
import { IntlProvider } from 'react-intl';
import messages from './intl';

export interface MaterialDialobProps {
  session: DialobSession;
  locale: string;
};

export const MaterialDialob: React.FC<MaterialDialobProps> = ({session, locale, children}) => {
  
  return (
    <Session key={session.id} session={session} locale={locale}>
      <IntlProvider locale={session.getLocale() || locale} messages={messages[locale]}>
        {children}
      </IntlProvider>
    </Session>
  );
}