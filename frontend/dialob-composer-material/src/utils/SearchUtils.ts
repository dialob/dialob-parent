import { ContextVariable, DialobItem, Variable } from "../types";

export type ItemMatchType = 'label' | 'description' | 'id' | 'visibility' | 'requirement' | 'validation'; 
export type VariableMatchType = 'name' | 'description' | 'default' | 'expression';

export interface SearchMatch {
  type: 'item' | 'variable';
  matchType: ItemMatchType | VariableMatchType;
  content?: string;
  id: string;
}

export const matchItemByKeyword = (item: DialobItem, languages?: string[], keyword?: string): SearchMatch | undefined => {
  if (!keyword || !languages) {
    return undefined;
  }
  for (const language of languages) {
    if (item.label && item.label[language] && item.label[language].toLowerCase().includes(keyword.toLowerCase())) {
      return { id: item.id, type: 'item', matchType: 'label', content: item.label[language] };
    } else if (item.description && item.description[language] && item.description[language].toLowerCase().includes(keyword.toLowerCase())) {
      return { id: item.id, type: 'item', matchType: 'description', content: item.description[language] };
    } else if (item.id.toLowerCase().includes(keyword.toLowerCase())) {
      return { id: item.id, type: 'item', matchType: 'id', content: item.id };
    } else if (item.activeWhen?.toLowerCase().includes(keyword.toLowerCase())) {
      return { id: item.id, type: 'item', matchType: 'visibility', content: item.activeWhen };
    } else if (item.required?.toLowerCase().includes(keyword.toLowerCase())) {
      return { id: item.id, type: 'item', matchType: 'requirement', content: item.required };
    } else if (item.validations?.some((validation) => (validation.message && validation.message[language] &&
      validation.message[language].toLowerCase().includes(keyword.toLowerCase())) ||
      (validation.rule && validation.rule.toLowerCase().includes(keyword.toLowerCase())))) {
      return { id: item.id, type: 'item', matchType: 'validation', content: item.validations.map(v => v.message ? v.message[language] : v.rule).join(', ') };
    }
  }
  return undefined;
}

export const matchVariableByKeyword = (variable: ContextVariable | Variable, keyword?: string): SearchMatch | undefined => {
  if (!keyword) {
    return undefined;
  }
  if (variable.name.toLowerCase().includes(keyword.toLowerCase())) {
    return { id: variable.name, type: 'variable', matchType: 'name', content: variable.name };
  } else if (variable.description?.toLowerCase().includes(keyword.toLowerCase())) {
    return { id: variable.name, type: 'variable', matchType: 'description', content: (variable as ContextVariable).description };
  } else if ((variable as ContextVariable).defaultValue?.toLowerCase().includes(keyword.toLowerCase())) {
    return { id: variable.name, type: 'variable', matchType: 'default', content: (variable as ContextVariable).defaultValue };
  } else if ((variable as Variable).expression?.toLowerCase().includes(keyword.toLowerCase())) {
    return { id: variable.name, type: 'variable', matchType: 'expression', content: (variable as Variable).expression };
  }
  return undefined;
}
