import { ContextVariable, DialobItem, Variable } from "../dialob";

export const matchItemByKeyword = (item: DialobItem, languages?: string[], keyword?: string) => {
  if (!keyword || !languages) {
    return true;
  }
  for (const language of languages) {
    if (item.label && item.label[language] && item.label[language].toLowerCase().includes(keyword.toLowerCase())) {
      return true;
    } else if (item.description && item.description[language] && item.description[language].toLowerCase().includes(keyword.toLowerCase())) {
      return true;
    } else if (item.id.toLowerCase().includes(keyword.toLowerCase())) {
      return true;
    } else if (item.activeWhen?.toLowerCase().includes(keyword.toLowerCase())) {
      return true;
    } else if (item.required?.toLowerCase().includes(keyword.toLowerCase())) {
      return true;
    } else if (item.validations?.some((validation) => (validation.message && validation.message[language] &&
      validation.message[language].toLowerCase().includes(keyword.toLowerCase())) ||
      (validation.rule && validation.rule.toLowerCase().includes(keyword.toLowerCase())))) {
      return true;
    }
  }
  return false;
}

export const matchVariableByKeyword = (variable: ContextVariable | Variable, keyword?: string) => {
  if (!keyword) {
    return true;
  }
  if (variable.name.toLowerCase().includes(keyword.toLowerCase())) {
    return true;
  } else if ((variable as ContextVariable).defaultValue?.toLowerCase().includes(keyword.toLowerCase())) {
    return true;
  } else if ((variable as Variable).expression?.toLowerCase().includes(keyword.toLowerCase())) {
    return true;
  }
  return false;
}
