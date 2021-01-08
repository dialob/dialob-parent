import { Action } from '../src/actions';
import { Session, SessionOptions } from '../src/session';
import { MockTransport } from './mocks/mock-transport';

jest.useFakeTimers();

// https://stackoverflow.com/a/51045733
const flushPromises = () => new Promise(setImmediate);

async function verifyUpdates(transport: MockTransport, n: number) {
  jest.runAllTimers();
  await flushPromises();
  expect(transport.update).toBeCalledTimes(n);
}

function makeSession(options?: SessionOptions) {
  const transport = new MockTransport();
  transport.update.mockReturnValue({
    rev: 1200,
  });
  const session = new Session('session.test.ts', transport, options);
  return { transport, session };
}

test('state is empty on initial session creation', () => {
  const { session } = makeSession();
  expect(session.getItem('some-item')).toBe(undefined);
  expect(session.getItemErrors('some-item')).toBe(undefined);
  expect(session.getLocale()).toBe(undefined);
  expect(session.getValueSet('some-valueset')).toBe(undefined);
  expect(session.getAllItems()).toEqual([]);
});

test('fetching from local state does not make any calls over the transport mechanism', () => {
  const { session, transport } = makeSession();

  session.getItem('some-item');
  session.getItemErrors('some-item');
  session.getLocale();
  session.getValueSet('some-valueset');
  session.getAllItems();

  expect(transport.getFullState).not.toBeCalled();
  expect(transport.update).not.toBeCalled();
});

test('pulls data from transport layer and updates state', async () => {
  const { session, transport } = makeSession();

  const action1: Action = {
    type: 'ITEM',
    item: {
      id: 'number-item',
      type: 'number',
      value: 3
    }
  };

  const action2: Action = {
    type: 'ITEM',
    item: {
      id: 'text-item',
      type: 'text',
      value: 'Some text'
    }
  };

  transport.getFullState.mockReturnValue({
    rev: 120,
    actions: [action1, action2],
  });

  await session.pull();
  expect(transport.getFullState).toBeCalled();
  expect(session.getItem('number-item')).toBe(action1.item);
  expect(session.getItem('text-item')).toBe(action2.item);
});

test('updates local state immediately when an action is triggered (if possible)', async () => {
  const { session, transport } = makeSession();

  transport.getFullState.mockReturnValue({
    rev: 10,
    actions: [
      {
        type: 'ITEM',
        item: {
          id: 'item1',
          type: 'text',
          value: null
        }
      },
    ],
  });

  await session.pull();
  expect(session.getItem('item1')).toEqual({
    id: 'item1',
    type: 'text',
    value: null,
  });
  session.setAnswer('item1', 'New value');
  expect(session.getItem('item1')).toEqual({
    id: 'item1',
    type: 'text',
    value: 'New value',
  });
});

test('batches actions and syncs data after specified `syncWait` time', async () => {
  const { session, transport } = makeSession({ syncWait: 200 });

  transport.getFullState.mockReturnValue({
    rev: 11,
    actions: [
      {
        type: 'ITEM',
        item: {
          id: 'item1',
          type: 'text',
          value: null
        }
      },
      {
        type: 'ITEM',
        item: {
          id: 'item2',
          type: 'text',
          value: null
        }
      },
      {
        type: 'ITEM',
        item: {
          id: 'rowgroup0',
          type: 'rowgroup'
        }
      },
      {
        type: 'ITEM',
        item: {
          id: 'rowgroup0_row0',
          type: 'row',
        },
      }
    ],
  });

  await session.pull();
  session.setAnswer('item1', 'new value');
  session.deleteRow('rowgroup0_row0');
  session.complete();

  expect(transport.update).not.toBeCalled();
  jest.advanceTimersByTime(150);
  expect(transport.update).not.toBeCalled();
  jest.advanceTimersByTime(50);
  expect(transport.update).toBeCalledTimes(1);
  expect(transport.update).toBeCalledWith('session.test.ts', [
    {
      type: 'ANSWER',
      id: 'item1',
      answer: 'new value'
    },
    {
      type: 'DELETE_ROW',
      id: 'rowgroup0_row0',
    },
    {
      type: 'COMPLETE'
    }
  ], 11);
});

test('does not batch an answer to the same item multiple times', async () => {
  const { session, transport } = makeSession();

  transport.getFullState.mockReturnValue({
    rev: 120,
    actions: [
      {
        type: 'ITEM',
        item: {
          id: 'item1',
          type: 'text',
          value: null
        }
      },
      {
        type: 'ITEM',
        item: {
          id: 'item2',
          type: 'text',
          value: null
        }
      }
    ],
  });

  await session.pull();
  session.setAnswer('item1', 'First answer');
  session.setAnswer('item2', 'Item2');
  session.setAnswer('item1', 'Second answer');

  jest.runAllTimers();
  expect(transport.update).toBeCalledWith('session.test.ts', [
    {
      type: 'ANSWER',
      id: 'item1',
      answer: 'Second answer'
    },
    {
      type: 'ANSWER',
      id: 'item2',
      answer: 'Item2'
    }
  ], 120);
});

test('syncs new actions that were queued during previous sync', async () => {
  const { session, transport } = makeSession({ syncWait: 100 });

  transport.getFullState.mockReturnValue({
    rev: 120,
    actions: [
      {
        type: 'ITEM',
        item: {
          id: 'item1',
          type: 'text',
          value: null
        }
      },
      {
        type: 'ITEM',
        item: {
          id: 'item2',
          type: 'text',
          value: null
        }
      }
    ],
  });

  transport.update.mockReturnValue(new Promise(resolve => {
    setTimeout(() => {
      resolve({
        rev: 15001
      });
    }, 500);
  }));

  await session.pull();
  session.setAnswer('item2', 'item2 value');
  session.setAnswer('item1', 'new value');
  session.setAnswer('item1', 'newest value');

  jest.advanceTimersByTime(100);
  await flushPromises();
  expect(transport.update.mock.calls[0][1]).toEqual([
    {
      type: 'ANSWER',
      id: 'item2',
      answer: 'item2 value',
    },
    {
      type: 'ANSWER',
      id: 'item1',
      answer: 'newest value',
    },
  ]);

  session.setAnswer('item1', 'change again');

  jest.advanceTimersByTime(200);
  await flushPromises();
  session.setAnswer('item1', 'second sync');

  jest.advanceTimersByTime(200);
  await flushPromises();
  expect(transport.update).toBeCalledTimes(2);
  expect(transport.update.mock.calls[1][1]).toEqual([
    {
      type: 'ANSWER',
      id: 'item1',
      answer: 'second sync',
    },
  ]);

  await verifyUpdates(transport, 2);
});

test('syncs new actions after syncWait if no actions that need to be immediately synced were queued', async () => {
  const { session, transport } = makeSession({ syncWait: 10000 });

  transport.getFullState.mockReturnValue({
    rev: 120,
    actions: [
      {
        type: 'ITEM',
        item: {
          id: 'item1',
          type: 'text',
          value: null
        }
      },
      {
        type: 'ITEM',
        item: {
          id: 'item2',
          type: 'text',
          value: null
        }
      }
    ],
  });

  transport.update.mockReturnValue(new Promise(resolve => {
    setTimeout(() => {
      resolve({
        rev: 15001
      });
    }, 100);
  }));

  await session.pull();
  session.setAnswer('item2', 'item2 value');
  session.setAnswer('item1', 'new value');
  session.setAnswer('item1', 'newest value');

  jest.advanceTimersByTime(10000);
  await flushPromises();
  expect(transport.update.mock.calls[0][1]).toEqual([
    {
      type: 'ANSWER',
      id: 'item2',
      answer: 'item2 value',
    },
    {
      type: 'ANSWER',
      id: 'item1',
      answer: 'newest value',
    },
  ]);

  session.setAnswer('item1', 'change again');

  jest.advanceTimersByTime(50);
  await flushPromises();
  session.setAnswer('item1', 'second sync');

  jest.advanceTimersByTime(1000);
  await flushPromises();
  expect(transport.update).toBeCalledTimes(1);

  jest.advanceTimersByTime(5000);
  await flushPromises();
  expect(transport.update).toBeCalledTimes(1);

  jest.advanceTimersByTime(4000);
  await flushPromises();
  expect(transport.update).toBeCalledTimes(2);
  expect(transport.update.mock.calls[1][1]).toEqual([
    {
      type: 'ANSWER',
      id: 'item1',
      answer: 'second sync',
    }
  ]);

  await verifyUpdates(transport, 2);
});

test('syncs new actions immediately if an action was queued that needs to be immediately synced', async () => {
  const { session, transport } = makeSession({ syncWait: 10000 });

  transport.getFullState.mockReturnValue({
    rev: 120,
    actions: [
      {
        type: 'ITEM',
        item: {
          id: 'item1',
          type: 'text',
          value: null
        }
      },
      {
        type: 'ITEM',
        item: {
          id: 'item2',
          type: 'text',
          value: null
        }
      }
    ],
  });

  transport.update.mockReturnValue(new Promise(resolve => {
    setTimeout(() => {
      resolve({
        rev: 15001
      });
    }, 100);
  }));

  await session.pull();
  session.setAnswer('item2', 'item2 value');
  session.setAnswer('item1', 'new value');
  session.setAnswer('item1', 'newest value');

  jest.advanceTimersByTime(10000);
  await flushPromises();
  expect(transport.update.mock.calls[0][1]).toEqual([
    {
      type: 'ANSWER',
      id: 'item2',
      answer: 'item2 value',
    },
    {
      type: 'ANSWER',
      id: 'item1',
      answer: 'newest value',
    },
  ]);

  session.setAnswer('item1', 'change again');
  jest.advanceTimersByTime(50);
  await flushPromises();
  expect(transport.update).toBeCalledTimes(1);

  session.setAnswer('item1', 'second sync');
  session.next();
  session.setAnswer('item1', 'newest change');

  jest.advanceTimersByTime(100);
  await flushPromises();
  expect(transport.update).toBeCalledTimes(2);
  expect(transport.update.mock.calls[1][1]).toEqual([
    {
      type: 'ANSWER',
      id: 'item1',
      answer: 'second sync',
    },
    {
      type: 'NEXT',
    },
  ]);

  await verifyUpdates(transport, 3);
});

test('calls event handlers on state update', async () => {
  const { session, transport } = makeSession();
  transport.getFullState.mockReturnValue({
    rev: 120,
    actions: [
      {
        type: 'ITEM',
        item: {
          id: 'item1',
          type: 'text',
          value: null
        }
      },
      {
        type: 'ITEM',
        item: {
          id: 'item2',
          type: 'text',
          value: null
        }
      }
    ],
  });

  await session.pull();
  const onUpdate = jest.fn();

  session.on('update', onUpdate);
  session.setAnswer('item1', 'new-text');

  expect(onUpdate).toBeCalledTimes(1);
  expect(onUpdate).lastCalledWith();
});

test('calls event handlers on sync update', async () => {
  const { session, transport } = makeSession({ syncWait: 10000 });
  const onUpdate = jest.fn();
  const onSync = jest.fn();
  session.on('update', onUpdate);
  session.on('sync', onSync);

  await flushPromises();
  expect(onUpdate).not.toBeCalled();

  const transportResponse = {
    rev: 120,
    actions: [
      {
        type: 'ITEM',
        item: {
          id: 'item1',
          type: 'text',
          value: null
        }
      },
      {
        type: 'ITEM',
        item: {
          id: 'item2',
          type: 'text',
          value: null
        }
      }
    ],
  }
  transport.getFullState.mockReturnValue(transportResponse);

  await session.pull();
  expect(onUpdate).toBeCalledTimes(1);
  expect(onSync).toBeCalledTimes(2);

  expect(onSync).toBeCalledWith('INPROGRESS');
  expect(onSync).toBeCalledWith('DONE', transportResponse);
});

test('throws error if item does not exist', () => {
  const { session } = makeSession();
  expect(() => {
    session.setAnswer('someItem', 'yes');
  }).toThrowError(`No item found with id 'someItem'`);
});

test('throws error if syncWait is smaller than -1', () => {
  expect(() => makeSession({ syncWait: -2 })).toThrowError('syncWait must be -1 or higher!');
});