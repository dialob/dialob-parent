import { Session as DialobSession } from '@resys/dialob-fill-api';
import React, { useEffect } from 'react';
import { SessionContext } from './context/sessionContext';

export interface SessionProps {
  session: DialobSession;
  locale?: string;
};
export const Session: React.FC<SessionProps> = ({ session, children, locale }) => {
  useEffect(() => {
    session.pull();
  }, [session]);

  useEffect(() => {
    if (locale) {
      session.setLocale(locale);
    }
  }, [locale])

  return (
    <SessionContext.Provider value={session}>
      {children}
    </SessionContext.Provider>
  );
}
