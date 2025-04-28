import React from 'react';
import Markdown from 'react-markdown';
import { Button, Typography, Box, TextareaAutosize } from '@mui/material';
import { Visibility } from '@mui/icons-material';
import { FormattedMessage } from 'react-intl';
import { useComposer } from '../../dialob';
import { useEditor } from '../../editor';
import { markdownComponents } from '../../defaults/markdown';
import { IndexedRule } from './types';
import { getLanguageName } from '../../utils/TranslationUtils';
import { LocalizedString } from '../../types';


const LocalizedStringEditor: React.FC<{
  type: 'label' | 'description' | 'validations',
  rule?: IndexedRule,
  setRule?: React.Dispatch<React.SetStateAction<IndexedRule | undefined>>
}> = ({ type, rule, setRule }) => {
  const { form, updateLocalizedString } = useComposer();
  const { editor, setActiveItem } = useEditor();
  const item = editor.activeItem;
  const formLanguages = form.metadata.languages;
  const [preview, setPreview] = React.useState(false);
  const [localizedString, setLocalizedString] = React.useState<LocalizedString | undefined>();

  React.useEffect(() => {
    if (item) {
      setLocalizedString(type === 'validations' ? rule?.validationRule.message : item[type]);
    }
  }, [item, rule, type]);

  React.useEffect(() => {
    if (item && localizedString && localizedString !== (type === 'validations' ? rule?.validationRule.message : item[type])) {
      const id = setTimeout(() => {
        updateLocalizedString(item.id, type, localizedString, rule?.index);
        if (type === 'validations' && rule && setRule && item) {
          const newRule = { ...rule, validationRule: { ...rule.validationRule, message: localizedString } };
          setRule(newRule);
          const newValidations = item.validations?.map((r, index) => index === rule.index ? newRule.validationRule : r);
          setActiveItem({ ...item, validations: newValidations });
        } else {
          setActiveItem({ ...item, [type]: localizedString });
        }
      }, 300);
      return () => clearTimeout(id);
    }
  }, [localizedString]);

  if (!item || (type === 'validations' && rule === undefined)) {
    return null;
  }

  const handleUpdate = (value: string, language: string) => {
    setLocalizedString({ ...localizedString, [language]: value });
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
              <TextareaAutosize style={{ width: '100%' }} minRows={5} value={localizedText} onChange={(e) => handleUpdate(e.target.value, language)} />}
          </Box>
        );
      })}
    </>
  );
}

export { LocalizedStringEditor };
