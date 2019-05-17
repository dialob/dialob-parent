export interface ResetAction {
  type: 'RESET';
}

export interface LocaleAction {
  type: 'LOCALE';
  value: string;
}

export interface ItemActionGroup {
  id: string;
  type: 'group';
  label: string;
  items: string[];
  answered: boolean;
}

export interface ItemActionQuestionnaire {
  id: string;
  type: 'questionnaire';
  label: string;
  items: string[];
  activeItem: string;
  availableItems: string[];
  allowedActions: Array<'ANSWER'>;
  answered: boolean;
}

export interface ItemActionValue {
  id: string;
  type: 'number' | 'string';
  label: string;
  value: any;
  answered: boolean;
}

export interface ItemAction {
  type: 'ITEM';
  item: ItemActionGroup | ItemActionQuestionnaire | ItemActionValue;
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

export interface AnswerAction {
  type: 'ANSWER';
  id: string;
  answer: any;
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

export type Action = ResetAction | LocaleAction | ItemAction | ValueSetAction | AnswerAction | NextAction | CompleteAction | ErrorAction;
