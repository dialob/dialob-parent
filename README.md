# Dialob Fill API
## Install
```sh
yarn add @dialob/fill-api
```

## Quick-start
```js
import DialobFill from '@dialob/fill-api';

// Replace `sessionId` and `endpoint` with appropriate values
const sessionId = '8bab410b7bfac6f64fbbb1024d52a96f';
const session = DialobFill.newSession(sessionId, {
  endpoint: 'https://instance.dialob.io/',
});

// Now you have a session object that holds the state of the session. You should first pull data
// from the server
session.pull();

// Update answer
session.setAnswer('itemId', 'newAnswer');

// Add row to row group
session.addRowToGroup('rowGroup1');

// Delete row from row group
session.deleteRow('rowGroup1.1');

// Next page
session.next();

// Previous page
session.previous();

// Go directly to a page
session.goToPage(pageId);

// Complete the session
session.complete();

// Get item
session.getItem(itemId);

// Get item errors
session.getItemErrors(itemId);

// Get valueSet
session.getValueSet(valueSetId);

// Get locale
session.getLocale();

// Set locale
session.setLocale(locale);

// Get context or expression variable value
session.getVariable(id);

// The session object batches updates and syncs them at appropriate times, however, it also updates
// its local cache instantly. The `update` event is emitted on any state change, local or remote.
session.on('update', () => {
  console.log('onUpdate');
});

// `sync` event is emitted when syncing starts or ends.
session.on('sync' (syncState) => {
  // `syncState` can be either `INPROGRESS` (syncing is ongoing) or `DONE` (syncing was successfully
  // completed)
  console.log('onSync', syncState);
});

// `error` event is emitted after any error, remote or local.
session.on('error', (type, error) => {
  // `type` is either `CLIENT` (client-side error) or `SYNC` (sync error) or `SYNC-REPEATED`
  // (repeated sync error, indication of systematic failure and should require user interaction)
  console.error(type, error);
});

// You can remove listeners with `removeListener()`
session.removeListener('update', updateFn);
```

## Session options
You can define session options as the third argument to the session constructor:
```js
const session = DialobFill.newSession(sessionId, transport, options);
```

The possible options are:
```ts
interface SessionOptions {
  // Defines how many milliseconds session should batch actions for before syncing them to server.
  // A value of -1 disables the batching logic and always immediately syncs each action. This can
  // be used if you want to do debouncing yourself, for example.
  syncWait?: number;
}
```

## Compatibility
This API does not follow the same versioning as Dialob backend. To ensure full compatibility, refer
to this table. Each row documents a compatibility change, any versions in between can be assumed to
be compatible with eachother.

| fill-api   | backend  |
| ---------- | -------- |
| 1.4.0      | 1.0.11   |
| 1.3.0      | 1.0.8    |
| 1.0.0      | 1.0.0    |
