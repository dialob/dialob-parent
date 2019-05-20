import React, { useEffect, useState } from 'react';
import { useFillSession } from './hooks/useFillSession';
import { Item } from './Item';
import { SessionComponents } from './sessionComponents';
import { SessionQuestionnaire } from '@resys/dialob-fill-api';

export interface QuestionnaireProps {
  components: SessionComponents;
};
export const Questionnaire: React.FC<QuestionnaireProps> = ({ components }) => {
  const session = useFillSession();
  const [questionnaire, setQuestionnaire] = useState<SessionQuestionnaire | undefined>(session.getQuestionnaire());

  useEffect(() => {
    const listener = () => {
      const newQuestionnaire = session.getQuestionnaire();
      setQuestionnaire(newQuestionnaire);
    };
    session.on('update', listener);

    return () => {
      session.removeListener('update', listener);
    }
  }, [session]);
  
  return (
    <components.Questionnaire questionnaire={questionnaire}>
      {questionnaire && <Item key={questionnaire.activeItem} id={questionnaire.activeItem} components={components}/>}
    </components.Questionnaire>
  );
}
