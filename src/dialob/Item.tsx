import { useFillItem } from '@dialob/fill-react';
import React, { useContext } from 'react';
import { 
  Page, Group, Text, Number, TextBox, BooleanCheckbox, 
  BooleanRadio, Choice, MultiChoiceAC, MultiChoice, Note, 
  DateField, TimeField, RowGroup, Row, SurveyGroup 
} from '@dialob/fill-material';

//import { Address } from '@resys/dialob-fill-material-item-address';
import { MapboxContext } from '@resys/mapbox-connector';

export interface ItemProps {
  id: string;
  forcePage ?: boolean;
};
export const Item: React.FC<ItemProps> = ({ id, forcePage }) => {
  const { item, errors, availableItems } = useFillItem(id);
  const mapboxContext = useContext(MapboxContext);
  
  if(!item) return null;

  const withChildren = () => availableItems.map(itemId => <Item key={itemId} id={itemId} />)

  if(item.type === 'group' && (item.view === 'page' || forcePage === true)) {
    return <Page page={item}>{withChildren()}</Page>;
  } else if (item.type === 'group') {
    return <Group group={item}>{withChildren()}</Group>;
  } else if (item.type === 'text' && item.view === 'textBox') {
    return <TextBox text={item} errors={errors} />;
  } else if (item.type === 'text' && item.view === 'address') {
    if (mapboxContext.token) {
      //return <Address address={item} errors={errors} />;
      return <Text text={item} errors={errors} />
    } else {
      console.warn(`Mapbox API access token is not set, Address item ${id} falls back to Text`)
      return <Text text={item} errors={errors} />
    }
  } else if(item.type === 'text') {
    return <Text text={item} errors={errors}/>;
  } else if(item.type === 'boolean') {
    if (item.props) {
      const display = item.props.display;
      if (display === 'checkbox') {
        return <BooleanCheckbox boolean={item} errors={errors}/>;
      }
    }
    return <BooleanRadio boolean={item} errors={errors}/>;
  } else if(item.type === 'number' || item.type === 'decimal') {
    return <Number number={item} errors={errors} />;
  } else if (item.type === 'list') {
    return <Choice choice={item} errors={errors} />;
  } else if (item.type === 'multichoice' && (item.props?.autocomplete === 'true' || item.props?.autocomplete === true)) {
    return <MultiChoiceAC multichoice={item} errors={errors} />
  } else if (item.type === 'multichoice') {
    return <MultiChoice multichoice={item} errors={errors} />;
  } else if (item.type === 'note') {
    return <Note note={item} />;
  } else if (item.type === 'date') {
    return <DateField datefield={item} errors={errors}/>
  } else if (item.type === 'time') {
    return <TimeField timefield={item} errors={errors}/>
  } else if (item.type === 'rowgroup') {
    return <RowGroup rowGroup={item}>{withChildren()}</RowGroup>;
  } else if (item.type === 'row') {
    return <Row row={item}>{id => (<Item id={id} key={id} />)}</Row>;
  } else if (item.type === 'surveygroup') {
    return <SurveyGroup surveyGroup={item}>{withChildren()}</SurveyGroup>;
  } else if(item.type === 'survey') {
    /*
    Surveys should not be handled through Item, as they depend on the encompassing
    SurveyGroup and should never be rendered as separate entities
    */
   return null;
  } else if(item.type === 'questionnaire') {
    throw new Error('Questionnaire should be handled outside Item');
  } else {
    const itemProxy: any = item;
    //throw new Error(`Unknown item type '${itemProxy.type}'!`);
    console.log(`Unsupported item type '${itemProxy.type}'`);
    return null;
  }
}
