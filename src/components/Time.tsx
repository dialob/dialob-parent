import React, { useContext } from 'react';
import { ItemProps } from './componentTypes';
import { DialobContext } from '../context/DialobContext';
import { FormattedTime } from 'react-intl';
import moment from 'moment';
import { Item } from './Item';

export const Time: React.FC<ItemProps> = ({ item, answerId }) => {
  const dC = useContext(DialobContext);
  const answer = dC.getAnswer(item.id, answerId);
  if (answer === null) { return null; }
  return (
    <Item label={dC.getTranslated(item.label)}>
      <FormattedTime value={moment(answer, 'HH:mm').toDate()} />
    </Item>
  );

}