# Dialob Review Components for Material UI

## Application Setup

### Dependencies

Add dependency

```
pnpm add @dialob/review-material

```


The following peer dependencies need to be installed for mui v5:

```json
{
    "@emotion/react": "^11.5.0",
    "@emotion/styled": "^11.3.0",
    "@mui/icons-material": "^5.0.0",
    "@mui/material": "^5.0.4",
    "@mui/styles": "^5.0.1",
    "react-intl": "^7.1.*",
    "react-markdown": "^10.1.0",
    "@date-io/date-fns": "^2.11.*",
    "date-fns": "^2.0.0"
}
```


### Exports

* `MaterialDialobReview` - Main UI component
* `DEFAULT_ITEM_CONFIG` - Built-in default item configuration
* `ItemProps` - TS type for custom item components

### Usage

```ts
import React from 'react';
import { MaterialDialobReview } from '@dialob/review-material';

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
