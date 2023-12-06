import React from 'react';

import svLocale from 'date-fns/locale/sv';
import fiLocale from 'date-fns/locale/fi';
import etLocale from 'date-fns/locale/et';
import enLocale from 'date-fns/locale/en-US';


import createSession from './session';
import App from '../src/App';
import { Dialob } from '../src/dialob/Dialob';


const localeMap = {
  en: enLocale,
  et: etLocale,
  fi: fiLocale,
  sv: svLocale,
};
//MMMM dd, yyyy, MMMM dd, yyyy

import { IntlProvider } from 'react-intl'

const locale = 'en';

export default { title: 'Material Components' };


export const DialobSession = () => {
  const [locale, setLocale] = React.useState<string>("en");
  const onComplete = (session: any) => {
    console.log('Session completed callback:', session.id);
  }

  const session = createSession();

  return (
    <IntlProvider locale={locale}>
        <Dialob key={session?.id} session={session} locale={locale} onComplete={onComplete} />
    </IntlProvider>
  );
}

