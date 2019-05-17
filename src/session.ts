import produce from 'immer';
import { Action, ErrorAction, ItemActionGroup, ItemActionQuestionnaire, ItemActionValue, ValueSetAction } from './actions';
import { DialobResponse, Transport } from './transport';

type ErrorActionValue = ErrorAction['error'];
export interface SessionError extends ErrorActionValue {};
export interface SessionQuestionnaire extends ItemActionQuestionnaire {};
export interface SessionGroup extends ItemActionGroup {};
export interface SessionAnswer extends ItemActionValue {
  errors: SessionError[];
}
type ValueSetActionValue = ValueSetAction['valueSet'];
export interface SessionValueSet extends ValueSetActionValue {};

export interface SessionState {
  questionnaires: Record<string, SessionQuestionnaire>;
  groups: Record<string, SessionGroup>;
  answers: Record<string, SessionAnswer>;
  valueSets: Record<string, SessionValueSet>;
  errors: SessionError[];
  locale?: string;
  rev: number;
};

export type onUpdateFn = () => void;
export type onSyncFn = () => void;
export type onErrorFn = (error: DialobError) => void;
export type onClientErrorFn = (error: DialobError) => void;
export type onSyncErrorFn = (error: DialobError) => void;

export class Session {
  id: string;
  private transport: Transport;
  private state: SessionState;
  private syncActionQueue: Action[];
  private syncTimer?: number;

  private updateListeners: onUpdateFn[] = [];
  private syncListeners: onSyncFn[] = [];
  private errorListeners: onErrorFn[] = [];
  private clientErrorListeners: onClientErrorFn[] = [];
  private syncErrorListeners: onSyncErrorFn[] = [];

  constructor(id: string, transport: Transport) {
    this.id = id;
    this.transport = transport;
    this.state = {
      questionnaires: {},
      groups: {},
      answers: {},
      valueSets: {},
      errors: [],
      rev: 0,
    };
    this.syncActionQueue = [];
  }

  /** STATE LOGIC */
  private applyActions(actions: Action[]): SessionState {
    this.state = produce(this.state, state => {
      for(const action of actions) {
        if(action.type === 'RESET') {
          state.questionnaires = {};
          state.groups = {};
          state.answers = {};
          state.valueSets = {};
          state.errors = [];
          state.locale = undefined;
        } else if(action.type === 'ANSWER') {
          const answer = state.answers[action.id];
          if(!answer) throw new DialobError(`No item found with id '${action.id}'`);
          answer.value = action.answer;
        } else if(action.type === 'ITEM') {
          const item = action.item;
          if(item.type === 'questionnaire') {
            state.questionnaires[item.id] = item;
          } else if(item.type === 'group') {
            state.groups[item.id] = item;
          } else {
            state.answers[item.id] = { ...item, errors: [] };
          }
        } else if(action.type === 'ERROR') {
          state.errors.push(action.error);
          const answer = state.answers[action.error.id];
          if(answer) {
            answer.errors.push(action.error);
          }
        } else if(action.type === 'LOCALE') {
          state.locale = action.value;
        } else if(action.type === 'VALUE_SET') {
          state.valueSets[action.valueSet.id] = action.valueSet;
        } else if(action.type === 'COMPLETE') {
          // Do nothing
        } else if(action.type === 'NEXT') {
          // Do nothing
        } else {
          this.handleError(new DialobError('Unexpected action type!'));
        }
      }
    });

    this.updateListeners.map(l => l());
    return this.state;
  }

  // basically a polyfill for `Object.values()`, which isn't available in es5
  private mapToList<T>(data: { [s: string]: T }): T[] {
    return Object.keys(data).map(key => data[key]);
  }

  public getQuestionnaire(id: string): SessionQuestionnaire | undefined {
    return this.state.questionnaires[id];
  }

  public getAllQuestionnaires(): SessionQuestionnaire[] {
    return this.mapToList(this.state.questionnaires);
  }

  public getQuestionnaireGroups(questionnaireId: string): SessionGroup[] {
    return this.state.questionnaires[questionnaireId].items.map(id => this.state.groups[id]);
  }

  public getGroup(id: string): SessionGroup | undefined {
    return this.state.groups[id];
  }

  public getAllGroups(): SessionGroup[] {
    return this.mapToList(this.state.groups);
  }

  public getGroupChildren(groupId: string): (SessionAnswer | SessionGroup)[] {
    return this.state.groups[groupId].items.map(id => {
      return this.state.answers[id] || this.state.groups[id];
    });
  }

  public getAnswer(id: string): SessionAnswer | undefined {
    return this.state.answers[id];
  }

  public getAllAnswers(): SessionAnswer[] {
    return this.mapToList(this.state.answers);
  }

  public getValueSet(id: string): SessionValueSet | undefined {
    return this.state.valueSets[id];
  }

  public getAllValueSets(): SessionValueSet[] {
    return this.mapToList(this.state.valueSets);
  }

  /** SYNCING */
  private queueAction(action: Action) {
    if(this.syncTimer) {
      clearTimeout(this.syncTimer);
      this.syncTimer = setTimeout(this.syncQueuedActions, 50);
    }
    this.syncActionQueue.push(action);
    this.applyActions([action]);
  }

  private syncQueuedActions(): Promise<DialobResponse> {
    if(this.syncTimer) {
      clearTimeout(this.syncTimer);
      this.syncTimer = undefined;
    }

    const queue = this.syncActionQueue;
    this.syncActionQueue = [];
    return this.sync(queue, this.state.rev);
  }

  private async sync(actions: Action[], rev: number): Promise<DialobResponse> {
    let response;
    try {
      response = await this.transport.update(this.id, actions, rev);
    } catch(e) {
      this.handleError(e);
      throw e;
    }
    this.state.rev = response.rev;
    this.applyActions(response.actions);

    this.syncListeners.map(l => l());
    return response;
  }

  public async pull(): Promise<DialobResponse> {
    let response;
    try {
      response = await this.transport.getFullState(this.id);
    } catch(e) {
      this.handleError(e);
      throw e;
    }

    this.state.rev = response.rev;
    this.applyActions(response.actions);

    this.syncListeners.map(l => l());
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

  public complete() {
    this.queueAction({ type: 'COMPLETE' });
  }

  /** EVENT LISTENERS */
  private listenerSubscriber<T>(target: T[]) {
    return (listener: T) => {
      target.push(listener);
    }
  }

  public onUpdate = this.listenerSubscriber(this.updateListeners);
  public onSync = this.listenerSubscriber(this.syncListeners);
  public onError = this.listenerSubscriber(this.errorListeners);
  public onClientError = this.listenerSubscriber(this.clientErrorListeners);
  public onSyncError = this.listenerSubscriber(this.syncErrorListeners);

  private handleError(error: DialobError) {
    this.errorListeners.map(l => l(error));

    if(error instanceof DialobRequestError) {
      this.syncErrorListeners.map(l => l(error));
    } else {
      this.clientErrorListeners.map(l => l(error));
    }
  }
};
