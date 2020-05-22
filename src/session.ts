import produce from 'immer';
import { Action, ErrorAction, ItemAction, ItemType, ValueSetAction } from './actions';
import { DialobError } from './error';
import { onSyncFn, SyncQueue } from './sync-queue';
import { Transport } from './transport';

export type SessionItem<T extends ItemType = ItemType> = ItemAction<T>['item'];
export type SessionError = ErrorAction['error'];
export type SessionValueSet = ValueSetAction['valueSet'];

export interface SessionState {
  items: Record<string, SessionItem>;
  reverseItemMap: Record<string, Set<string>>;
  valueSets: Record<string, SessionValueSet>;
  errors: Record<string, SessionError[]>;
  locale?: string;
  complete: boolean;
};

export type onUpdateFn = () => void;
export type onErrorFn = (type: 'CLIENT' | 'SYNC' | 'SYNC-REPEATED', error: DialobError) => void;

export interface SessionOptions {
  syncWait?: number;
}

type Event = 'update' | 'sync' | 'error';

export class Session {
  private state: SessionState;
  private syncQueue: SyncQueue;

  private listeners: {
    update: onUpdateFn[],
    error: onErrorFn[],
  } = {
    update: [],
    error: [],
  };

  constructor(id: string, transport: Transport, options: SessionOptions = {}) {
    this.state = {
      items: {},
      reverseItemMap: {},
      valueSets: {},
      errors: {},
      complete: false,
    };
    this.syncQueue = new SyncQueue(id, transport, options.syncWait || 250);
    this.syncQueue.on('sync', (type, response) => {
      if(type === 'DONE' && response?.actions) {
        this.applyActions(response.actions);
      }
    });
  }

  /** STATE LOGIC */
  private insertReverseRef(state: SessionState, parentId: string, refIds: string[]) {
    for(const refId of refIds) {
      if(!state.reverseItemMap[refId]) {
        state.reverseItemMap[refId] = new Set();
      }
      state.reverseItemMap[refId].add(parentId);
    }
  }

  private applyActions(actions: Action[]): SessionState {
    this.state = produce(this.state, state => {
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
        } else if (action.type === 'GOTO') {
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

  public getAllItems(): SessionItem[] {
    return Object.values(this.state.items);
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
  public pull(): Promise<void> {
    return this.syncQueue.pull();
  }

  private queueAction(action: Action) {
    this.applyActions([action]);
    this.syncQueue.add(action);
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

  public goToPage(pageId: string) {
    this.queueAction({ type: 'GOTO', id: pageId });
  }

  /** EVENT LISTENERS */
  public on(type: 'update', listener: onUpdateFn): void;
  public on(type: 'sync', listener: onSyncFn): void;
  public on(type: 'error', listener: onErrorFn): void;
  public on(type: Event, listener: Function): void {
    if(type === 'sync') {
      this.syncQueue.on('sync', listener as any);
    } else {
      if(type === 'error') {
        this.syncQueue.on(type, listener as any);
      }

      this.listeners = produce(this.listeners, listeners => {
        const target: Function[] = listeners[type];
        target.push(listener);
      });
    }
  }

  public removeListener(type: Event, listener: Function): any {
    if(type === 'sync') {
      this.syncQueue.removeListener(type, listener);
    } else {
      if(type === 'error') {
        this.syncQueue.removeListener(type, listener);
      }

      this.listeners = produce(this.listeners, listeners => {
        const target: Function[] = listeners[type];
        const idx = target.findIndex(t => t === listener);
        target.splice(idx, 1);
      });
    }
  }

  private handleError(error: DialobError) {
    this.listeners.error.forEach(l => l('CLIENT', error));
  }
};
