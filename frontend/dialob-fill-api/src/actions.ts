export interface ResetAction {
  type: 'RESET';
}

export interface LocaleAction {
  type: 'LOCALE';
  value: string;
}

export interface SetLocaleAction {
  type: 'SET_LOCALE';
  value: string;
}

export type ValueType = string | string[] | number | number[] | boolean | null;

export type ItemType = 'questionnaire' | 'group' | 'text' | 'number' | 'boolean' | 'multichoice' | 'survey' | 'surveygroup' | 'list' | 'note' | 'date' | 'time' | 'decimal' | 'row' | 'rowgroup' | 'context' | 'variable';

interface GenericItemAction<Type extends ItemType, Value, Props> {
  type: 'ITEM';
  item: {
    id: string;
    type: Type;
    view?: string;
    label?: string;
    description?: string;
    disabled?: boolean;
    required?: boolean;
    className?: string[];
    value?: Value;
    items?: string[];
    activeItem?: string;
    availableItems?: string[];
    allowedActions?: Array<'ANSWER' | 'NEXT' | 'PREVIOUS' | 'COMPLETE' | 'ADD_ROW' | 'DELETE_ROW'>;
    answered?: boolean;
    valueSetId?: string;
    props?: Props;
  };
}

type IsType<Given extends ItemType, Value, Matching extends ItemType, Result, Props> =
  Given extends Matching
  ? Result extends Value
  ? GenericItemAction<Matching, Result, Props>
  : never
  : never;

export type ItemAction<T extends ItemType, Props = { [name: string]: string | null }, K = unknown> =
  IsType<T, K, 'questionnaire', undefined, Props>
  | IsType<T, K, 'group', undefined, Props>
  | IsType<T, K, 'text', string, Props>
  | IsType<T, K, 'number', number, Props>
  | IsType<T, K, 'boolean', boolean, Props>
  | IsType<T, K, 'multichoice', string[], Props>
  | IsType<T, K, 'survey', string, Props>
  | IsType<T, K, 'surveygroup', undefined, Props>
  | IsType<T, K, 'list', string, Props>
  | IsType<T, K, 'note', undefined, Props>
  | IsType<T, K, 'date', string, Props>
  | IsType<T, K, 'time', string, Props>
  | IsType<T, K, 'decimal', number, Props>
  | IsType<T, K, 'row', undefined, Props>
  | IsType<T, K, 'rowgroup', undefined, Props>
  | IsType<T, K, 'context', ValueType, Props>
  | IsType<T, K, 'variable', ValueType, Props>
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
  answer?: ValueType;
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

export interface GotoAction {
  type: 'GOTO';
  id: string;
}

export interface CompleteAction {
  type: 'COMPLETE';
}

export interface FillError {
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

export type Action = ResetAction | LocaleAction | SetLocaleAction | ItemAction<ItemType> | ValueSetAction | RemoveItemsAction | RemoveValueSetsAction | AnswerAction | PreviousAction | NextAction | GotoAction | CompleteAction | ErrorAction | RemoveErrorAction | DeleteRowAction | AddRowAction;
