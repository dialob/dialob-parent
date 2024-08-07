import React from 'react';
import Markdown from 'react-markdown';
import { Button, Typography, Box, TextareaAutosize, Menu, MenuItem, IconButton } from '@mui/material';
import { Add, Delete, Translate, Visibility } from '@mui/icons-material';
import { FormattedMessage } from 'react-intl';
import { LocalizedString, useComposer } from '../../dialob';
import { useEditor } from '../../editor';
import { markdownComponents } from '../../defaults/markdown';
import { IndexedRule } from './ValidationRuleEditor';
import { getLanguageName } from '../../utils/TranslationUtils';


const LocalizedStringEditor: React.FC<{
  type: 'label' | 'description' | 'validations',
  rule?: IndexedRule,
  setRule?: React.Dispatch<React.SetStateAction<IndexedRule | undefined>>
}> = ({ type, rule, setRule }) => {
  const { form, updateLocalizedString } = useComposer();
  const { editor, setActiveItem } = useEditor();
  const activeLanguage = editor.activeFormLanguage;
  const item = editor.activeItem;
  const formLanguages = form.metadata.languages;
  const [preview, setPreview] = React.useState(false);
  const [localizedString, setLocalizedString] = React.useState<LocalizedString | undefined>();
  const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);

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
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [localizedString]);

  if (!item || (type === 'validations' && rule === undefined)) {
    return null;
  }

  const handleAdd = (language: string) => {
    setLocalizedString({ ...localizedString, [language]: '' });
  }

  const handleUpdate = (value: string, language: string) => {
    setLocalizedString({ ...localizedString, [language]: value });
  }

  const handleDelete = (language: string) => {
    const newLocalizedString = { ...localizedString };
    delete newLocalizedString[language];
    setLocalizedString(newLocalizedString);
  }

  return (
    <>
      <Box display='flex'>
        <Box flexGrow={1} />
        <Button variant={preview ? 'contained' : 'outlined'} endIcon={<Visibility />} onClick={() => setPreview(!preview)}>
          <FormattedMessage id='dialogs.options.preview' />
        </Button>
        {(localizedString === undefined || localizedString[activeLanguage] === undefined) && <Button variant='outlined'
          onClick={() => handleAdd(activeLanguage)} sx={{ textTransform: 'none', ml: 1 }} endIcon={<Translate />}
          disabled={form.metadata.languages?.length === (localizedString ? Object.keys(localizedString).length : 0)}>
          <FormattedMessage id={`dialogs.options.translation.${activeLanguage}.add`} />
        </Button>}
        {formLanguages && formLanguages.length > 1 && <Button variant='outlined' onClick={(e) => setAnchorEl(e.currentTarget)}
          disabled={form.metadata.languages?.length === (localizedString ? Object.keys(localizedString).length : 0)}
          sx={{ textTransform: 'none', ml: 1 }} endIcon={<Add />}>
          <FormattedMessage id={`dialogs.options.${type}.add`} />
        </Button>}
        <Menu
          anchorEl={anchorEl}
          open={Boolean(anchorEl)}
          onClose={() => setAnchorEl(null)}
        >
          {form.metadata.languages?.filter((language) => !localizedString?.[language])
            .map((language) => (
              <MenuItem key={language} onClick={() => {
                handleAdd(language);
                setAnchorEl(null);
              }}>{getLanguageName(language)}</MenuItem>
            ))}
        </Menu>
      </Box>
      {localizedString && Object.keys(localizedString).map((language) => {
        const localizedText = localizedString[language];
        return (
          <Box key={language}>
            <Box display='flex' alignItems='center'>
              <Typography color='text.hint'>{getLanguageName(language)}</Typography>
              <IconButton size='small' onClick={() => handleDelete(language)} color='error'><Delete /></IconButton>
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
