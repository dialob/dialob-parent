import { ContextVariable, DialobItem, DialobItems, Variable } from "../types";

export const isPage = (items: DialobItems, item: DialobItem): boolean => {
  return Object.values(items).find(i => i.type === 'questionnaire' && i.items && i.items.includes(item.id)) !== undefined;
}

export const isContextVariable = (variable: ContextVariable | Variable): variable is ContextVariable => (variable as ContextVariable).context === true;
