import { ItemActionList, ItemActionMultiChoice, ItemActionSurveyGroup } from '@resys/dialob-fill-api/lib/actions';
import React from 'react';
import { useValueSet } from './hooks/useFillValueSet';
import { SessionComponents } from './sessionComponents';
import { Survey } from './Survey';

interface Props {
  item: ItemActionList | ItemActionMultiChoice | ItemActionSurveyGroup;
  components: SessionComponents;
}
export const ValueSetItem: React.FC<Props> = ({ item, components }) => {
  const valueSet = useValueSet(item.valueSetId);
  if(!valueSet) return null;

  if(item.type === 'multichoice') {
    return <components.Multichoice multichoice={item} valueSet={valueSet}/>
  } else if(item.type === 'list') {
    return <components.List list={item} valueSet={valueSet}/>
  } else if(item.type === 'surveygroup') {
    return (
      <components.SurveyGroup surveyGroup={item} valueSet={valueSet}>
        {item.items.map(itemId => (
          <Survey id={itemId} valueSet={valueSet} components={components}/>
        ))}
      </components.SurveyGroup>
    );
  } else {
    const itemProxy: any = item;
    throw new Error(`Unrecognized ValueSetItem with type '${itemProxy.type}'!`);
  }
}
