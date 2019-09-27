export interface ResetAction {
  type: 'RESET';
}

export interface LocaleAction {
  type: 'LOCALE';
  value: string;
}

export type ItemType = 'questionnaire' | 'group' | 'text' | 'number' | 'boolean' | 'multichoice' | 'survey' | 'surveygroup' | 'list' | 'note' | 'date' | 'time' | 'decimal' | 'row' | 'rowgroup';

interface GenericItemAction<T extends ItemType, K> {
  type: 'ITEM';
  item: {
    id: string;
    type: T;
    view?: string;
    label?: string;
    description?: string;
    disabled?: boolean;
    required?: boolean;
    className?: string[];
    value?: K;
    items?: string[];
    activeItem?: string;
    availableItems?: string[];
    allowedActions?: Array<'ANSWER' | 'NEXT' | 'PREVIOUS' | 'COMPLETE'>;
    answered?: boolean;
    valueSetId?: string;
    props?: {
      [name: string]: any;
    }
  };
}
type IsType<Given extends ItemType, Matching extends ItemType, Result> = Given extends Matching ? GenericItemAction<Given, Result> : never;

export type ItemAction<T extends ItemType> = 
    IsType<T, 'questionnaire', never>
  | IsType<T, 'group', never>
  | IsType<T, 'text', string>
  | IsType<T, 'number', number>
  | IsType<T, 'boolean', boolean>
  | IsType<T, 'multichoice', string[]>
  | IsType<T, 'survey', string>
  | IsType<T, 'surveygroup', never>
  | IsType<T, 'list', string>
  | IsType<T, 'note', never>
  | IsType<T, 'date', string>
  | IsType<T, 'time', string>
  | IsType<T, 'decimal', number>
  | IsType<T, 'row', never>
  | IsType<T, 'rowgroup', never>
;


export interface ValueSetAction {
  type: 'VALUE_SET';
  valueSet: {
    id: string;
    entries: Array<{
      key: string;
      value: string;
    }>;
  }
}

export interface RemoveItemsAction {
  type: 'REMOVE_ITEMS',
  ids: string[];
}

export interface RemoveValueSetsAction {
  type: 'REMOVE_VALUE_SETS',
  ids: string[];
}

export interface AnswerAction {
  type: 'ANSWER';
  id: string;
  answer: any;
}

export interface AddRowAction {
  type: 'ADD_ROW';
  id: string;
}

export interface DeleteRowAction {
  type: 'DELETE_ROW';
  id: string;
}

export interface PreviousAction {
  type: 'PREVIOUS';
}

export interface NextAction {
  type: 'NEXT';
}

export interface CompleteAction {
  type: 'COMPLETE';
}

interface FillError {
  id: string;
  code: string;
  description: string;
}
export interface ErrorAction {
  type: 'ERROR';
  error: FillError;
}

export interface RemoveErrorAction {
  type: 'REMOVE_ERROR';
  error: FillError
}

export type Action = ResetAction | LocaleAction | ItemAction<ItemType> | ValueSetAction | RemoveItemsAction | RemoveValueSetsAction | AnswerAction | PreviousAction | NextAction | CompleteAction | ErrorAction | RemoveErrorAction | DeleteRowAction | AddRowAction;
