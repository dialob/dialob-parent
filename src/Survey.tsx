import { SessionValueSet } from '@resys/dialob-fill-api';
import React from 'react';
import { useFillItem } from './hooks/useFillItem';
import { SessionComponents } from './sessionComponents';

export interface SurveyProps {
  id: string;
  valueSet: SessionValueSet;
  components: SessionComponents;
};
export const Survey: React.FC<SurveyProps> = ({ id, valueSet, components }) => {
  const item = useFillItem(id);
  if(!item || item.type !== 'survey') return null;

  return (
    <components.Survey valueSet={valueSet} survey={item}/>
  );
}
