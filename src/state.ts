import produce from 'immer';
import { DialobError } from './error';
import { Action, ErrorAction, ItemAction, ItemType, ValueSetAction } from './actions';

export type SessionItem<T extends ItemType = ItemType> = ItemAction<T>['item'];
export type SessionError = ErrorAction['error'];
export type SessionValueSet = ValueSetAction['valueSet'];

export interface SessionState {
  items: Record<string, SessionItem>;
  reverseItemMap: Record<string, Set<string>>;
  valueSets: Record<string, SessionValueSet>;
  // We keep errors in an array as this is the common structure in which errors are needed by client UIs. See
  // `session.getItemErrors()`. It adds some slight overhead when adding/removing errors, but that happens less
  // frequently than the client asking for the errors.
  errors: Record<string, SessionError[]>;
  locale?: string;
  complete: boolean;
  variables: {
    [id: string]: any;
  }
};

export function initState(state: SessionState = ({} as SessionState)): SessionState {
  state.items = {};
  state.reverseItemMap = {};
  state.valueSets = {};
  state.errors = {};
  state.complete = false;
  state.variables = {};
  return state;
}

function insertReverseRef(state: SessionState, parentId: string, refIds: string[]) {
    for(const refId of refIds) {
      if(!state.reverseItemMap[refId]) {
        state.reverseItemMap[refId] = new Set();
      }
      state.reverseItemMap[refId].add(parentId);
    }
  }

export function updateState(state: SessionState, actions: Action[]): SessionState {
  return produce(state, state => {
    for(const action of actions) {
      if(action.type === 'RESET') {
        initState(state);
      } else if(action.type === 'ANSWER') {
        const answer = state.items[action.id];
        if(!answer) throw new DialobError(`No item found with id '${action.id}'`);
        if(answer.type === 'questionnaire' || answer.type === 'group' || answer.type === 'surveygroup' || answer.type === 'note') {
          throw new DialobError(`Item '${action.id}' is not an answer!`);
        }

        answer.value = action.answer;
      } else if(action.type === 'ITEM') {
        const item = action.item;

        if (action.item.type === 'context' || action.item.type === 'variable') {
          state.variables[action.item.id] = action.item.value;
        } else {
          state.items[item.id] = item;

          if('items' in item && item.items) {
            insertReverseRef(state, item.id, item.items);
          }
        }
      } else if(action.type === 'ERROR') {
        const error = action.error;
        if(!state.errors[error.id]) {
          state.errors[error.id] = [error];
        } else {
          const itemErrors = state.errors[error.id];
          const errorIdx = itemErrors.findIndex(e => e.code === error.code);
          if(errorIdx !== -1) {
            itemErrors[errorIdx] = error;
          } else {
            itemErrors.push(error);
          }
        }
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
      } else if (action.type === 'SET_LOCALE') {
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
        throw new DialobError('Unexpected action type!');
      }
    }
  });
}