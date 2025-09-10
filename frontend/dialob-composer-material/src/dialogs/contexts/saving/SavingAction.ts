import {
  DialobItemTemplate, ValueSetEntry, ValidationRule, LocalizedString,
  ContextVariableType,
  ContextVariable,
  Variable
} from "../../../types";

export type SavingAction =
  { type: 'updateItem', itemId: string, attribute: string, value: string, language?: string }
  | { type: 'updateItemId', itemId: string }
  | { type: 'updateLocalizedString', itemId: string, attribute: string, value: LocalizedString, index?: number }
  | { type: 'changeItemType', itemId: string, config: DialobItemTemplate }
  | { type: 'setItemProp', itemId: string, key: string, value: any }
  | { type: 'deleteItemProp', itemId: string, key: string }

  | { type: 'createValidation', itemId: string, rule?: ValidationRule }
  | { type: 'setValidationMessage', itemId: string, index: number, language: string, message: string }
  | { type: 'setValidationExpression', itemId: string, index: number, expression: string }
  | { type: 'deleteValidation', itemId: string, index: number }

  | { type: 'createValueSet', itemId: string | null, entries?: ValueSetEntry[] }
  | { type: 'setValueSetEntries', valueSetId: string, entries: ValueSetEntry[] }
  | { type: 'addValueSetEntry', valueSetId: string, entry?: ValueSetEntry }
  | { type: 'updateValueSetEntry', valueSetId: string, index: number, entry: ValueSetEntry }
  | { type: 'updateValueSetEntryLabel', valueSetId: string, index: number, text: string | null, language: string }
  | { type: 'deleteValueSetentry', valueSetId: string, index: number }
  | { type: 'moveValueSetEntry', valueSetId: string, from: number, to: number }
  | { type: 'setGlobalValueSetName', valueSetId: string, name: string }
  | { type: 'deleteLocalValueSet', valueSetId: string }
  | { type: 'deleteGlobalValueSet', valueSetId: string }

  | { type: 'createVariable', context: boolean }
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  | { type: 'updateContextVariable', variableId: string, contextType?: ContextVariableType | string, defaultValue?: any }
  | { type: 'updateExpressionVariable', variableId: string, expression: string }
  | { type: 'updateVariablePublishing', variableId: string, published: boolean }
  | { type: 'updateVariableDescription', variableId: string, description: string }
  | { type: 'deleteVariable', variableId: string }
  | { type: 'moveVariable', origin: ContextVariable | Variable, destination: ContextVariable | Variable }
  | { type: 'changeVariableId', variables: (ContextVariable | Variable)[] }
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  | { type: 'setMetadataValue', attr: string, value: any }

