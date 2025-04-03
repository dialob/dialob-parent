import React from 'react';
import { TableContainer, TableHead, TableRow, TableCell, TableBody, Typography, Box, IconButton, Button, Alert } from '@mui/material';
import { LanguagesTable } from './LanguageEditor';
import { MissingTranslation, TranslationType, getLanguageName, getMissingTranslations } from '../../utils/TranslationUtils';
import { useComposer } from '../../dialob';
import { FormattedMessage } from 'react-intl';
import { CheckCircle, Error, KeyboardArrowDown, KeyboardArrowRight } from '@mui/icons-material';
import { useEditor } from '../../editor';

const MissingTranslationsCategory: React.FC<{ type: TranslationType, translations: MissingTranslation[] }> = ({ type, translations }) => {
  const { form } = useComposer();
  const { setActiveItem, setItemOptionsActiveTab, setActiveList } = useEditor();
  const languages = form.metadata.languages || [];
  const [expanded, setExpanded] = React.useState(false);

  const navigateToItem = (missing: MissingTranslation) => {
    if (missing.global) {
      setActiveList('global');
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
            {translations.map((missing, idx) => (
              <TableRow key={idx}>
                <TableCell width='70%'>
                  <Button variant='text' onClick={() => navigateToItem(missing)} sx={{ color: 'inherit', m: 0, justifyContent: 'flex-start', textTransform: 'none' }}>
                    {missing.id}
                  </Button>
                </TableCell>
                {languages.map(lang => (
                  <TableCell key={lang} align='center'>
                    {missing.missingIn.includes(lang) ? <Error color='error' /> : <CheckCircle color='success' />}
                  </TableCell>
                ))}
              </TableRow>
            ))}
          </TableBody>
        </LanguagesTable>
      </TableContainer>}
    </>
  )
}

const MissingTranslations: React.FC = () => {
  const { form } = useComposer();
  const missingTranslations = getMissingTranslations(form);

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
      {categories}
    </Box>
  );
};

export { MissingTranslations };
