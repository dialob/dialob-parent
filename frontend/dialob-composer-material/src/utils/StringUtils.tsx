import { LocalizedString } from "../types";

export const cleanString = (str: string): string => {
  const nonBreakingSpaceRegex = /[\u00A0]/g;
  let cleanedStr = str;

  if (nonBreakingSpaceRegex.test(str)) {
    cleanedStr = str.replace(nonBreakingSpaceRegex, ' ');
  }

  const zeroWidthCharsRegex = /[\u200B-\u200D\uFEFF]/g;

  if (zeroWidthCharsRegex.test(cleanedStr)) {
    cleanedStr = cleanedStr.replace(zeroWidthCharsRegex, '');
  }

  return cleanedStr;
}

export const cleanLocalizedString = (str: LocalizedString): LocalizedString => {
  const cleanedStr = { ...str };
  Object.keys(str).map((key) => {
    cleanedStr[key] = cleanString(str[key]);
  });
  return cleanedStr;
}
