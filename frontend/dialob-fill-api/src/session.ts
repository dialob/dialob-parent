import { produce } from 'immer';
import { Action, AnswerAction } from './actions';

import { updateState, initState, SessionError, SessionItem, SessionState, SessionValueSet } from './state';
import { SyncQueue } from './sync-queue';
import type { onSyncFn, onErrorFn, onUpdateFn, ListenerFunction, Event } from './sync-queue';
import { Transport } from './transport';

import type { ValueType } from './actions';

export interface SessionOptions {
  syncWait?: number;
}


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
    this.state = initState();
    this.syncQueue = new SyncQueue(id, transport, options.syncWait || 250);
    this.syncQueue.on('sync', (type, response) => {
      if (type === 'DONE' && response?.actions) {
        this.state = updateState(this.state, response.actions);
        this.listeners.update.forEach(l => l());
      }
    });
  }

  applyActions(actions: Action[]): SessionState {
    this.state = updateState(this.state, actions);
    this.listeners.update.forEach(l => l());
    return this.state;
  }

  /** STATE LOGIC */
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

  public getVariable(id: string): ValueType | undefined {
    return this.state.variables[id];
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
  public setAnswer(itemId: string, answer: AnswerAction['answer']) {
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

  public setLocale(locale: string) {
    if (locale !== this.state.locale) {
      this.queueAction({ type: 'SET_LOCALE', value: locale });
    }
  }

  public get id() {
    return this.syncQueue.getId();
  }

  /** EVENT LISTENERS */
  public on(type: 'update', listener: onUpdateFn): void;
  public on(type: 'sync', listener: onSyncFn): void;
  public on(type: 'error', listener: onErrorFn): void;
  public on(type: Event, listener: ListenerFunction): void {
    if (type === 'sync') {
      this.syncQueue.on('sync', listener as onSyncFn);
    } else {
      if (type === 'error') {
        this.syncQueue.on(type, listener as onErrorFn);
      }

      this.listeners = produce(this.listeners, listeners => {
        const target: ListenerFunction[] = listeners[type];
        target.push(listener);
      });
    }
  }

  public removeListener(type: Event, listener: ListenerFunction): void {
    if (type === 'sync') {
      this.syncQueue.removeListener(type, listener);
    } else {
      if (type === 'error') {
        this.syncQueue.removeListener(type, listener);
      }

      this.listeners = produce(this.listeners, listeners => {
        const target: ListenerFunction[] = listeners[type];
        const idx = target.findIndex(t => t === listener);
        target.splice(idx, 1);
      });
    }
  }
};
