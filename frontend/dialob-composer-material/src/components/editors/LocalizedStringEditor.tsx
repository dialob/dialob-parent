import React from 'react';
import Markdown from 'react-markdown';
import { Button, Typography, Box, TextareaAutosize } from '@mui/material';
import { Visibility } from '@mui/icons-material';
import { FormattedMessage } from 'react-intl';
import { useComposer } from '../../dialob';
import { markdownComponents } from '../../defaults/markdown';
import { IndexedRule } from './types';
import { getLanguageName } from '../../utils/TranslationUtils';
import { LocalizedString } from '../../types';
import { useSave } from '../../dialogs/contexts/saving/useSave';


const LocalizedStringEditor: React.FC<{
  type: 'label' | 'description' | 'validations',
  rule?: IndexedRule,
  setRule?: React.Dispatch<React.SetStateAction<IndexedRule | undefined>>
}> = ({ type, rule, setRule }) => {
  const { form } = useComposer();
  const { savingState, updateLocalizedString } = useSave();
  const item = savingState.item;
  const formLanguages = form.metadata.languages;
  const [preview, setPreview] = React.useState(false);
  const localizedString = type === 'validations' ? rule?.validationRule.message : item[type];

  if (!item || (type === 'validations' && rule === undefined)) {
    return null;
  }

  const handleUpdate = (value: string, language: string) => {
    if (value !== '') {
      const updatedLocalizedString: LocalizedString = {
        ...localizedString,
        [language]: value
      };
      if (type === 'validations' && rule && setRule) {
        const newRule = { ...rule, validationRule: { ...rule.validationRule, message: updatedLocalizedString } };
        setRule(newRule);
        updateLocalizedString(item.id, type, updatedLocalizedString, rule.index);
      } else {
        updateLocalizedString(item.id, type, updatedLocalizedString);
      }
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
        return (
          <Box key={language}>
            <Box display='flex' alignItems='center'>
              <Typography color='text.hint'>{getLanguageName(language)}</Typography>
            </Box>
            {preview ?
              <Box sx={{ border: 1, borderRadius: 0.5, borderColor: 'text.secondary' }}>
                <Markdown skipHtml components={markdownComponents}>{localizedText}</Markdown>
              </Box > :
              <TextareaAutosize style={{ width: '100%', maxWidth: '100%' }} minRows={5} value={localizedText} onChange={(e) => handleUpdate(e.target.value, language)} />}
          </Box>
        );
      })}
    </>
  );
}

export { LocalizedStringEditor };
