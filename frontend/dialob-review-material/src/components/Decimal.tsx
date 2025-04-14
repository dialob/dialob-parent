import React, { useContext } from 'react';
import { useIntl } from 'react-intl';
import { ItemProps } from './componentTypes';
import { DialobContext } from '../context/DialobContext';
import { Item } from './Item';

export const Decimal: React.FC<ItemProps> = ({ item, answerId }) => {
  const dC = useContext(DialobContext);
  const intl = useIntl();
  const answer = dC.getAnswer(item.id, answerId);
  if (answer === null) { return null; }
  return (
    <Item label={dC.getTranslated(item.label)}>
      <React.Fragment>
        {intl.formatNumber(answer)}
      </React.Fragment>
    </Item>
  );

}
