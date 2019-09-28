# Dialob Fill API
## Install
```sh
yarn add @resys/dialob-fill-api
```

## Quick-start
```js
import DialobFill from '@resys/dialob-fill-api';

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
  // `type` is either `CLIENT` (client-side error) or `SYNC` (sync error)
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
  // be used if you want to do debouncing yourself, for exapmle.
  syncWait?: number;
}
```
