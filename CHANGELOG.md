## next

## 2.5.0
### Enhancements
- Added `useFillVariable()` hook.

## 2.4.1
### Enhancements
- Added `useFillUpdate()` hook.

## 2.3.1
### Enhancements
- Package source is included on publish for sourcemaps.

## 2.3.0
### Enhancements
-  Added optional `locale` prop to `<Session>` component to support overriding session language (Requires Dialob Backend 1.0.8)

## 2.2.1
### Enhancements
- Package source is included on publish for sourcemaps.

## 2.2.0
### Enhancements
- it's now possible to define which type will be returned from `useFillItem`:
```ts
const item = useFillItem<'boolean'>('booleanItem');
```
It is advised to use this only if you have manually verified the type of this item and are absolutely
certain it can not change in runtime (for example, the UI is only ran against a tagged form that
has been manually verified).

## 2.1.0
### Enhancements
- return `availableItems` property from `useFillItem()`. This contains the id-s of children of this
item that are actually available (visible) in the session.

## 2.0.1
### Bug fixes
- `useFillItem()` now issues less updates, in turn fixing a bug with UI updates
