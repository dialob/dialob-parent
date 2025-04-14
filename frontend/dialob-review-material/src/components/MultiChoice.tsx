import React, { useContext } from 'react';
import { ListItem, ListItemIcon, ListItemText, List } from '@mui/material';
import { CheckOutlined, RemoveOutlined } from '@mui/icons-material/';
import { ItemProps } from './componentTypes';
import { DialobContext } from '../context/DialobContext';
import { Item } from './Item';


export const MultiChoice: React.FC<ItemProps> = ({ item, answerId }) => {
  const dC = useContext(DialobContext);
  const answer = dC.getAnswer(item.id, answerId);
  if (answer === null || answer.length === 0) { return null; }
  const valueSet = dC.findValueSet(item.valueSetId)

  const renderEntry = (entry, answered) => {
    return (
      <ListItem key={entry.id}>
        {answered ?
          <ListItemIcon><CheckOutlined /></ListItemIcon>
          : <ListItemIcon><RemoveOutlined /></ListItemIcon>}
        <ListItemText>{dC.getTranslated(entry.label)}</ListItemText>
      </ListItem>)
  }

  const answeredEntries = valueSet.entries.filter(entry => answer.includes(entry.id)).map(entry => renderEntry(entry, true));
  const notAnsweredEntries = valueSet.entries.filter(entry => !answer.includes(entry.id)).map(entry => renderEntry(entry, false));

  return (
    <Item label={dC.getTranslated(item.label)}>
      <List dense>
        {answeredEntries}
        {notAnsweredEntries}
      </List>
    </Item>
  );

}
