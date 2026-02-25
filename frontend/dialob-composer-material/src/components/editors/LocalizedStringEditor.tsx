import React from 'react';
import Markdown from 'react-markdown';
import { Button, Typography, Box } from '@mui/material';
import { Visibility } from '@mui/icons-material';
import { FormattedMessage } from 'react-intl';
import { useComposer } from '../../dialob';
import { markdownComponents } from '../../defaults/markdown';
import { IndexedRule } from './types';
import { getLanguageName } from '../../utils/TranslationUtils';
import { LocalizedString } from '../../types';
import { useSave } from '../../dialogs/contexts/saving/useSave';
import { MarkdownEditor } from './MarkdownEditor';
import remarkGfm from 'remark-gfm';
import { useEditor } from '../../editor';
import { useBackend } from '../../backend/useBackend';
import { TranslationEntry, TranslationResponse } from '../../backend/types';
import AITranslationIndicator from '../translations/AITranslationIndicator';
import TranslateButton from '../translations/TranslateButton';
import { useAITranslation } from '../translations';
import { buildItemLabelId, buildItemDescriptionId, buildValidationId } from '../../utils/TranslationUtils';


const LocalizedStringEditor: React.FC<{
  type: 'label' | 'description' | 'validations',
  rule?: IndexedRule,
  setRule?: React.Dispatch<React.SetStateAction<IndexedRule | undefined>>
}> = ({ type, rule, setRule }) => {
  const { form } = useComposer();
  const { savingState, updateLocalizedString, applyTranslations } = useSave();
  const { editor } = useEditor();
  const { translateEntries, config } = useBackend();
  const item = savingState.item;
  const formLanguages = form.metadata.languages;
  const [preview, setPreview] = React.useState(false);
  const [translating, setTranslating] = React.useState<Record<string, boolean>>({});
  const localizedString = type === 'validations' ? rule?.validationRule.message : item?.[type];
  const activeFormLanguage = editor.activeFormLanguage;

  if (!item || (type === 'validations' && rule === undefined)) {
    return null;
  }

  const getEntryId = (): string => {
    if (type === 'label') {
      return buildItemLabelId(item.id);
    } else if (type === 'description') {
      return buildItemDescriptionId(item.id);
    } else if (type === 'validations' && rule) {
      return buildValidationId(item.id, rule.index);
    }
    return '';
  };

  const { isAITranslated, getMetadata: getAITranslationMetadata, removeFlag } = useAITranslation(getEntryId());

  const handleUpdate = (value: string, language: string) => {
    const updatedLocalizedString: LocalizedString = {
      ...localizedString,
      [language]: value
    };
    
    if (isAITranslated(language)) {
      removeFlag(language);
    }

    if (type === 'validations' && rule && setRule) {
      const newRule = { ...rule, validationRule: { ...rule.validationRule, message: updatedLocalizedString } };
      setRule(newRule);
      updateLocalizedString(item.id, type, updatedLocalizedString, rule.index);
    } else {
      updateLocalizedString(item.id, type, updatedLocalizedString);
    }
  }

  const handleClearLanguage = (language: string) => {
    const updatedLocalizedString: LocalizedString = { ...localizedString };
    delete updatedLocalizedString[language];
    
    if (isAITranslated(language)) {
      removeFlag(language);
    }

    if (type === 'validations' && rule && setRule) {
      const newRule = { ...rule, validationRule: { ...rule.validationRule, message: updatedLocalizedString } };
      setRule(newRule);
      updateLocalizedString(item.id, type, updatedLocalizedString, rule.index);
    } else {
      updateLocalizedString(item.id, type, updatedLocalizedString);
    }
  }

  const handleTranslate = async (targetLanguage: string) => {
    if (!config.translationServiceUrl) {
      console.error('Translation service URL not configured');
      return;
    }

    const sourceText = localizedString?.[activeFormLanguage];
    if (!sourceText) {
      return;
    }

    setTranslating(prev => ({ ...prev, [targetLanguage]: true }));

    try {
      const entryId = getEntryId();
      const entry: TranslationEntry = {
        id: entryId,
        text: sourceText
      };

      const response = await translateEntries({
        sourceLanguage: activeFormLanguage,
        targetLanguage,
        entries: [entry]
      });

      if (response.success && response.result) {
        const translationResponse = response.result as TranslationResponse;
        if (translationResponse.translations && translationResponse.translations.length > 0) {
          applyTranslations(translationResponse.translations, activeFormLanguage, targetLanguage);
          
          const translation = translationResponse.translations[0];
          const updatedLocalizedString: LocalizedString = {
            ...localizedString,
            [targetLanguage]: translation.translatedText
          };
          
          if (type === 'validations' && rule && setRule) {
            const newRule = { ...rule, validationRule: { ...rule.validationRule, message: updatedLocalizedString } };
            setRule(newRule);
          }
        }
      } else if (response.apiError) {
        console.error('Translation failed:', response.apiError.message || response.apiError);
      }
    } catch (error) {
      console.error('Translation error:', error);
    } finally {
      setTranslating(prev => ({ ...prev, [targetLanguage]: false }));
    }
  }

  return (
    <>
      <Box display='flex' width='100%'>
        <Box flexGrow={1} />
        <Button variant={preview ? 'contained' : 'outlined'} endIcon={<Visibility />} onClick={() => setPreview(!preview)}>
          <FormattedMessage id='dialogs.options.preview' />
        </Button>
      </Box>
      {formLanguages?.map((language) => {
        const localizedText = localizedString?.[language];
        const isTranslating = translating[language];
        const canTranslate = language !== activeFormLanguage && 
                            localizedText === undefined && 
                            localizedString?.[activeFormLanguage] &&
                            config.translationServiceUrl;
        const aiMetadata = isAITranslated(language) ? getAITranslationMetadata(language) : null;
        
        return (
          <Box key={language}>
            <Box display='flex' alignItems='center' gap={1}>
              <Typography color='text.hint'>{getLanguageName(language)}</Typography>
              {aiMetadata && (
                <AITranslationIndicator 
                  metadata={aiMetadata} 
                  onClick={() => removeFlag(language)}
                />
              )}
              {canTranslate && (
                <TranslateButton
                  sourceLanguage={activeFormLanguage}
                  targetLanguage={language}
                  isTranslating={isTranslating}
                  onClick={() => handleTranslate(language)}
                />
              )}
            </Box>
            <Box sx={{ border: 1, borderRadius: 1, borderColor: 'divider', my: 1 }}>
              {preview ? <Markdown components={markdownComponents} remarkPlugins={[remarkGfm]}>{localizedText}</Markdown> :
              <MarkdownEditor value={localizedText} setValue={handleUpdate} language={language} onClear={handleClearLanguage} />}
            </Box>
          </Box>
        );
      })}
    </>
  );
}

export { LocalizedStringEditor };
