## next

## 2.6.2
### Bug fixes
- Actually expose `useFillLocale()`

## 2.6.1
### Bug fixes
- Actually expose `useFillActions()`

# 2.6.0
### Enhancements
- `useFillLocale()` hook has been added

### Deprecations
- `useFillSession()` has been deprecated and replaced with `useFillActions()`. `useFillActions()` exposes only functions that can modify the session, but does not expose any functions that can query data from the session. This is to avoid any accidental mistakes, for example:

```jsx
const session = useFillSession();
return <SomeItem language={session.getLocale()}>;
```

The issue with this is that this item won't automatically render when the session's locale changes. It's extremely rare that this sort of non-updating behaviour is required - instead, you'd normally expect the value returned from the hook to auto-update.

We also can't change the `useFillSession()` hook to just always auto-update. This could too easily create cascading updates in the React tree, making each change possibly laggy.

For those reasons, the `useFillActions()` hook was chosen. It can stay constant across renders (i.e. it won't trigger React updates), and for any other case where data is required from the session, the appropriate hooks should be used. These hooks can properly track changes to the specific properties of the session, enabling efficient updates.

Example usage of `useFillActions()`:

```jsx
const { setAnswer, next, previous } = useFillActions();
setAnswer('itemId', 'new value');
next();
previous();
```

or:

```jsx
const fillActions = useFillActions();
fillActions.setAnswer('itemId', 'new value');
fillActions.next();
fillActions.previous();
```

## 2.5.1
### Other
- Package is built with CommonJS module target

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
