import { Action } from './actions';
import { DialobError } from './error';
import { DialobResponse, Transport } from './transport';
import produce from 'immer';

export type onSyncFn = (syncState: 'INPROGRESS' | 'DONE', response?: DialobResponse) => void;
export type onErrorFn = (type: 'SYNC' | 'SYNC-REPEATED', error: DialobError) => void;

function syncActionImmediately(action: Action): boolean {
  return action.type === 'ADD_ROW' || action.type === 'NEXT' || action.type === 'PREVIOUS' || action.type === 'GOTO';
}

export class SyncQueue {
  private id: string;
  private transport: Transport;
  private inSync = false;
  private retryCount = 0;
  private rev = 0;
  private syncWait: number;
  private syncTimer?: ReturnType<typeof setTimeout>;
  private syncActionQueue: Action[];
  private syncQueueImmediately = false;

  private listeners: {
    sync: onSyncFn[];
    error: onErrorFn[];
  } = {
    sync: [],
    error: [],
  };

  constructor(id: string, transport: Transport, syncWait: number) {
    if(syncWait < -1) {
      throw new Error('syncWait must be -1 or higher!');
    }

    this.id = id;
    this.syncActionQueue = [];
    this.syncWait = syncWait;
    this.transport = transport;
  }

  public async pull(): Promise<void> {
    this.listeners.sync.forEach(l => l('INPROGRESS'));
    let response: DialobResponse | undefined;
    try {
      response = await this.transport.getFullState(this.id);
    } catch(e) {
      this.handleError(e);
      throw e;
    }

    this.rev = response.rev;
    this.listeners.sync.forEach(l => l('DONE', response));
  }

  public add(action: Action) {
    if(this.syncWait === -1) {
      // We use queue here instead of calling this.sync() directly, because if sync fails we need
      // to re-try and to have an efficient re-try, we need to work with an action queue anyway.
      // Better to re-use the existing logic than to have multiple implementations.
      this.addToSyncQueue(action);
      this.syncQueuedActions();
    } else {
      this.clearDeferredSync();
      this.addToSyncQueue(action);
      if(this.syncQueueImmediately || syncActionImmediately(action)) {
        this.syncQueueImmediately = true;
        this.syncQueuedActions();
      } else {
        this.deferSync();
      }
    }
  }

  public on(type: 'sync', listener: onSyncFn): void;
  public on(type: 'error', listener: onErrorFn): void;
  public on(type: 'sync' | 'error', listener: Function) {
    this.listeners = produce(this.listeners, listeners => {
      const target: Function[] = listeners[type];
      target.push(listener);
    });
  }

  public removeListener(type: 'sync' | 'error', listener: Function): any {
    this.listeners = produce(this.listeners, listeners => {
      const target: Function[] = listeners[type];
      const idx = target.findIndex(t => t === listener);
      target.splice(idx, 1);
    });
  }

  private addToSyncQueue(action: Action): void {
    let add = false;
    if(action.type === 'ANSWER') {
      // If answer change is already in sync queue, update that answer instead of appending new one
      const existingAnswerIdx = this.syncActionQueue.findIndex(queuedAction => {
        return queuedAction.type === action.type && queuedAction.id === action.id;
      });

      if(existingAnswerIdx !== -1) {
        this.syncActionQueue[existingAnswerIdx] = action;
      } else {
        add = true;
      }
    // In cases where server response is required, only add the action to queue once. Otherwise
    // you can create a situation where user clicks something multiple times because nothing is
    // happening on screen and then once sync succeeds, all the queued actions create a very
    // unexpected state on user's screen
    } else if(action.type === 'ADD_ROW') {
      add = !this.syncActionQueue.some(queuedAction =>
        queuedAction.type === action.type && queuedAction.id === action.id
      );
    } else if(action.type === 'NEXT' || action.type === 'PREVIOUS' || action.type === 'GOTO') {
      add = !this.syncActionQueue.some(queuedAction => queuedAction.type === action.type);
    } else {
      add = true;
    }

    if(add) {
      this.syncActionQueue.push(action);
    }
  }


  private syncQueuedActions = async (): Promise<void> => {
    this.clearDeferredSync();
    if(this.inSync || this.syncActionQueue.length === 0) {
      return;
    }
    
    this.inSync = true;
    const syncedActions = this.syncActionQueue;
    const syncImmediately = this.syncQueueImmediately;
    this.syncActionQueue = [];
    this.syncQueueImmediately = false;
    try {
      await this.sync(syncedActions, this.rev);
      this.inSync = false;
      this.retryCount = 0;

      if(this.syncActionQueue.length > 0 && !this.syncTimer) {
        this.syncQueuedActions();
      }
    } catch(e) {
      if(e.name !== 'NetworkError') {
        throw e;
      }
      const newActions = this.syncActionQueue;
      this.syncActionQueue = syncedActions;
      for(const action of newActions) {
        this.addToSyncQueue(action);
      }
      this.inSync = false;
      this.syncQueueImmediately = this.syncQueueImmediately || syncImmediately;
      this.retryCount++;

      if(!this.syncTimer) {
        this.deferSync(1000);
      }

      if(this.retryCount >= 3) {
        this.listeners.error.forEach(l => l('SYNC-REPEATED', e));
      }
    }
  }

  private async sync(actions: Action[], rev: number): Promise<void> {
    this.listeners.sync.forEach(l => l('INPROGRESS'));
    let response: DialobResponse | undefined;
    try {
      response = await this.transport.update(this.id, actions, rev);
    } catch(e) {
      this.handleError(e);
      throw e;
    }

    this.rev = response.rev;
    this.listeners.sync.forEach(l => l('DONE', response));
  }

  private deferSync(timeout = this.syncWait) {
    this.syncTimer = setTimeout(this.syncQueuedActions, timeout);
  }

  private clearDeferredSync() {
    if(!this.syncTimer) return;

    clearTimeout(this.syncTimer);
    this.syncTimer = undefined;
  }

  private handleError(error: Error) {
    this.listeners.error.forEach(l => l('SYNC', error));
  }
}
