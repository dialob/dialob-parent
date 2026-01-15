import React from 'react';
import { Alert, Box, Button, IconButton, TableBody, TableCell, TableContainer, TableHead, TableRow, Typography } from '@mui/material';
import { LanguagesTable } from './LanguageEditor';
import { MissingTranslation, TranslationType, getLanguageName, getMissingTranslations } from '../../utils/TranslationUtils';
import { useComposer } from '../../dialob';
import { FormattedMessage } from 'react-intl';
import { CheckCircle, Error, KeyboardArrowDown, KeyboardArrowRight, Translate } from '@mui/icons-material';
import { useEditor } from '../../editor';
import { useBackend } from '../../backend/useBackend';
import { TranslationEntry, TranslationResponse, TranslationResult } from '../../backend/types';
import { ComposerState, ValueSet } from '../../types';
import TranslateButton from './TranslateButton';
import TranslationProgressDialog from './TranslationProgressDialog';
import TranslationPreviewDialog from './TranslationPreviewDialog';

// Helper function to reconstruct proper entry ID from MissingTranslation
const getEntryIdFromMissingTranslation = (missing: MissingTranslation, translationType: TranslationType, form: ComposerState): string => {
  // Extract the actual entry ID from the display ID
  const parts = missing.id.split(' ');
  
  // For validations and valuesets, the actual ID is in parentheses
  if (parts.length > 1 && parts[1].startsWith('(') && parts[1].endsWith(')')) {
    const idInParens = parts[1].slice(1, -1); // Remove parentheses
    
    // For valuesets, need to add 'v:' prefix and reconstruct full ID
    if (translationType === 'valueset') {
      // idInParens format is "valueSetId:index"
      // We need "v:valueSetId:index:entryId"
      const [valueSetId, indexStr] = idInParens.split(':');
      const index = parseInt(indexStr, 10);
      const valueSet = form.valueSets?.find((vs: ValueSet) => vs.id === valueSetId);
      const entry = valueSet?.entries?.[index];
      if (entry) {
        return `v:${valueSetId}:${index}:${entry.id}`;
      }
      return idInParens; // Fallback
    }
    
    // For validations, format is "v:index", need "i:itemId:v:index"
    if (translationType === 'validation') {
      const itemId = parts[0].split('-')[0]; // Extract item ID from "itemId-ruleN"
      return `i:${itemId}:${idInParens}`;
    }
    
    return idInParens;
  }
  
  // For labels and descriptions, reconstruct the proper entry ID
  const itemId = missing.id;
  if (translationType === 'label') {
    return `i:${itemId}:l`;
  } else if (translationType === 'description') {
    return `i:${itemId}:d`;
  }
  
  return missing.id;
};

const MissingTranslationsCategory: React.FC<{ type: TranslationType, translations: MissingTranslation[] }> = ({ type, translations }) => {
  const { form, applyTranslations } = useComposer();
  const { editor, setActiveItem, setItemOptionsActiveTab, setActiveList } = useEditor();
  const { translateEntries, config } = useBackend();
  const languages = form.metadata.languages || [];
  const [expanded, setExpanded] = React.useState(false);
  const [translatingKey, setTranslatingKey] = React.useState<string | null>(null);

  const hasSourceText = (missing: MissingTranslation, translationType: TranslationType): boolean => {
    const sourceLanguage = editor.activeFormLanguage;
    const entryId = getEntryIdFromMissingTranslation(missing, translationType, form);
    const parts = entryId.split(':');
    
    if (parts[0] === 'i') {
      const itemId = parts[1];
      const item = form.data[itemId];
      if (!item) return false;

      if (parts[2] === 'l') {
        return !!item.label?.[sourceLanguage];
      } else if (parts[2] === 'd') {
        return !!item.description?.[sourceLanguage];
      } else if (parts[2] === 'v') {
        const validationIndex = parseInt(parts[3], 10);
        return !!item.validations?.[validationIndex]?.message?.[sourceLanguage];
      }
    } else if (parts[0] === 'v') {
      const valueSetId = parts[1];
      const entryIndex = parseInt(parts[2], 10);
      const valueSet = form.valueSets?.find(vs => vs.id === valueSetId);
      return !!valueSet?.entries?.[entryIndex]?.label?.[sourceLanguage];
    }
    
    return false;
  };

  const navigateToItem = (missing: MissingTranslation) => {
    if (missing.global) {
      const valueSetId = missing.id.split('-')[0];
      setActiveList(valueSetId);
    } else {
      const found = Object.values(form.data).find(item => item.id === missing.id.split('-')[0]);
      if (found) {
        setActiveItem(found);
        switch (type) {
          case 'label':
            setItemOptionsActiveTab('label');
            break;
          case 'description':
            setItemOptionsActiveTab('description');
            break;
          case 'valueset':
            setItemOptionsActiveTab('choices');
            break;
          case 'validation':
            setItemOptionsActiveTab('validations');
            break;
        }
      }
    }
  }

  const handleTranslate = async (missing: MissingTranslation, targetLanguage: string) => {
    const sourceLanguage = editor.activeFormLanguage;
    const entryId = getEntryIdFromMissingTranslation(missing, type, form);
    
    // Get source text based on entry type
    let sourceText: string | undefined;
    const parts = entryId.split(':');
    
    if (parts[0] === 'i') {
      // Item entry
      const itemId = parts[1];
      const item = form.data[itemId];
      if (!item) {
        console.error('Item not found:', itemId);
        return;
      }

      if (parts[2] === 'l') {
        sourceText = item.label?.[sourceLanguage];
      } else if (parts[2] === 'd') {
        sourceText = item.description?.[sourceLanguage];
      } else if (parts[2] === 'v') {
        const validationIndex = parseInt(parts[3], 10);
        sourceText = item.validations?.[validationIndex]?.message?.[sourceLanguage];
      }
    } else if (parts[0] === 'v') {
      // ValueSet entry
      const valueSetId = parts[1];
      const entryIndex = parseInt(parts[2], 10);
      const valueSet = form.valueSets?.find(vs => vs.id === valueSetId);
      sourceText = valueSet?.entries?.[entryIndex]?.label?.[sourceLanguage];
    }

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
              const entryId = getEntryIdFromMissingTranslation(missing, type, form);
              return (
                <TableRow key={idx}>
                  <TableCell width='70%'>
                    <Button variant='text' onClick={() => navigateToItem(missing)} sx={{ color: 'inherit', m: 0, justifyContent: 'flex-start', textTransform: 'none' }}>
                      {missing.id}
                    </Button>
                  </TableCell>
                  {languages.map(lang => {
                    const isMissing = missing.missingIn.includes(lang);
                    const isTranslating = translatingKey === `${entryId}:${lang}`;
                    const sourceTextExists = hasSourceText(missing, type);
                    const canTranslate = isMissing && lang !== editor.activeFormLanguage && sourceTextExists;

                    return (
                      <TableCell key={lang} align='center'>
                        <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 0.5 }}>
                          {isMissing ? (
                            canTranslate && config.translationServiceUrl ? <TranslateButton
                              sourceLanguage={editor.activeFormLanguage}
                              targetLanguage={lang}
                              isTranslating={isTranslating}
                              onClick={() => handleTranslate(missing, lang)}
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
        Object.entries(missingTranslations).forEach(([categoryType, categoryTranslations]) => {
          const translationType = categoryType as TranslationType;
          categoryTranslations?.forEach(missing => {
            if (!missing.missingIn.includes(targetLanguage)) {
              return; // Already has translation
            }

            // Reconstruct proper entry ID
            const entryId = getEntryIdFromMissingTranslation(missing, translationType, form);
            const idParts = entryId.split(':');
            let sourceText: string | undefined;

            if (idParts[0] === 'i') {
              // Item entry
              const itemId = idParts[1];
              const item = form.data[itemId];
              if (!item) return;

              if (idParts[2] === 'l') {
                sourceText = item.label?.[sourceLanguage];
              } else if (idParts[2] === 'd') {
                sourceText = item.description?.[sourceLanguage];
              } else if (idParts[2] === 'v') {
                const validationIndex = parseInt(idParts[3], 10);
                sourceText = item.validations?.[validationIndex]?.message?.[sourceLanguage];
              }
            } else if (idParts[0] === 'v') {
              // ValueSet entry
              const valueSetId = idParts[1];
              const entryIndex = parseInt(idParts[2], 10);
              const valueSet = form.valueSets?.find(vs => vs.id === valueSetId);
              sourceText = valueSet?.entries?.[entryIndex]?.label?.[sourceLanguage];
            }

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
