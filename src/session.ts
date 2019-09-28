import produce from 'immer';
import { Action, ErrorAction, ItemAction, ItemType, ValueSetAction } from './actions';
import { DialobError, DialobRequestError } from './error';
import { DialobResponse, Transport } from './transport';

export type SessionItem<T extends ItemType = ItemType> = ItemAction<T>['item'];
export type SessionError = ErrorAction['error'];
export type SessionValueSet = ValueSetAction['valueSet'];

export interface SessionState {
  items: Record<string, SessionItem>;
  reverseItemMap: Record<string, Set<string>>;
  valueSets: Record<string, SessionValueSet>;
  errors: Record<string, SessionError[]>;
  locale?: string;
  rev: number;
  complete: boolean;
};

export type onUpdateFn = () => void;
export type onSyncFn = (syncState: 'INPROGRESS' | 'DONE') => void;
export type onErrorFn = (type: 'CLIENT' | 'SYNC' | 'SYNC-REPEATED', error: DialobError) => void;

interface Options {
  syncWait?: number;
}

type Event = 'update' | 'sync' | 'error';

export class Session {
  id: string;
  private transport: Transport;
  private state: SessionState;
  private syncActionQueue: Action[];
  private syncWait: number;
  private syncTimer?: number;

  private listeners: {
    update: onUpdateFn[],
    sync: onSyncFn[],
    error: onErrorFn[],
  } = {
    update: [],
    sync: [],
    error: [],
  };

  constructor(id: string, transport: Transport, options: Options = {}) {
    this.id = id;
    this.transport = transport;
    this.state = {
      items: {},
      reverseItemMap: {},
      valueSets: {},
      errors: {},
      rev: 0,
      complete: false,
    };
    this.syncActionQueue = [];

    this.syncWait = options.syncWait || 250;
    if(this.syncWait < -1) {
      throw new Error('syncWait must be -1 or higher!');
    }
  }
  
  private insertReverseRef(state: SessionState, parentId: string, refIds: string[]) {
    for(const refId of refIds) {
      if(!state.reverseItemMap[refId]) {
        state.reverseItemMap[refId] = new Set();
      }
      state.reverseItemMap[refId].add(parentId);
    }
  }

  /** STATE LOGIC */
  private applyActions(actions: Action[], rev?: number): SessionState {
    this.state = produce(this.state, state => {
      if(rev) {
        state.rev = rev;
      }
      for(const action of actions) {
        if(action.type === 'RESET') {
          state.items = {};
          state.reverseItemMap = {};
          state.valueSets = {};
          state.errors = {};
          state.locale = undefined;
          state.complete = false;
        } else if(action.type === 'ANSWER') {
          const answer = state.items[action.id];
          if(!answer) throw new DialobError(`No item found with id '${action.id}'`);
          if(answer.type === 'questionnaire' || answer.type === 'group' || answer.type === 'surveygroup' || answer.type === 'note') {
            throw new DialobError(`Item '${action.id}' is not an answer!`);
          }

          answer.value = action.answer;
        } else if(action.type === 'ITEM') {
          const item = action.item;
          state.items[item.id] = item;

          if('items' in item && item.items) {
            this.insertReverseRef(state, item.id, item.items);
          }
        } else if(action.type === 'ERROR') {
          const error = action.error;
          if(!state.errors[error.id]) {
            state.errors[error.id] = [];
          }

          state.errors[error.id].push(error);
        } else if(action.type === 'LOCALE') {
          state.locale = action.value;
        } else if(action.type === 'VALUE_SET') {
          state.valueSets[action.valueSet.id] = action.valueSet;
        } else if(action.type === 'REMOVE_ITEMS') {
          for(const id of action.ids) {
            delete state.items[id];
            delete state.errors[id];

            if(state.reverseItemMap[id]) {
              state.reverseItemMap[id].forEach(reference => {
                const referencedItem: any = state.items[reference];
                if(!referencedItem || !referencedItem['items']) return;
                const idx = referencedItem.items.indexOf(reference);
                if(idx === -1) return;
                referencedItem.items.splice(idx, 1);
              });
            }
            delete state.reverseItemMap[id];
          }
        } else if(action.type === 'ADD_ROW') {
          // Wait for server response
        } else if(action.type === 'DELETE_ROW') {
          delete state.items[action.id];
        } else if(action.type === 'COMPLETE') {
          state.complete = true;
        } else if(action.type === 'NEXT') {
          // Wait for server response
        } else if(action.type === 'PREVIOUS') {
          // Wait for server response
        } else if(action.type === 'REMOVE_ERROR') {
          const error = action.error;
          const itemErrors = state.errors[error.id];
          if(itemErrors) {
            const errorIdx = itemErrors.findIndex(e => e.code === error.code);
            if(errorIdx !== -1) {
              itemErrors.splice(errorIdx, 1);
            }
          }
        } else if(action.type === 'REMOVE_VALUE_SETS') {
          for(const id of action.ids) {
            delete state.valueSets[id];
          }
        } else {
          this.handleError(new DialobError('Unexpected action type!'));
        }
      }
    });

    this.listeners.update.forEach(l => l());
    return this.state;
  }

  public getItem(id: string): SessionItem | undefined {
    return this.state.items[id];
  }

  public getItemErrors(id: string): SessionError[] | undefined {
    return this.state.errors[id];
  }

  public getValueSet(id: string): SessionValueSet | undefined {
    return this.state.valueSets[id];
  }

  public getLocale(): string | undefined {
    return this.state.locale;
  }

  public isComplete(): boolean {
    return this.state.complete;
  }

  /** SYNCING */
  private immediateSync = new Set(['ADD_ROW', 'NEXT', 'PREVIOUS']);
  private queueAction(action: Action) {
    if(this.syncWait === -1) {
      this.applyActions([action]);
      // We use queue here instead of calling this.sync() directly, because if sync fails we need
      // to re-try and to have an efficient re-try, we need to work with an action queue anyway.
      // Better to re-use the existing logic than to have multiple implementations.
      this.addToSyncQueue(action);
      this.syncQueuedActions();
    } else {
      if(this.syncTimer) {
        clearTimeout(this.syncTimer);
      }
      this.syncTimer = setTimeout(this.syncQueuedActions, this.syncWait);
      this.addToSyncQueue(action);
      this.applyActions([action]);
      if(this.immediateSync.has(action.type)) {
        this.syncQueuedActions();
      }
    }
  }

  private addToSyncQueue(action: Action) {
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
    } else if(action.type === 'NEXT' || action.type === 'PREVIOUS') {
      add = !this.syncActionQueue.some(queuedAction => queuedAction.type === action.type);
    } else {
      add = true;
    }

    if(add) {
      this.syncActionQueue.push(action);
    }
  }

  private inSync = false;
  private retryCount = 0;
  private syncQueuedActions = async (): Promise<void> => {
    if(this.inSync) {
      return;
    }

    this.inSync = true;
    if(this.syncTimer) {
      clearTimeout(this.syncTimer);
      this.syncTimer = undefined;
    }

    const queue = this.syncActionQueue;
    this.syncActionQueue = [];
    try {
      await this.sync(queue, this.state.rev);
      this.inSync = false;
      this.retryCount = 0;

      if(this.syncActionQueue.length > 0 && !this.syncTimer) {
        this.syncTimer = setTimeout(this.syncQueuedActions, this.syncWait);
      }
    } catch(e) {
      this.inSync = false;
      this.retryCount++;
      const newQueue = this.syncActionQueue;
      this.syncActionQueue = queue;
      newQueue.forEach(this.addToSyncQueue);

      if(!this.syncTimer) {
        this.syncTimer = setTimeout(this.syncQueuedActions, 1000);
      }

      if(this.retryCount >= 3) {
        this.listeners.error.forEach(l => l('SYNC-REPEATED', e));
      }
    }
  }

  private async sync(actions: Action[], rev: number): Promise<DialobResponse> {
    this.listeners.sync.forEach(l => l('INPROGRESS'));
    let response;
    try {
      response = await this.transport.update(this.id, actions, rev);
    } catch(e) {
      this.handleError(e);
      throw e;
    }

    this.applyActions(response.actions || [], response.rev);
    this.listeners.sync.forEach(l => l('DONE'));
    return response;
  }

  public async pull(): Promise<DialobResponse> {
    this.listeners.sync.forEach(l => l('INPROGRESS'));
    let response;
    try {
      response = await this.transport.getFullState(this.id);
    } catch(e) {
      this.handleError(e);
      throw e;
    }

    this.applyActions(response.actions || [], response.rev);
    this.listeners.sync.forEach(l => l('DONE'));
    return response;
  }

  /** CONVENIENCE METHODS */
  public setAnswer(itemId: string, answer: any) {
    this.queueAction({
      type: 'ANSWER',
      answer,
      id: itemId,
    });
  }

  public addRowToGroup(rowGroupId: string) {
    this.queueAction({ type: 'ADD_ROW', id: rowGroupId })
  }

  public deleteRow(rowId: string) {
    this.queueAction({ type: 'DELETE_ROW', id: rowId });
  }

  public complete() {
    this.queueAction({ type: 'COMPLETE' });
  }

  public next() {
    this.queueAction({ type: 'NEXT' });
  }

  public previous() {
    this.queueAction({ type: 'PREVIOUS' });
  }

  /** EVENT LISTENERS */
  public on(type: 'update', listener: onUpdateFn): void;
  public on(type: 'sync', listener: onSyncFn): void;
  public on(type: 'error', listener: onErrorFn): void;
  public on(type: Event, listener: Function): void {
    const target: Function[] = this.listeners[type];
    target.push(listener);
  }

  public removeListener(type: Event, listener: Function): any {
    const target: Function[] = this.listeners[type];
    const idx = target.findIndex(t => t === listener);
    target.splice(idx, 1);
  }

  private handleError(error: DialobError) {
    if(error instanceof DialobRequestError) {
      this.listeners.error.forEach(l => l('SYNC', error));
    } else {
      this.listeners.error.forEach(l => l('CLIENT', error));
    }
  }
};
