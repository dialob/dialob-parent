import DialobFill, { Config, SessionOptions } from '@resys/dialob-fill-api';
import React from 'react';
import { SessionContext } from './context/sessionContext';

export interface SessionProps {
  id: string;
  config: Config;
  options?: SessionOptions;
};
export const Session: React.FC<SessionProps> = ({ id, config, options, children }) => {
  const session = DialobFill.newSession(id, config, options);
  session.pull();

  return (
    <SessionContext.Provider value={session}>
      {children}
    </SessionContext.Provider>
  );
}
