# Dialob Fill React
This package provides convenience methods for interacting with Dialob Fill sessions through React.

## Install
```sh
yarn add @resys/dialob-fill-react
```

## Quick-start
```jsx
import React from 'react';
import { Session, useFillItem, useFillSession, useFillValueSet } from '@resys/dialob-fill-react';

const App = () => (
  <Session id={sessionId} config={config}>
    <Questionnaire/>
  </Session>
);

const Questionnaire = () => {
  const questionnaire = useFillItem('questionnaire');
  if(!questionnaire) return null;

  if(questionnaire.items) {
    return (
      <div>
        {questionnaire.items.map(itemId => (
          <Item key={itemId} id={itemId}/>
        ))}
      </div>
    );
  }

  return <span>Empty questionnaire</span>
}

const Item = ({ id }) => {
  const item = useFillItem(id);
  if(!item) return null;

  if(item.type === 'text') {
    return <input type='text' value={item.value}/>
  } else if(item.type === 'surveyGroup') {
    return <SurveyGroup item={item}/>
  } else if(item.type === 'survey') {
    return <Survey item={item}/>
  } else {
    // etc... add your own selectors
    return null;
  }
}

const SurveyGroup = ({ item }) => {
  const valueSet = useFillValueSet(item.valueSetId);
  if(!valueSet) return null;

  return (
    <table>
      <thead>
        <tr>
          <th></th>
          {valueSet.entries.map(entry => (
            <th key={entry.key}>{entry.value}</th>
          ))}
        </tr>
      </thead>
      <tbody>
        {item.items.map(itemId => (
          <Item key={itemId} id={itemId}/>
        ))}
      </tbody>
    </div>
  );
}

const Survey = ({ item }) => {
  const valueSet = useFillValueSet(item.valueSetId);
  const session = useFillSession();
  if(!valueSet) return null;

  return (
    <tr>
      <td>{item.label}</td>
      {valueSet.entries.map(entry => (
        <td key={entry.key}>
          <input
            onChange={e => session.setAnswer(item.id, e.currentTarget.value)}
            type='checkbox'
            checked={entry.value}
          >
        </td>
      ))}
    </tr>
  );
}
```
