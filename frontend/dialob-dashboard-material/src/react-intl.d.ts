import * as React from 'react';

declare module 'react-intl' {
  interface IntlProviderProps {
    locale: string;
    defaultLocale?: string;
    messages?: Record<string, string>;
    children?: React.ReactNode;
  }

  export class IntlProvider extends React.Component<IntlProviderProps> { }
}