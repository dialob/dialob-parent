import React from 'react';
import { ValueSet } from '../../types';
import { useBackend } from '../../backend/useBackend';
import { useSave } from '../../dialogs/contexts/saving/useSave';
import { TranslationEntry, TranslationResponse } from '../../backend/types';
import { buildValueSetEntryId } from '../../utils/TranslationUtils';

interface TranslationProgress {
  current: number;
  total: number;
}

interface UseBulkTranslateValueSetReturn {
  translateAll: () => Promise<void>;
  isTranslating: boolean;
  progress: TranslationProgress;
}

export const useBulkTranslateValueSet = (
  valueSet: ValueSet | undefined,
  sourceLanguage: string,
  targetLanguages: string[]
): UseBulkTranslateValueSetReturn => {
  const { translateEntries } = useBackend();
  const { applyTranslations } = useSave();
  const [isTranslating, setIsTranslating] = React.useState(false);
  const [progress, setProgress] = React.useState<TranslationProgress>({ current: 0, total: 0 });

  const translateAll = React.useCallback(async () => {
    if (!valueSet || !valueSet.entries || valueSet.entries.length === 0) {
      return;
    }

    if (targetLanguages.length === 0) {
      return;
    }

    setIsTranslating(true);

    try {
      // Process each target language sequentially
      for (const targetLanguage of targetLanguages) {
        const entriesToTranslate: TranslationEntry[] = [];

        // Build entries that need translation
        valueSet.entries.forEach((entry, index) => {
          const sourceText = entry.label?.[sourceLanguage];
          const targetText = entry.label?.[targetLanguage];

          if (sourceText && !targetText) {
            entriesToTranslate.push({
              id: buildValueSetEntryId(valueSet.id, index, entry.id),
              text: sourceText
            });
          }
        });

        if (entriesToTranslate.length === 0) {
          continue;
        }

        setProgress({ 
          current: targetLanguages.indexOf(targetLanguage) + 1, 
          total: targetLanguages.length 
        });

        const response = await translateEntries({
          sourceLanguage,
          targetLanguage,
          entries: entriesToTranslate
        });

        if (response.success && response.result) {
          const translationResponse = response.result as TranslationResponse;
          const { translations } = translationResponse;
          
          // Apply all translations at once (including metadata)
          applyTranslations(translations, sourceLanguage, targetLanguage);
        } else if (response.apiError) {
          console.error('Translation failed:', response.apiError.message);
        }
      }
    } catch (error) {
      console.error('Translation error:', error);
    } finally {
      setIsTranslating(false);
      setProgress({ current: 0, total: 0 });
    }
  }, [valueSet, sourceLanguage, targetLanguages, translateEntries, applyTranslations]);

  return {
    translateAll,
    isTranslating,
    progress
  };
};
