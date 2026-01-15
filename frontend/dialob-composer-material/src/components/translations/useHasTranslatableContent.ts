import React from 'react';
import { ValueSet } from '../../types';


export const useHasTranslatableContent = (
  valueSet: ValueSet | undefined,
  sourceLanguage: string,
  targetLanguages: string[]
): boolean => {
  return React.useMemo(() => {
    if (!valueSet || !valueSet.entries || valueSet.entries.length === 0) {
      return false;
    }
    
    if (targetLanguages.length === 0) {
      return false;
    }
    
    // Check if any entry has source text but is missing target language translations
    return valueSet.entries.some(entry => {
      const sourceText = entry.label?.[sourceLanguage];
      if (!sourceText) {
        return false;
      }
      
      // Check if any target language is missing translation
      return targetLanguages.some(targetLang => !entry.label?.[targetLang]);
    });
  }, [valueSet, sourceLanguage, targetLanguages]);
};
