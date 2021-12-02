import React from 'react';
import { Session as DialobSession, FillError } from '@dialob/fill-api';
import { Session } from '@dialob/fill-react';
import { IntlProvider } from 'react-intl';

import { ConfigContext, DefaultRenderErrors, MarkdownView } from './';
import builtInMessages from './intl';

export interface MaterialDialobProps {
  session: DialobSession;
  locale: string;
  messages?: {[key:string]: string};
  components?: {
    errors?: (items: FillError[]) => React.ReactElement;
    description?: (text: string) => React.ReactElement;
  };
};

export const MaterialDialob: React.FC<MaterialDialobProps> = ({ session, locale, children, components, messages }) => {
  const errors =  (items: FillError[]) => components?.errors ? components.errors(items) : <DefaultRenderErrors errors={items} />;
  const description = (text: string) => components?.description ? components.description(text) : <MarkdownView text={text} />;
        
  return (
    <ConfigContext.Provider value={{errors, description}} >
      <Session key={session.id} session={session} locale={locale}>
        <IntlProvider locale={session.getLocale() || locale} messages={messages === undefined ? builtInMessages[locale]: messages}>
          {children}
        </IntlProvider>
      </Session>
    </ConfigContext.Provider>
  );
}