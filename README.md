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
// its local cache instantly. `onUpdate` is ran on any state change, local or remote.
session.onUpdate(() => {
  console.log('onUpdate');
});

// `onSync` is only ran after the state is successfully synced to the server.
session.onSync(() => {
  console.log('onSync');
});

// `onError` is only ran after any error, remote or local.
session.onError(error => {
  console.error(error);
});

// `onClientError` is ran after some sort of client-side error.
session.onClientError(error => {
  console.error(error);
});

// `onSyncError` is ran when there is some sync error
session.onSyncError(error => {
  console.error(error);
});
```
