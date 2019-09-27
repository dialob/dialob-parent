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

type IsType<Given extends ItemType, Value, Matching extends ItemType, Result> =
  Given extends Matching
  ? Result extends Value
    ? GenericItemAction<Matching, Result>
    : never
  : never;

export type ItemAction<T extends ItemType, K = unknown> = 
    IsType<T, K, 'questionnaire', undefined>
  | IsType<T, K, 'group', undefined>
  | IsType<T, K, 'text', string>
  | IsType<T, K, 'number', number>
  | IsType<T, K, 'boolean', boolean>
  | IsType<T, K, 'multichoice', string[]>
  | IsType<T, K, 'survey', string>
  | IsType<T, K, 'surveygroup', undefined>
  | IsType<T, K, 'list', string>
  | IsType<T, K, 'note', undefined>
  | IsType<T, K, 'date', string>
  | IsType<T, K, 'time', string>
  | IsType<T, K, 'decimal', number>
  | IsType<T, K, 'row', undefined>
  | IsType<T, K, 'rowgroup', undefined>
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
