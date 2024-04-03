export type LocalizedString = {
  [language: string]: string;
};

export type Variable = {
  name: string;
  published?: boolean;
  expression: string;
};

export type ContextVariableType = 'text' | 'number' | 'decimal' | 'boolean' | 'date' | 'time';

export type ContextVariable = {
  name: string;
  published?: boolean;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  defaultValue?: any;
  context: boolean;
  contextType: ContextVariableType | string;
};

export const isContextVariable = (variable: ContextVariable | Variable): variable is ContextVariable => (variable as ContextVariable).context === true;

export type ValueSetEntry = {
  id: string;
  label: LocalizedString;
  when?: string;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  [prop: string]: any;
}

export type ValueSet = {
  id: string;
  entries: ValueSetEntry[];
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

export type ComposerState = {
  _id: string;
  _rev: string;
  name: string;
  data: {
    [item: string]: DialobItem;
  };
  variables?: (Variable | ContextVariable)[];
  valueSets?: ValueSet[];
  metadata: {
    label?: string;
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
    composer?: {
      globalValueSets?: {
        label?: string;
        valueSetId: string;
      }[];
      contextValues?: {
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        [name: string]: any;
      }
    },
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    [prop: string]: any;
  }
};

export type ComposerCallbacks = {
  onAddItem?: (state: ComposerState, item: DialobItem) => void;
}
