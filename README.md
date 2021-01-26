# Dialob Review Components for Material UI

## Application Setup

### Dependencies

Add dependency

```
yarn add @resys/dialob-review-material
```

Following peer dependencies need to be installed:

```json
{
  "@material-ui/core": "^4.11.*",
  "@material-ui/icons": "^4.9.*",
  "@date-io/moment": "^1.3.*",
  "moment": "^2.24.*",
  "react-intl": "5.3.*",
  "react-markdown": "^4.3.*"
}
```

### Exports

* `MaterialDialobReview` - Main UI component
* `DEFAULT_ITEM_CONFIG` - Built-in default item configuration
* `ItemProps` - TS type for custom item components

### Usage

```ts
import React from 'react';
import { MaterialDialobReview } from '@resys/dialob-review-material';

const ReviewWrapper : React.FC = () => {
  return (
    <MaterialDialobReview formData={formData} sessionData={sessionData} />
  );
};
```

#### Props

* `formData` - Dialob form json
* `sessionData` - Dialob session json
* `title` - (Optional) document title, defaults to form title
* `locale` - (Optional) UI locale, defaults to session locale
* `itemConfig` - (Optional) alternative item configuration for customization, defaults to built-in default config

### Customization

Pass custom `itemConfig` prop that defines components used for rendering items. See `DEFAULT_ITEM_CONFIG` for example.

TBD..

### Missing features

* Description popups
