import React from 'react';
import { useFillItem } from './hooks/useFillItem';
import { SessionComponents } from './sessionComponents';
import { ValueSetItem } from './ValueSetItem';

export interface ItemProps {
  id: string;
  components: SessionComponents;
};
export const Item: React.FC<ItemProps> = ({ id, components }) => {
  const item = useFillItem(id);
  if(!item) return null;
  if(item.type === 'group') {
    return (
      <components.Group group={item}>
        {item.items && item.items.map(itemId => {
          return <Item key={itemId} id={itemId} components={components}/>
        })}
      </components.Group>
    )
  } else if(item.type === 'boolean') {
    return <components.Boolean boolean={item}/>
  } else if(item.type === 'questionnaire') {
    return <components.Questionnaire questionnaire={item}/>
  } else if(item.type === 'text') {
    return <components.Text text={item}/>
  } else if(item.type === 'number') {
    return <components.Number number={item}/>
  } else if(item.type === 'note') {
    return <components.Note note={item}/>
  } else if(item.type === 'multichoice' || item.type === 'list' || item.type === 'surveygroup') {
    return <ValueSetItem item={item} components={components}/>
  } else if(item.type === 'survey') {
    throw new Error(`Surveys should not be handled through Item, as they depend on the encompassing
    SurveyGroup and should never be rendered as separate entities`);
  } else {
    const itemProxy: any = item;
    throw new Error(`Unknown item type '${itemProxy.type}'!`);
  }
}
