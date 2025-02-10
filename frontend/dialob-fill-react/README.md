# Dialob Fill React
This package provides convenience methods for interacting with Dialob Fill sessions through React.

Components:

- `<Session session={session} locale={locale}/>` where `session` is a session object created through
`@dialob/fill-api` library and `locale` is optional prop to override filling session locale. Can be changed during filling.

Hooks:

- `useFillActions()` returns actions that let you modify the session
- `useFillItem(id)` pulls the item and its errors with the given id from the session and keeps it updated
- `useFillValueSet(id)` pulls the value set with the given id from the session and keeps it updated
- `useFillUpdate(session => console.log(session.getLocale()))` runs the given callback every time the session updates
- `useFillVariable(id)` pulls the value of the context or expression variable with the given id from the session and keeps it updated. Result can be undefined if variable value is not known.

Deprecated:
- ~~`useFillSession()` returns the fill API session object~~
## Install
```sh
pnpm add @dialob/fill-react @dialob/fill-api
```

## Migration from 2.x

* Change dependencies: `@resys/dialob-fill-api` -> `@dialob/fill-api` and `@resys/dialob-fill-react` -> `@dialob/fill-react`
* `pnpm install`
* Change imports accordingly

## Quick-start
```jsx
import React, { useMemo } from 'react';
import DialobFill from '@dialob/fill-api';
import { Session, useFillActions, useFillItem, useFillValueSet } from '@dialob/fill-react';

const App = () => {
  const session = useMemo(() => {
    return DialobFill.newSession(sessionId, config);
  }, []);

  return (
    <Session session={session}>
      <Questionnaire/>
    </Session>
  );
}

const Questionnaire = () => {
  const { item: questionnaire, errors } = useFillItem('questionnaire');
  if(!questionnaire) return null;

  if(questionnaire.items) {
    return (
      <div>
        {questionnaire.items.map(itemId => (
          <Item key={itemId} id={itemId}/>
        ))}
        {errors.map(error => (
          <span key={error.code}>{error.description}</span>
        ))}
      </div>
    );
  }

  return <span>Empty questionnaire</span>
}

const Item = ({ id }) => {
  const { item, errors } = useFillItem(id);
  if(!item) return null;

  if(item.type === 'text') {
    return (
      <div>
        <input type='text' value={item.value}/>
        {errors.map(error => (
          <span key={error.code}>{error.description}</span>
        ))}
      </div>
    );
  } else if(item.type === 'group') {
    return (
      <div className='group'>
        {item.items && item.items.map(itemId => (
          <Item key={itemId} id={itemId}/>
        ))}
      </div>
    );
  } else if(item.type === 'surveyGroup') {
    return <SurveyGroup item={item}/>
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
          <Survey key={itemId} id={itemId} valueSet={valueSet}/>
        ))}
      </tbody>
    </table>
  );
}

const Survey = ({ id, valueSet }) => {
  const { item } = useFillItem(id);
  const { setAnswer } = useFillActions();
  if(!item) return null;

  return (
    <tr>
      <td>{item.label}</td>
      {valueSet.entries.map(entry => (
        <td key={entry.key}>
          <input
            onChange={e => setAnswer(item.id, e.currentTarget.value)}
            type='checkbox'
            checked={entry.value}
          />
        </td>
      ))}
    </tr>
  );
}
```
