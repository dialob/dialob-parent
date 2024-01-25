import { Session as DialobSession } from '@dialob/fill-api';
import React, { PropsWithChildren, useEffect, useMemo } from 'react';
import { SessionContext } from './context/sessionContext';

export interface SessionProps {
  session: DialobSession;
  locale?: string;
};
export const Session: React.FC<PropsWithChildren<SessionProps>> = ({ session, children, locale }) => {
  useEffect(() => {
    session.pull();
  }, [session]);

  useEffect(() => {
    if (locale) {
      session.setLocale(locale);
    }
  }, [locale]);

  const actions = useMemo(() => {
    return {
      setAnswer: session.setAnswer.bind(session),
      addRowToGroup: session.addRowToGroup.bind(session),
      deleteRow: session.deleteRow.bind(session),
      complete: session.complete.bind(session),
      next: session.next.bind(session),
      previous: session.previous.bind(session),
      goToPage: session.goToPage.bind(session),
      on: session.on.bind(session),
      removeListener: session.removeListener.bind(session),
    };
  }, [session]);

  return (
    <SessionContext.Provider value={{ session, actions }}>
      {children}
    </SessionContext.Provider>
  );
}
