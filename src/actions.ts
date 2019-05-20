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
  items?: string[];
  answered: boolean;
}

export interface ItemActionQuestionnaire {
  id: string;
  type: 'questionnaire';
  label: string;
  items: string[];
  activeItem: string;
  availableItems: string[];
  allowedActions: Array<'ANSWER' | 'NEXT' | 'PREVIOUS' | 'COMPLETE'>;
  answered: boolean;
}

interface ItemActionValue<T extends string, K> {
  id: string;
  type: T;
  label: string;
  value: K;
  answered: boolean;
}

export type ItemActionText = ItemActionValue<'text', string>;
export type ItemActionNumber = ItemActionValue<'number', number>;
export type ItemActionBoolean = ItemActionValue<'boolean', boolean>;

export interface ItemActionMultiChoice {
  id: string;
  type: 'multichoice';
  valueSetId: string;
  label?: string;
  value?: string[];
  answered: boolean;
}

export interface ItemActionSurvey {
  id: string;
  type: 'survey';
  label: string;
  value?: string;
  answered: boolean;
}

export interface ItemActionSurveyGroup {
  id: string;
  type: 'surveygroup';
  label: string;
  items: string[];
  answered: boolean;
  valueSetId: string;
}

export interface ItemActionList {
  id: string;
  type: 'list';
  valueSetId: string;
  label?: string;
  value?: string;
  answered: boolean;
}

export interface ItemActionNote {
  id: string;
  type: 'note';
  label: string;
  answered: boolean;
}

export interface ItemAction {
  type: 'ITEM';
  item: ItemActionGroup | ItemActionQuestionnaire | ItemActionText | ItemActionNumber | ItemActionBoolean | ItemActionMultiChoice | ItemActionSurvey | ItemActionSurveyGroup | ItemActionList | ItemActionNote;
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
