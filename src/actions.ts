export interface ResetAction {
  type: 'RESET';
}

export interface LocaleAction {
  type: 'LOCALE';
  value: string;
}

export interface ItemAction {
  type: 'ITEM';
  item: {
    id: string;
    type: 'questionnaire' | 'group' | 'text' | 'number' | 'boolean' | 'multichoice' | 'survey' | 'surveygroup' | 'list' | 'note' | 'date' | 'time' | 'decimal' | 'row' | 'rowgroup';
    view?: string;
    label?: string;
    description?: string;
    disabled?: boolean;
    required?: boolean;
    className?: string[];
    value?: any;
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

export type Action = ResetAction | LocaleAction | ItemAction | ValueSetAction | RemoveItemsAction | RemoveValueSetsAction | AnswerAction | PreviousAction | NextAction | CompleteAction | ErrorAction | RemoveErrorAction | DeleteRowAction | AddRowAction;
