import React, { useContext } from 'react';
import { ItemProps } from './componentTypes';
import { DialobContext } from '../context/DialobContext';
import { Item } from './Item';

export const Choice: React.FC<ItemProps> = ({ item, answerId }) => {
  const dC = useContext(DialobContext);
  const answer = dC.getAnswer(item.id, answerId);
  if (answer === null) { return null; }
  const valueSetEntry = dC.findValueSet(item.valueSetId).entries.find(entry => entry.id === answer);
  return (
    <Item label={dC.getTranslated(item.label)}>
      {dC.getTranslated(valueSetEntry.label)}
    </Item>
  );

}
