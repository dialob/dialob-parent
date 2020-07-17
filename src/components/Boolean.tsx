import React, { useContext } from 'react';
import { ItemProps } from './componentTypes';
import { DialobContext } from '../context/DialobContext';
import { useIntl } from 'react-intl';
import { Item } from './Item';

export const Boolean: React.FC<ItemProps> = ({ item, answerId }) => {
  const dC = useContext(DialobContext);
  const answer = dC.getAnswer(item.id, answerId);
  const intl = useIntl();
  if (answer === null) { return null; }

  return (
    <Item label={dC.getTranslated(item.label)}>
      <span>{intl.formatMessage({id: answer === true ? 'booleanValue.true': 'booleanValue.false'})}</span>
    </Item>
  );

}