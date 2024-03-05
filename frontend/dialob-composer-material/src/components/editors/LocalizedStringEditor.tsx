import React from 'react';
import Markdown from 'react-markdown';
import { Button, Typography, Box, TextareaAutosize, Menu, MenuItem } from '@mui/material';
import { Add, Visibility } from '@mui/icons-material';
import { FormattedMessage } from 'react-intl';
import { LocalizedString, useComposer } from '../../dialob';
import { useEditor } from '../../editor';
import { markdownComponents } from '../../defaults/markdown';
import { IndexedRule } from './ValidationRuleEditor';


const LocalizedStringEditor: React.FC<{
  type: 'label' | 'description' | 'validations',
  rule?: IndexedRule,
  setRule?: React.Dispatch<React.SetStateAction<IndexedRule | undefined>>
}> = ({ type, rule, setRule }) => {
  const { form, updateLocalizedString } = useComposer();
  const { editor, setActiveItem } = useEditor();
  const item = editor.activeItem;
  const [preview, setPreview] = React.useState(false);
  const [localizedString, setLocalizedString] = React.useState<LocalizedString | undefined>();
  const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);

  React.useEffect(() => {
    if (item) {
      setLocalizedString(type === 'validations' ? rule?.validationRule.message : item[type]);
    }
  }, [item, rule]);

  React.useEffect(() => {
    if (item && localizedString) {
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
      }, 1000);
      return () => clearTimeout(id);
    }
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

  return (
    <>
      <Box display='flex'>
        <Box flexGrow={1} />
        <Button variant={preview ? 'contained' : 'outlined'} endIcon={<Visibility />} onClick={() => setPreview(!preview)}><FormattedMessage id='preview' /></Button>
        <Button variant='outlined' onClick={(e) => setAnchorEl(e.currentTarget)}
          disabled={form.metadata.languages?.length === (localizedString ? Object.keys(localizedString).length : 0)}
          sx={{ textTransform: 'none', ml: 1 }} endIcon={<Add />}>
          <FormattedMessage id={`dialogs.options.${type}.add`} />
        </Button>
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
              }}><FormattedMessage id={`locales.${language}`} /></MenuItem>
            ))}
        </Menu>
      </Box>
      {localizedString && Object.keys(localizedString).map((language) => {
        const localizedText = localizedString[language];
        return (
          <Box key={language}>
            <Typography color='text.hint'><FormattedMessage id={`locales.${language}`} /></Typography>
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

export default LocalizedStringEditor;
