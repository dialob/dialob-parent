import React from 'react';
import { Alert, Box, Button, IconButton, TableBody, TableCell, TableContainer, TableHead, TableRow, Typography } from '@mui/material';
import { LanguagesTable } from './LanguageEditor';
import { 
  MissingTranslation, 
  TranslationType, 
  getLanguageName, 
  getMissingTranslations,
  parseEntryId,
  getEntryText,
  buildDisplayId
} from '../../utils/TranslationUtils';
import { useComposer } from '../../dialob';
import { FormattedMessage } from 'react-intl';
import { CheckCircle, Error, KeyboardArrowDown, KeyboardArrowRight, Translate } from '@mui/icons-material';
import { useEditor } from '../../editor';
import { useBackend } from '../../backend/useBackend';
import { TranslationEntry, TranslationResponse, TranslationResult } from '../../backend/types';
import TranslateButton from './TranslateButton';
import TranslationProgressDialog from './TranslationProgressDialog';
import TranslationPreviewDialog from './TranslationPreviewDialog';

const MissingTranslationsCategory: React.FC<{ type: TranslationType, translations: MissingTranslation[] }> = ({ type, translations }) => {
  const { form, applyTranslations } = useComposer();
  const { editor, setActiveItem, setItemOptionsActiveTab, setActiveList } = useEditor();
  const { translateEntries, config } = useBackend();
  const languages = form.metadata.languages || [];
  const [expanded, setExpanded] = React.useState(false);
  const [translatingKey, setTranslatingKey] = React.useState<string | null>(null);

  const hasSourceText = (entryId: string): boolean => {
    const sourceLanguage = editor.activeFormLanguage;
    const sourceText = getEntryText(entryId, sourceLanguage, form);
    return !!sourceText;
  };

  const navigateToItem = (entryId: string) => {
    const parsed = parseEntryId(entryId);
    if (!parsed) return;

    if (parsed.type === 'valueset') {
      // Check if it's a global valueset
      const gvs = form.metadata.composer?.globalValueSets?.find(v => v.valueSetId === parsed.valueSetId);
      if (gvs) {
        setActiveList(parsed.valueSetId);
        return;
      }
    }

    // Navigate to item
    if (parsed.type === 'item') {
      const item = form.data[parsed.itemId];
      if (item) {
        setActiveItem(item);
        switch (parsed.subType) {
          case 'label':
            setItemOptionsActiveTab('label');
            break;
          case 'description':
            setItemOptionsActiveTab('description');
            break;
          case 'validation':
            setItemOptionsActiveTab('validations');
            break;
        }
      }
    } else if (parsed.type === 'valueset') {
      // Find the item that uses this valueset
      const item = Object.values(form.data).find(i => i.valueSetId === parsed.valueSetId);
      if (item) {
        setActiveItem(item);
        setItemOptionsActiveTab('choices');
      }
    }
  };

  const handleTranslate = async (entryId: string, targetLanguage: string) => {
    const sourceLanguage = editor.activeFormLanguage;
    const sourceText = getEntryText(entryId, sourceLanguage, form);

    if (!sourceText) {
      console.error('Source text not found for entry:', entryId, 'in language:', sourceLanguage);
      return;
    }

    setTranslatingKey(`${entryId}:${targetLanguage}`);

    try {
      const translationEntry: TranslationEntry = {
        id: entryId,
        text: sourceText
      };

      const response = await translateEntries({
        sourceLanguage,
        targetLanguage,
        entries: [translationEntry]
      });

      if (response.success && response.result) {
        const translationResponse = response.result as TranslationResponse;
        const { translations } = translationResponse;
        
        applyTranslations(translations, sourceLanguage, targetLanguage);
      } else if (response.apiError) {
        console.error('Translation failed:', response.apiError.message);
      }
    } catch (error) {
      console.error('Translation error:', error);
    } finally {
      setTranslatingKey(null);
    }
  };

  return (
    <>
      <Box sx={{ mb: 2, display: 'flex', alignItems: 'center' }}>
        <Typography variant='h5' fontWeight='bold'>
          <FormattedMessage id={`dialogs.translations.types.${type}`} />
        </Typography>
        <IconButton onClick={() => setExpanded(!expanded)}>{expanded ? <KeyboardArrowDown /> : <KeyboardArrowRight />}</IconButton>
      </Box>
      {expanded && <TableContainer sx={{ mb: 2 }}>
        <LanguagesTable>
          <TableHead>
            <TableRow>
              <TableCell width='70%' sx={{ fontWeight: 'bold' }}>ID</TableCell>
              {languages.map(lang => (
                <TableCell key={lang} align='center' sx={{ fontWeight: 'bold' }}>{getLanguageName(lang)}</TableCell>
              ))}
            </TableRow>
          </TableHead>
          <TableBody>
            {translations.map((missing, idx) => {
              const entryId = missing.id; // Now it's already in the correct format
              const displayId = buildDisplayId(entryId, form);
              
              return (
                <TableRow key={idx}>
                  <TableCell width='70%'>
                    <Button variant='text' onClick={() => navigateToItem(entryId)} sx={{ color: 'inherit', m: 0, justifyContent: 'flex-start', textTransform: 'none' }}>
                      {displayId}
                    </Button>
                  </TableCell>
                  {languages.map(lang => {
                    const isMissing = missing.missingIn.includes(lang);
                    const isTranslating = translatingKey === `${entryId}:${lang}`;
                    const sourceTextExists = hasSourceText(entryId);
                    const canTranslate = isMissing && lang !== editor.activeFormLanguage && sourceTextExists;

                    return (
                      <TableCell key={lang} align='center'>
                        <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 0.5 }}>
                          {isMissing ? (
                            canTranslate && config.translationServiceUrl ? <TranslateButton
                              sourceLanguage={editor.activeFormLanguage}
                              targetLanguage={lang}
                              isTranslating={isTranslating}
                              onClick={() => handleTranslate(entryId, lang)}
                              color='error'
                            /> : <Error color='error' />
                          ) : <CheckCircle color='success' />}
                        </Box>
                      </TableCell>
                    );
                  })}
                </TableRow>
              );
            })}
          </TableBody>
        </LanguagesTable>
      </TableContainer>}
    </>
  )
}

const MissingTranslations: React.FC = () => {
  const { form, applyTranslations } = useComposer();
  const { editor } = useEditor();
  const { translateEntries, config } = useBackend();
  const [isTranslatingAll, setIsTranslatingAll] = React.useState(false);
  const [translationProgress, setTranslationProgress] = React.useState({ current: 0, total: 0 });
  const [previewDialogOpen, setPreviewDialogOpen] = React.useState(false);
  const [collectedTranslations, setCollectedTranslations] = React.useState<Map<string, { translations: TranslationResult[], sourceLanguage: string, sourceTexts: Map<string, string> }>>(new Map());
  const missingTranslations = getMissingTranslations(form);

  const handleTranslateAll = async () => {
    if (!missingTranslations) return;

    const sourceLanguage = editor.activeFormLanguage;
    const languages = form.metadata.languages || [];
    const targetLanguages = languages.filter(lang => lang !== sourceLanguage);

    if (targetLanguages.length === 0) {
      return;
    }

    setIsTranslatingAll(true);
    const translationsByLanguage = new Map<string, { translations: TranslationResult[], sourceLanguage: string, sourceTexts: Map<string, string> }>();
    const sourceTextsMap = new Map<string, string>(); // Store source texts for all entries

    try {
      // Process each target language sequentially
      for (const targetLanguage of targetLanguages) {
        const entriesToTranslate: TranslationEntry[] = [];

        // Collect all missing entries for this target language
        Object.values(missingTranslations).forEach(categoryTranslations => {
          categoryTranslations?.forEach(missing => {
            if (!missing.missingIn.includes(targetLanguage)) {
              return; // Already has translation
            }

            const entryId = missing.id;
            const sourceText = getEntryText(entryId, sourceLanguage, form);

            if (sourceText) {
              entriesToTranslate.push({
                id: entryId,
                text: sourceText
              });
              // Store source text for this entry
              if (!sourceTextsMap.has(entryId)) {
                sourceTextsMap.set(entryId, sourceText);
              }
            }
          });
        });

        if (entriesToTranslate.length === 0) {
          continue;
        }

        setTranslationProgress({ 
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
          
          // Collect translations instead of applying them immediately
          translationsByLanguage.set(targetLanguage, {
            translations,
            sourceLanguage,
            sourceTexts: sourceTextsMap
          });
        } else if (response.apiError) {
          console.error('Translation failed:', response.apiError.message);
        }
      }

      // Show preview dialog with all collected translations
      if (translationsByLanguage.size > 0) {
        setCollectedTranslations(translationsByLanguage);
        setPreviewDialogOpen(true);
      }
    } catch (error) {
      console.error('Translation error:', error);
    } finally {
      setIsTranslatingAll(false);
      setTranslationProgress({ current: 0, total: 0 });
    }
  };

  const handleSaveTranslations = () => {
    collectedTranslations.forEach(({ translations, sourceLanguage }, targetLanguage) => {
      applyTranslations(translations, sourceLanguage, targetLanguage);
    });
    
    setPreviewDialogOpen(false);
    setCollectedTranslations(new Map());
  };

  const handleCancelTranslations = () => {
    setPreviewDialogOpen(false);
    setCollectedTranslations(new Map());
  };

  if (!missingTranslations) {
    return <Alert severity='success'><FormattedMessage id='dialogs.translations.missing.none' /></Alert>
  }

  const categories = Object.keys(missingTranslations).map(t => {
    const type = t as TranslationType;
    const translations = missingTranslations[type];
    if (translations && translations.length > 0) {
      return <MissingTranslationsCategory key={type} type={type} translations={translations} />
    }
  });

  return (
    <Box sx={{ pb: 2 }}>
      {config.translationServiceUrl && (
        <Box sx={{ display: 'flex', justifyContent: 'flex-end', mb: 2 }}>
          <Button
            variant="contained"
            startIcon={<Translate />}
            onClick={handleTranslateAll}
            disabled={isTranslatingAll}
          >
            <FormattedMessage id='dialogs.translations.missing.translateAll' />
          </Button>
        </Box>
      )}
      
      <TranslationProgressDialog
        open={isTranslatingAll}
        current={translationProgress.current}
        total={translationProgress.total}
      />

      <TranslationPreviewDialog
        open={previewDialogOpen}
        translations={collectedTranslations}
        onCancel={handleCancelTranslations}
        onSave={handleSaveTranslations}
      />

      {categories}
    </Box>
  );
};

export { MissingTranslations };
