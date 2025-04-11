import * as sessionJSON from './dialob.demo.session.json';
import { SessionState, initState, updateState } from '@dialob/fill-api/lib/state';
import { onUpdateFn, onErrorFn } from '@dialob/fill-api/lib/session';
import { SyncQueue } from '@dialob/fill-api/lib/sync-queue';

import { Action, SessionItem, SessionError, SessionValueSet, Session } from '@dialob/fill-api';


const sessionData = ((sessionJSON) as any).default;


class FakeSyncQueue {

  on() {
  }

  getId() {
    return "86088c3cf4ace4581a41f741cb0dcfff";
  }
  pull() {
    return sessionData;
  }
  add() {

  }

}

class FakeSession {
  private state: SessionState;
  private syncQueue: SyncQueue;

  private listeners: {
    update: onUpdateFn[],
    error: onErrorFn[],
  } = {
      update: [],
      error: [],
    };

  constructor() {
    //super("86088c3cf4ace4581a41f741cb0dcfff", {} as Transport);

    this.state = updateState(initState(), sessionData.actions);
    this.syncQueue = new FakeSyncQueue() as unknown as SyncQueue;

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

  public getVariable(id: string): any {
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

  public setLocale(locale: string) {
    if (locale !== this.state.locale) {
      this.queueAction({ type: 'SET_LOCALE', value: locale });
    }
  }

  public get id() {
    return this.syncQueue.getId();
  }

  public on(): void {
  }

  public removeListener(): void {
  }
};

const createSession = () => new FakeSession() as unknown as Session;

export default createSession;

