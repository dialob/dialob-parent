import { Session as DialobSession } from '@resys/dialob-fill-api';
import React, { useEffect } from 'react';
import { SessionContext } from './context/sessionContext';

export interface SessionProps {
  session: DialobSession;
};
export const Session: React.FC<SessionProps> = ({ session, children }) => {
  useEffect(() => {
    session.pull();
  }, [session]);

  return (
    <SessionContext.Provider value={session}>
      {children}
    </SessionContext.Provider>
  );
}
