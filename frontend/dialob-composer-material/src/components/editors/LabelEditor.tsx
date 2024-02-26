import React from 'react';
import Markdown from 'react-markdown';
import { Button, Typography, Box, TextareaAutosize, Menu, MenuItem } from '@mui/material';
import { Add, Visibility } from '@mui/icons-material';
import { FormattedMessage } from 'react-intl';
import { LocalizedString, useComposer } from '../../dialob';
import { useEditor } from '../../editor';
import { markdownComponents } from '../../defaults/markdown';


const LabelEditor: React.FC = () => {
  const { form, updateItem } = useComposer();
  const { editor } = useEditor();
  const item = editor.activeItem;
  const [preview, setPreview] = React.useState(false);
  const [labels, setLabels] = React.useState<LocalizedString | undefined>(item && item.label);
  const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);

  if (!item) {
    return null;
  }

  const handleAdd = (language: string) => {
    setLabels({ ...labels, [language]: '' });
    updateItem(item.id, 'label', '', language);
  }

  const handleUpdate = (value: string, language: string) => {
    setLabels({ ...labels, [language]: value });
    updateItem(item.id, 'label', value, language);
  }

  return (
    <>
      <Box display='flex'>
        <Box flexGrow={1} />
        <Button variant={preview ? 'contained' : 'outlined'} endIcon={<Visibility />} onClick={() => setPreview(!preview)}><FormattedMessage id='preview' /></Button>
        <Button variant='outlined' onClick={(e) => setAnchorEl(e.currentTarget)}
          disabled={form.metadata.languages?.length === (labels ? Object.keys(labels).length : 0)}
          sx={{ textTransform: 'none', ml: 1 }} endIcon={<Add />}>
          <FormattedMessage id='dialogs.options.labels.add' />
        </Button>
        <Menu
          anchorEl={anchorEl}
          open={Boolean(anchorEl)}
          onClose={() => setAnchorEl(null)}
        >
          {form.metadata.languages?.filter((language) => !labels?.[language])
            .map((language) => (
              <MenuItem onClick={() => {
                handleAdd(language);
                setAnchorEl(null);
              }}><FormattedMessage id={`locales.${language}`} /></MenuItem>
            ))}
        </Menu>
      </Box>
      {labels && Object.keys(labels).map((language) => {
        const localizedText = labels[language];
        return (
          <>
            <Typography color='text.hint'><FormattedMessage id={`locales.${language}`} /></Typography>
            {preview ?
              <Box sx={{ border: 1, borderRadius: 0.5, borderColor: 'text.secondary' }}>
                <Markdown skipHtml components={markdownComponents}>{localizedText}</Markdown>
              </Box > :
              <TextareaAutosize style={{ width: '100%' }} minRows={5} value={localizedText} onChange={(e) => handleUpdate(e.target.value, language)} />}
          </>
        );
      })}
    </>
  );
}

export default LabelEditor;
