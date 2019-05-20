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
    type: 'questionnaire' | 'group' | 'text' | 'number' | 'boolean' | 'multichoice' | 'survey' | 'surveygroup' | 'list' | 'note';
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

export interface AnswerAction {
  type: 'ANSWER';
  id: string;
  answer: any;
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

export interface ErrorAction {
  type: 'ERROR';
  error: {
    id: string;
    code: string;
    description: string;
  };
}

export type Action = ResetAction | LocaleAction | ItemAction | ValueSetAction | RemoveItemsAction | AnswerAction | PreviousAction | NextAction | CompleteAction | ErrorAction;
