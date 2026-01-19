import React from 'react';
import { Alert, Box, Button, IconButton, TableBody, TableCell, TableContainer, TableHead, TableRow, Typography } from '@mui/material';
import { LanguagesTable } from './LanguageEditor';
import { 
  AITranslation, 
  TranslationType, 
  getAITranslations, 
  getLanguageName,
  parseEntryId,
  getEntryText,
  buildDisplayId,
  getAITranslationMetadata as getAITranslationMetadataUtil
} from '../../utils/TranslationUtils';
import { useComposer } from '../../dialob';
import { FormattedMessage } from 'react-intl';
import { KeyboardArrowDown, KeyboardArrowRight } from '@mui/icons-material';
import { useEditor } from '../../editor';
import AITranslationIndicator from './AITranslationIndicator';

const AITranslationsCategory: React.FC<{ type: TranslationType, translations: AITranslation[] }> = ({ type, translations }) => {
  const { form, removeAITranslation } = useComposer();
  const { setActiveItem, setItemOptionsActiveTab, setActiveList } = useEditor();
  const languages = form.metadata.languages || [];
  const [expanded, setExpanded] = React.useState(false);
  const aiTranslations = form.metadata.composer?.aiTranslations || [];

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
              const entryId = ai.id; // Now it's already in the correct format
              const displayId = buildDisplayId(entryId, form);
              
              return (
                <TableRow key={idx}>
                  <TableCell width='70%'>
                    <Button variant='text' onClick={() => navigateToItem(entryId)} sx={{ color: 'inherit', m: 0, justifyContent: 'flex-start', textTransform: 'none' }}>
                      {displayId}
                    </Button>
                  </TableCell>
                  {languages.map(lang => {
                    const hasAITranslation = ai.languages.includes(lang);
                    const aiMetadata = hasAITranslation ? getAITranslationMetadataUtil(entryId, lang, aiTranslations) : null;
                    const translatedText = hasAITranslation ? getEntryText(entryId, lang, form) : undefined;

                    return (
                      <TableCell key={lang} align='center'>
                        <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 0.5 }}>
                          {hasAITranslation ? (
                            <>
                              {aiMetadata && (
                                <AITranslationIndicator 
                                  metadata={aiMetadata} 
                                  translatedText={translatedText}
                                  onClick={() => removeAITranslation(entryId, lang)}
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
