import DialobFill, { Config } from '@resys/dialob-fill-api';
import React from 'react';
import { SessionContext } from './context/sessionContext';
import { Questionnaire } from './Questionnaire';
import { SessionComponents } from './sessionComponents';

export interface SessionProps {
  id: string;
  config: Config;
  components: SessionComponents;
};
export const Session: React.FC<SessionProps> = ({ id, config, components }) => {
  const session = DialobFill.newSession(id, config);
  session.pull();

  return (
    <SessionContext.Provider value={session}>
      <Questionnaire components={components}/>
    </SessionContext.Provider>
  );
}
