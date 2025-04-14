import React, { useContext } from 'react';
import { ItemProps } from './componentTypes';
import { DialobContext } from '../context/DialobContext';
import { Item } from './Item';

export const Text: React.FC<ItemProps> = ({ item, answerId }) => {
  const dC = useContext(DialobContext);
  const answer = dC.getAnswer(item.id, answerId);
  if (answer === null) { return null; }
  return (
    <Item label={dC.getTranslated(item.label)}>
      {answer}
    </Item>
  );

}
