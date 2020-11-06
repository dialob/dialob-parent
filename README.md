# Dialob Fill Components for Material UI

## Application Setup

### Dependencies

Add dependency

```
yarn add @resys/dialob-fill-material
```

Following peer dependencies need to be installed:

```json
{
  "@resys/dialob-fill-api": "^1.4.*",
  "@resys/dialob-fill-react": "^2.6.*",
  "@resys/mapbox-connector": "^1.1.*",
  "@material-ui/core": "^4.11.*",
  "@material-ui/icons": "^4.9.*",
  "@material-ui/lab": "^4.0.*",
  "@material-ui/pickers": "^3.2.*",
  "@date-io/moment": "^1.3.*",
  "moment": "^2.24.*",
  "react-intl": "^4.5.*",
  "react-markdown": "^4.3.*"
}
```

### Dialob component wrapper

One possibility to implement

```ts
import React from 'react';
import {Session as DialobSession } from '@resys/dialob-fill-api';
import {MaterialDialob, DefaultView} from '@resys/dialob-fill-material';
import {Item} from './Item';

export interface DialobProps {
  session: DialobSession | null;
  locale: string;
}

export const Dialob: React.FC<DialobProps> = ({session, locale}) => {
  return (
    session &&
    <MaterialDialob session={session} locale={locale}>
      <DefaultView>
        { items => items.map(id => (<Item id={id} key={id} />))}
      </DefaultView>
    </MaterialDialob>
  );
};
```

`<Item>` is a component that renders an actual fill component depending on item's properties. You have to provide your own depending on application needs. (See example here: https://git.resys.io/dialob/dialob-fill-material-demo/-/blob/master/src/dialob/Item.tsx  (needs better docs))

### Component types

Following components are exported

* `<BooleanCheckbox>` Boolean, checkbox style
* `<BooleanRadio>` Boolean, radiobutton style (Yes/No)
* `<Choice>` Choice dropdown
* `<DateField>` Date field
* `<Group>` Group
* `<MultiChoice>` Multi-choice, checbkox list style
* `<MultiChoiceAC>` Multi-choice, dropdown autocomplete style
* `<Note>` Note
* `<Number>` Number field
* `<Page>` Page
* `<RowGroup>` Row group
* `<Row>` Row for Row group
* `<Text>` Text field
* `<TextBox>` Multi-line text field
* `<TimeField>` Time field
* `<SurveyGroup>` Survey group
* `<Survey>` Survey item. NB! Survey item shouldn't be directly created, it is created by SurveyGroup.
* `<Address>` Address lookup using Mapbox. Using this in an implementation requires setting up Mapbox API access token.
* `<GroupContext>` Group context provider
* `renderErrors(errors:FillError[])` Helper function to render filling validation errors in unified manner, usable for example `<TextField helperText={}`
` <ErrorHelperText>` Materia's `<FormHelperText>` with filling validation errors. 

(This needs improvement)

### Missing features

* Network / service error handling
* Default `<Item>` implementation for convenience
