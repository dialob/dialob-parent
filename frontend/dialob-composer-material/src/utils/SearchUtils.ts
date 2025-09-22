import { ContextVariable, DialobItem, Variable } from "../types";

export type ItemMatchType = 'label' | 'description' | 'id' | 'visibility' | 'requirement' | 'validation'; 
export type VariableMatchType = 'name' | 'description' | 'default' | 'expression';

export interface SearchMatch {
  type: 'item' | 'variable';
  matchType: ItemMatchType | VariableMatchType;
  content?: string;
  id: string;
}

const extractSnippet = (text: string, keyword: string, context: number = 15): string => {
  const idx = text.indexOf(keyword);
  const start = Math.max(0, idx - context);
  const end = Math.min(text.length, idx + keyword.length + context);

  let snippet = text.slice(start, end);

  const regex = new RegExp(`(${keyword})`, "gi");
  snippet = snippet.replace(regex, (match) => match.toUpperCase());

  const prefix = start > 0 ? "..." : "";
  const suffix = end < text.length ? "..." : "";

  return prefix + snippet + suffix;
}

export const matchItemByKeyword = (item: DialobItem, languages?: string[], keyword?: string): SearchMatch | undefined => {
  if (!keyword || !languages) {
    return undefined;
  }
  for (const language of languages) {
    if (item.label && item.label[language] && item.label[language].toLowerCase().includes(keyword.toLowerCase())) {
      const snippet = extractSnippet(item.label[language].toLowerCase(), keyword.toLowerCase());
      return { id: item.id, type: 'item', matchType: 'label', content: snippet };
    } else if (item.description && item.description[language] && item.description[language].toLowerCase().includes(keyword.toLowerCase())) {
      const snippet = extractSnippet(item.description[language].toLowerCase(), keyword.toLowerCase());
      return { id: item.id, type: 'item', matchType: 'description', content: snippet };
    } else if (item.id.toLowerCase().includes(keyword.toLowerCase())) {
      const snippet = extractSnippet(item.id.toLowerCase(), keyword.toLowerCase());
      return { id: item.id, type: 'item', matchType: 'id', content: snippet };
    } else if (item.activeWhen?.toLowerCase().includes(keyword.toLowerCase())) {
      const snippet = extractSnippet(item.activeWhen.toLowerCase(), keyword.toLowerCase());
      return { id: item.id, type: 'item', matchType: 'visibility', content: snippet };
    } else if (item.required?.toLowerCase().includes(keyword.toLowerCase())) {
      const snippet = extractSnippet(item.required.toLowerCase(), keyword.toLowerCase());
      return { id: item.id, type: 'item', matchType: 'requirement', content: snippet };
    } else if (item.validations?.some((validation) => validation.message && validation.message[language] && validation.message[language].toLowerCase().includes(keyword.toLowerCase()))) {
      const snippet = extractSnippet(item.validations.map(v => v.message ? v.message[language] : '').join(', ').toLowerCase(), keyword.toLowerCase());
      return { id: item.id, type: 'item', matchType: 'validation', content: snippet };
    } else if (item.validations?.some((validation) => validation.rule && validation.rule.toLowerCase().includes(keyword.toLowerCase()))) {
      const snippet = extractSnippet(item.validations.map(v => v.rule).join(', ').toLowerCase(), keyword.toLowerCase());
      return { id: item.id, type: 'item', matchType: 'validation', content: snippet };
    }
  }
  return undefined;
}

export const matchVariableByKeyword = (variable: ContextVariable | Variable, keyword?: string): SearchMatch | undefined => {
  if (!keyword) {
    return undefined;
  }
  if (variable.name.toLowerCase().includes(keyword.toLowerCase())) {
    const snippet = extractSnippet(variable.name.toLowerCase(), keyword.toLowerCase());
    return { id: variable.name, type: 'variable', matchType: 'name', content: snippet };
  } else if (variable.description?.toLowerCase().includes(keyword.toLowerCase())) {
    const snippet = extractSnippet(variable.description.toLowerCase(), keyword.toLowerCase());
    return { id: variable.name, type: 'variable', matchType: 'description', content: snippet };
  } else if ((variable as ContextVariable).defaultValue?.toLowerCase().includes(keyword.toLowerCase())) {
    const snippet = extractSnippet((variable as ContextVariable).defaultValue.toLowerCase(), keyword.toLowerCase());
    return { id: variable.name, type: 'variable', matchType: 'default', content: snippet };
  } else if ((variable as Variable).expression?.toLowerCase().includes(keyword.toLowerCase())) {
    const snippet = extractSnippet((variable as Variable).expression.toLowerCase(), keyword.toLowerCase());
    return { id: variable.name, type: 'variable', matchType: 'expression', content: snippet };
  }
  return undefined;
}
