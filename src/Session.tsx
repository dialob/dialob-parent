import DialobFill, { Config } from '@resys/dialob-fill-api';
import React from 'react';
import { SessionContext } from './context/sessionContext';

export interface SessionProps {
  id: string;
  config: Config;
};
export const Session: React.FC<SessionProps> = ({ id, config, children }) => {
  const session = DialobFill.newSession(id, config);
  session.pull();

  return (
    <SessionContext.Provider value={session}>
      {children}
    </SessionContext.Provider>
  );
}
