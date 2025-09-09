export type LocalizedString = {
  [language: string]: string;
};

export type Variable = {
  name: string;
  published?: boolean;
  expression: string;
  description?: string;
};

export type ContextVariableType = 'text' | 'number' | 'decimal' | 'boolean' | 'date' | 'time';

export type ContextVariable = {
  name: string;
  published?: boolean;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  defaultValue?: any;
  context: boolean;
  contextType: ContextVariableType | string;
  description?: string;
};

export type ValueSetEntry = {
  id: string;
  label: LocalizedString;
  when?: string;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  [prop: string]: any;
}

export type ValueSet = {
  id: string;
  entries?: ValueSetEntry[];
};

export type DialobItemType =
  'questionnaire' | 'group' | 'text' | 'number' | 'boolean' |
  'multichoice' | 'survey' | 'surveygroup' | 'list'
  | 'note' | 'date' | 'time' | 'decimal' | 'row' | 'rowgroup' |
  'verticalSurveygroup' | 'address' | 'textBox' | 'page';

export type DialobCategoryType = 'structure' | 'input' | 'output';

export type ValidationRule = {
  message?: LocalizedString;
  rule?: string;
};

export type DialobItemTemplate = {
  type: DialobItemType | string;
  view?: string;
  label?: LocalizedString;
  description?: LocalizedString;
  required?: string;
  valueSetId?: string;
  activeWhen?: string;
  canAddRowWhen?: string;
  canRemoveRowWhen?: string;
  items?: string[];
  className?: string[];
  props?: {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    [prop: string]: any;
  };
  validations?: ValidationRule[];
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  [prop: string]: any;
};

export type DialobItem = DialobItemTemplate & {
  id: string;
}

export type DialobItems = { [key: string]: DialobItem };

export type VisibilityType = 'ONLY_ENABLED' | 'SHOW_DISABLED' | 'ALL';

export type ComposerTag = {
  name: string;
  formName: string;
  formId?: string;
  description?: string;
  created?: string;
  type: 'NORMAL' | 'MUTABLE';
  creator?: string;
}

export type ComposerMetadata = {
  globalValueSets?: {
    label?: string;
    valueSetId: string;
  }[];
  contextValues?: {
    [name: string]: string;
  }
}

export type ComposerState = {
  _id?: string;
  _rev?: string;
  _tag?: string;
  name: string;
  data: {
    [item: string]: DialobItem;
  };
  variables?: (Variable | ContextVariable)[];
  valueSets?: ValueSet[];
  metadata: FormMetadata;
};

export type FormMetadata = {
  label?: string;
  labels?: string[];
  showDisabled?: boolean;
  questionClientVisibility?: VisibilityType;
  answersRequiredByDefault?: boolean;
  creator?: string;
  tenantId?: string;
  savedBy?: string;
  languages?: string[];
  valid?: boolean;
  created?: string;
  lastSaved?: string;
  composer?: ComposerMetadata;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  [prop: string]: any;
}

export const INIT_STATE: ComposerState = {
  _id: '',
  _rev: '',
  name: '',
  data: {},
  metadata: {},
};

export type ComposerCallbacks = {
  onAddItem?: (state: ComposerState, item: DialobItem) => void;
}
