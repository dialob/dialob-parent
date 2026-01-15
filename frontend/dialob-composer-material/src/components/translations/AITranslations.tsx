import React from 'react';
import { Alert, Box, Button, IconButton, TableBody, TableCell, TableContainer, TableHead, TableRow, Typography } from '@mui/material';
import { LanguagesTable } from './LanguageEditor';
import { AITranslation, TranslationType, getAITranslations, getLanguageName } from '../../utils/TranslationUtils';
import { useComposer } from '../../dialob';
import { FormattedMessage } from 'react-intl';
import { KeyboardArrowDown, KeyboardArrowRight } from '@mui/icons-material';
import { useEditor } from '../../editor';
import AITranslationIndicator from './AITranslationIndicator';
import { TranslationMetadata } from '../../types';

const AITranslationsCategory: React.FC<{ type: TranslationType, translations: AITranslation[] }> = ({ type, translations }) => {
  const { form, removeAITranslation } = useComposer();
  const { setActiveItem, setItemOptionsActiveTab, setActiveList } = useEditor();
  const languages = form.metadata.languages || [];
  const [expanded, setExpanded] = React.useState(false);

  const getAITranslationMetadata = (entryId: string, language: string): TranslationMetadata | undefined => {
    const aiTranslations = form.metadata.composer?.aiTranslations || [];
    return aiTranslations.find(t => t.entryId === entryId && t.targetLanguage === language);
  };

  const getTranslatedText = (entryId: string, language: string): string | undefined => {
    const parts = entryId.split(':');
    
    if (parts[0] === 'i') {
      const itemId = parts[1];
      const item = form.data[itemId];
      if (!item) return undefined;

      if (parts[2] === 'l') {
        return item.label?.[language];
      } else if (parts[2] === 'd') {
        return item.description?.[language];
      } else if (parts[2] === 'v') {
        const validationIndex = parseInt(parts[3], 10);
        return item.validations?.[validationIndex]?.message?.[language];
      }
    } else if (parts[0] === 'v') {
      const valueSetId = parts[1];
      const entryIndex = parseInt(parts[2], 10);
      const valueSet = form.valueSets?.find(vs => vs.id === valueSetId);
      return valueSet?.entries?.[entryIndex]?.label?.[language];
    }
    
    return undefined;
  };

  const handleRemoveAIFlag = (entryId: string, language: string) => {
    removeAITranslation(entryId, language);
  };

  const getEntryIdFromAITranslation = (ai: AITranslation, translationType: TranslationType): string => {
    // Extract the actual entry ID from the display ID
    const parts = ai.id.split(' ');
    
    // For validations and valuesets, the actual ID is in parentheses
    if (parts.length > 1 && parts[1].startsWith('(') && parts[1].endsWith(')')) {
      const idInParens = parts[1].slice(1, -1); // Remove parentheses
      
      // For valuesets, need to add 'v:' prefix and reconstruct full ID
      if (translationType === 'valueset') {
        // idInParens format is "valueSetId:index", need "v:valueSetId:index:entryId"
        const [valueSetId, indexStr] = idInParens.split(':');
        const index = parseInt(indexStr, 10);
        const valueSet = form.valueSets?.find(vs => vs.id === valueSetId);
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
    
    // For labels and descriptions, reconstruct the entry ID
    const itemId = ai.id;
    if (translationType === 'label') {
      return `i:${itemId}:l`;
    } else if (translationType === 'description') {
      return `i:${itemId}:d`;
    }
    
    return ai.id;
  };

  const navigateToItem = (ai: AITranslation) => {
    if (ai.global) {
      const valueSetId = ai.id.split('-')[0];
      setActiveList(valueSetId);
    } else {
      const found = Object.values(form.data).find(item => item.id === ai.id.split('-')[0]);
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
            {translations.map((ai, idx) => {
              const entryId = getEntryIdFromAITranslation(ai, type);
              return (
                <TableRow key={idx}>
                  <TableCell width='70%'>
                    <Button variant='text' onClick={() => navigateToItem(ai)} sx={{ color: 'inherit', m: 0, justifyContent: 'flex-start', textTransform: 'none' }}>
                      {ai.id}
                    </Button>
                  </TableCell>
                  {languages.map(lang => {
                    const hasAITranslation = ai.languages.includes(lang);
                    const aiMetadata = hasAITranslation ? getAITranslationMetadata(entryId, lang) : null;
                    const translatedText = hasAITranslation ? getTranslatedText(entryId, lang) : undefined;

                    return (
                      <TableCell key={lang} align='center'>
                        <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 0.5 }}>
                          {hasAITranslation ? (
                            <>
                              {aiMetadata && (
                                <AITranslationIndicator 
                                  metadata={aiMetadata} 
                                  translatedText={translatedText}
                                  onClick={() => handleRemoveAIFlag(entryId, lang)}
                                />
                              )}
                            </>
                          ) : (
                            <Box sx={{ width: 24, height: 24 }} /> // Empty placeholder for alignment
                          )}
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
  );
};

const AITranslations: React.FC = () => {
  const { form } = useComposer();
  const aiTranslations = getAITranslations(form);

  if (!aiTranslations) {
    return <Alert severity='info'><FormattedMessage id='dialogs.translations.ai.none' /></Alert>
  }

  const categories = Object.keys(aiTranslations).map(t => {
    const type = t as TranslationType;
    const translations = aiTranslations[type];
    if (translations && translations.length > 0) {
      return <AITranslationsCategory key={type} type={type} translations={translations} />
    }
    return null;
  });

  return (
    <Box sx={{ pb: 2 }}>
      {categories}
    </Box>
  );
};

export { AITranslations };
