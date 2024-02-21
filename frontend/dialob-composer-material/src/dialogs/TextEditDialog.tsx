import React from 'react';
import Markdown from 'react-markdown';
import { Dialog, DialogTitle, DialogContent, Button, Typography, Box, TextareaAutosize } from '@mui/material';
import { Visibility } from '@mui/icons-material';
import { useEditor } from '../editor';
import { FormattedMessage } from 'react-intl';
import { useComposer } from '../dialob';
import { DialogActionButtons, DialogHelpButton, DialogLanguageMenu } from './DialogComponents';
import { markdownComponents } from '../defaults/markdown';

const TextEditDialog: React.FC = () => {
  const { updateItem } = useComposer();
  const { editor, setTextEditDialogType, setActiveItem } = useEditor();
  const item = editor.activeItem;
  const open = item && editor.textEditDialogType !== undefined || false;
  const [activeLanguage, setActiveLanguage] = React.useState(editor.activeFormLanguage);
  const [preview, setPreview] = React.useState(false);
  const [localizedText, setLocalizedText] = React.useState<string>();

  const handleClose = () => {
    setTextEditDialogType(undefined);
    setActiveItem(undefined);
  }

  const handleClick = () => {
    if (editor.textEditDialogType && item && localizedText && localizedText.length > 0) {
      updateItem(item.id, editor.textEditDialogType, localizedText, activeLanguage);
      handleClose();
    }
  }

  React.useEffect(() => {
    if (editor.textEditDialogType && item) {
      const localizedText = item[editor.textEditDialogType]?.[activeLanguage];
      setLocalizedText(localizedText || '');
    } else {
      setLocalizedText('');
    }
    setPreview(false);
  }, [activeLanguage, editor.textEditDialogType, item]);

  if (!item) {
    return null;
  }

  return (
    <Dialog open={open} maxWidth='md' fullWidth>
      <DialogTitle sx={{ display: 'flex', flexDirection: 'row', alignItems: 'center' }}>
        <Typography><FormattedMessage id={`dialogs.text.${editor.textEditDialogType}.title`} values={{ itemId: item.id }} /></Typography>
        <Box flexGrow={1} />
        <Box sx={{ display: 'flex', width: 0.35, justifyContent: 'space-between' }}>
          <DialogLanguageMenu activeLanguage={activeLanguage} setActiveLanguage={setActiveLanguage} />
          <Button color='inherit' variant='contained' endIcon={<Visibility />} onClick={() => setPreview(!preview)}><FormattedMessage id='preview' /></Button>
          <DialogHelpButton helpUrl='https://www.markdownguide.org/basic-syntax/' />
        </Box>
      </DialogTitle>
      <DialogContent>
        {preview ?
          <Box sx={{ border: 1, borderRadius: 0.5, borderColor: 'text.secondary' }}>
            <Markdown skipHtml components={markdownComponents}>{localizedText}</Markdown>
          </Box> :
          <TextareaAutosize style={{ width: '100%' }} minRows={10} value={localizedText} onChange={(e) => setLocalizedText(e.target.value)} />
        }
      </DialogContent>
      <DialogActionButtons handleClose={handleClose} handleClick={handleClick} />
    </Dialog>
  );
};

export default TextEditDialog;
