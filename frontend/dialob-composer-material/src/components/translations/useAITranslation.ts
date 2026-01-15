import { useComposer } from '../../dialob';
import { useSave } from '../../dialogs/contexts/saving/useSave';
import { isAITranslated as isAITranslatedUtil, getAITranslationMetadata as getAITranslationMetadataUtil } from '../../utils/TranslationUtils';
import { TranslationMetadata } from '../../types';

interface UseAITranslationReturn {
  isAITranslated: (language: string) => boolean;
  getMetadata: (language: string) => TranslationMetadata | undefined;
  removeFlag: (language: string) => void;
}

export const useAITranslation = (entryId: string): UseAITranslationReturn => {
  const { form } = useComposer();
  const { savingState, removeAITranslation } = useSave();

  // Get AI translations from saving context first, fall back to form metadata
  const aiTranslations = savingState.composerMetadata?.aiTranslations || form.metadata.composer?.aiTranslations || [];

  const isAITranslated = (language: string): boolean => {
    return isAITranslatedUtil(entryId, language, aiTranslations);
  };

  const getMetadata = (language: string): TranslationMetadata | undefined => {
    return getAITranslationMetadataUtil(entryId, language, aiTranslations);
  };

  const removeFlag = (language: string): void => {
    removeAITranslation(entryId, language);
  };

  return {
    isAITranslated,
    getMetadata,
    removeFlag
  };
};
