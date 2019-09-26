export interface ResetAction {
  type: 'RESET';
}

export interface LocaleAction {
  type: 'LOCALE';
  value: string;
}

export type ItemType = 'questionnaire' | 'group' | 'text' | 'number' | 'boolean' | 'multichoice' | 'survey' | 'surveygroup' | 'list' | 'note' | 'date' | 'time' | 'decimal' | 'row' | 'rowgroup';
type IsType<Given extends ItemType, Matching extends ItemType, Result> = Given extends Matching ? Result : never;

export interface ItemAction<T extends ItemType> {
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
    value?: 
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
