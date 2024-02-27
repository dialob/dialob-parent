import React from 'react';
import Markdown from 'react-markdown';
import { Button, Typography, Box, TextareaAutosize, Menu, MenuItem } from '@mui/material';
import { Add, Visibility } from '@mui/icons-material';
import { FormattedMessage } from 'react-intl';
import { LocalizedString, useComposer } from '../../dialob';
import { useEditor } from '../../editor';
import { markdownComponents } from '../../defaults/markdown';


const LocalizedStringEditor: React.FC<{ type: 'label' | 'description' }> = ({ type }) => {
  const { form, updateLocalizedString } = useComposer();
  const { editor } = useEditor();
  const item = editor.activeItem;
  const [preview, setPreview] = React.useState(false);
  const [localizedString, setLocalizedString] = React.useState<LocalizedString | undefined>();
  const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
  const minRows = type === 'label' ? 5 : 10;

  React.useEffect(() => {
    setLocalizedString(type === 'label' ? item?.label : item?.description);
  }, [item]);

  if (!item) {
    return null;
  }

  const handleAdd = (language: string) => {
    setLocalizedString({ ...localizedString, [language]: '' });
  }

  const handleUpdate = (value: string, language: string) => {
    setLocalizedString({ ...localizedString, [language]: value });
  }

  React.useEffect(() => {
    if (localizedString) {
      const id = setTimeout(() => {
        updateLocalizedString(item.id, type, localizedString);
      }, 300);
      return () => clearTimeout(id);
    }
  }, [localizedString]);

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
              <MenuItem onClick={() => {
                handleAdd(language);
                setAnchorEl(null);
              }}><FormattedMessage id={`locales.${language}`} /></MenuItem>
            ))}
        </Menu>
      </Box>
      {localizedString && Object.keys(localizedString).map((language) => {
        const localizedText = localizedString[language];
        return (
          <>
            <Typography color='text.hint'><FormattedMessage id={`locales.${language}`} /></Typography>
            {preview ?
              <Box sx={{ border: 1, borderRadius: 0.5, borderColor: 'text.secondary' }}>
                <Markdown skipHtml components={markdownComponents}>{localizedText}</Markdown>
              </Box > :
              <TextareaAutosize style={{ width: '100%' }} minRows={minRows} value={localizedText} onChange={(e) => handleUpdate(e.target.value, language)} />}
          </>
        );
      })}
    </>
  );
}

export default LocalizedStringEditor;
