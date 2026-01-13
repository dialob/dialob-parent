import React from 'react';
import Markdown from 'react-markdown';
import { Button, Typography, Box, IconButton, Tooltip, CircularProgress, Chip } from '@mui/material';
import { Visibility, Translate, SmartToy } from '@mui/icons-material';
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


const LocalizedStringEditor: React.FC<{
  type: 'label' | 'description' | 'validations',
  rule?: IndexedRule,
  setRule?: React.Dispatch<React.SetStateAction<IndexedRule | undefined>>
}> = ({ type, rule, setRule }) => {
  const { form } = useComposer();
  const { savingState, updateLocalizedString, addAITranslation, removeAITranslation } = useSave();
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
      return `i:${item.id}:l`;
    } else if (type === 'description') {
      return `i:${item.id}:d`;
    } else if (type === 'validations' && rule) {
      return `i:${item.id}:v:${rule.index}`;
    }
    return '';
  };

  const isAITranslated = (language: string): boolean => {
    const entryId = getEntryId();
    // Check in saving context first, fall back to form metadata
    const aiTranslations = savingState.composerMetadata?.aiTranslations || form.metadata.composer?.aiTranslations || [];
    return !!aiTranslations.some(
      t => t.entryId === entryId && t.targetLanguage === language
    );
  };

  const handleUpdate = (value: string, language: string) => {
    const updatedLocalizedString: LocalizedString = {
      ...localizedString,
      [language]: value
    };
    
    // Remove AI translation flag if user manually edits
    const entryId = getEntryId();
    if (isAITranslated(language)) {
      removeAITranslation(entryId);
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
          const translation = translationResponse.translations[0];
          
          // Update the localized string in saving context
          const updatedLocalizedString: LocalizedString = {
            ...localizedString,
            [targetLanguage]: translation.translatedText
          };
          
          if (type === 'validations' && rule && setRule) {
            const newRule = { ...rule, validationRule: { ...rule.validationRule, message: updatedLocalizedString } };
            setRule(newRule);
            updateLocalizedString(item.id, type, updatedLocalizedString, rule.index);
          } else {
            updateLocalizedString(item.id, type, updatedLocalizedString);
          }
          
          // Add AI translation metadata in saving context
          addAITranslation(entryId, activeFormLanguage, targetLanguage);
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
        const localizedText = localizedString ? localizedString[language] : '';
        const isTranslating = translating[language];
        const canTranslate = language !== activeFormLanguage && 
                            !localizedText && 
                            localizedString?.[activeFormLanguage] &&
                            config.translationServiceUrl;
        const showAIIndicator = isAITranslated(language);
        
        return (
          <Box key={language}>
            <Box display='flex' alignItems='center' gap={1}>
              <Typography color='text.hint'>{getLanguageName(language)}</Typography>
              {showAIIndicator && (
                <Tooltip title="AI Translated">
                  <Chip 
                    icon={<SmartToy fontSize="small" />} 
                    label="AI" 
                    size="small" 
                    variant="outlined"
                    sx={{ height: 20 }}
                  />
                </Tooltip>
              )}
              {canTranslate && (
                <Tooltip title={`Translate from ${getLanguageName(activeFormLanguage)}`}>
                  <span>
                    <IconButton 
                      size="small" 
                      onClick={() => handleTranslate(language)}
                      disabled={isTranslating}
                    >
                      {isTranslating ? <CircularProgress size={20} /> : <Translate fontSize="small" />}
                    </IconButton>
                  </span>
                </Tooltip>
              )}
            </Box>
            <Box sx={{ border: 1, borderRadius: 1, borderColor: 'divider', my: 1 }}>
              {preview ? <Markdown components={markdownComponents} remarkPlugins={[remarkGfm]}>{localizedText}</Markdown> :
              <MarkdownEditor value={localizedText} setValue={handleUpdate} language={language} />}
            </Box>
          </Box>
        );
      })}
    </>
  );
}

export { LocalizedStringEditor };
