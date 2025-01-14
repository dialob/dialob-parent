# 1.5.5

* Removed "shrink" property from InputLabel for Choice item, this resolves the crossed label issue

# 1.5.4

* Added Material UI Alert wrapper around the note item for styling the component depending on the style property

# 1.5.3

* Added color property, that is used to set background color to groups when the property is set to true

# 1.5.2

* Malay locale and UI translation

# 1.5.1

* Changed the positioning of Info Icon used in Description component.

# 1.5.0

 * Added implementation for these props: spacesTop, spacesBottom, invisible, indent and border
 * Added few swedish translations: Multirow: Add - Remove, Page control at bottom: Previous - Next
 * React 18
 * React-dom 18
 * Typescript 4.2

# 1.4.1

 * Added description for note item type
 * Added responsive image implementation for MarkdownView

# 1.4.0

 * Fix "show inactive items" rendering
 * Add breadcrumb navigation

# 1.3.2

 * Rowgroup's Add row & Remove row buttons are disabled when not allowed by form rule

# 1.2.0

* MaterialDialob translation messages can be overridden by embmedding application

# 1.1.4

* Fixed boolean radio default state

# 1.1.3

* Material UI 5.1

# 1.1.2

* Material UI 5 Final

# 1.1.1

* Fixed issues with number input label positioning
* Requires `dialob-fill-react` **3.1.x**

# 1.1.0

* Material UI 5 (RC 1)
* Use MUI 5 "system" styling (sx prop) instead of makeStyles
* Use date-fns instead of moment
* React 17
* React-intl 5
* React-markdown 7
* Number item (decimal and integer) is now using locale-specific react-number-format input handling

# 0.7.1

* Fixed translation for completion dialog title

# 0.7.0

* RowGroup can now tage `columns` property

# 0.6.1

* Fixed `columns` property for groups

# 0.6.0

* Renamed to `@dialob/fill-react` and moved to npmjs

# 0.5.0

* Item type `<Address>` moved to separate library `@resys/dialob-fill-material-item-address`.

# 0.4.0

* Added `<ChoiceAC>` item type

# 0.3.0

* Added `<Address>` item type

# 0.2.2

* Export `renderErrors()` and `<ErrorHelperText>` 

# 0.2.1

* Fixed MarkDown rendering

# 0.2.0

* Upgraded to use `dialob-fill-api` **1.4** and `dialob-fill-react` **2.6**
* Exported `GroupContext` 
* Impledmented `SurveyGroup`
