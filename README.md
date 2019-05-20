# Dialob Fill API
## Install
```sh
yarn add @resys/dialob-fill-api
```

## Quick-start
```js
import dialobFill from '@resys/dialob-fill-api';

// Replace `sessionId` and `endpoint` with appropriate values
const sessionId = '8bab410b7bfac6f64fbbb1024d52a96f';
const session = dialobFill.newSession(sessionId, {
  endpoint: 'https://instance.dialob.io/',
});

// Now you have a session object that holds the state of the session. You should first pull data
// from the server.
session.pull();
// NOTE: It is more proper to `pull()` only after setting up listeners (which are described below).

// Update answers:
session.setAnswer('itemId', 'newAnswer');

// Complete the session:
session.complete();

// The session object batches updates and syncs them at appropriate times, however, it also updates
// its local cache instantly. The `update` event is emitted on any state change, local or remote.
session.on('update', () => {
  console.log('onUpdate');
});

// `sync` event is emitted when syncing starts or ends.
session.on('sync' (syncState) => {
  console.log('onSync', syncState);
});

// `error` event is emitted after any error, remote or local.
session.on('error', (type, error) => {
  console.error(type, error);
});

// You can remove listeners with `removeListener()`
session.removeListener('update', updateFn);
```
