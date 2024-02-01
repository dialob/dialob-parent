import React from 'react';
import Markdown from 'react-markdown';
import { Dialog, DialogTitle, DialogContent, Button, Typography, Box, TextareaAutosize } from '@mui/material';
import { Visibility } from '@mui/icons-material';
import { useEditor } from '../editor';
import { FormattedMessage } from 'react-intl';
import { DialobItem, useComposer } from '../dialob';
import { DialogActionButtons, DialogHelpButton, DialogLanguageMenu } from './DialogComponents';

const TextEditDialog: React.FC = () => {
  const { form, updateItem } = useComposer();
  const item: DialobItem = Object.values(form.data)[2];
  const { editor, setTextEditDialogType } = useEditor();
  const open = editor.textEditDialogType !== undefined;
  const [activeLanguage, setActiveLanguage] = React.useState(editor.activeFormLanguage);
  const [preview, setPreview] = React.useState(false);
  const [localizedText, setLocalizedText] = React.useState<string | undefined>(undefined);

  const handleClose = () => {
    setTextEditDialogType(undefined);
  }

  const handleClick = () => {
    if (editor.textEditDialogType && localizedText) {
      updateItem(item.id, editor.textEditDialogType, localizedText, activeLanguage);
      handleClose();
    }
  }

  React.useEffect(() => {
    if (editor.textEditDialogType) {
      setLocalizedText(item[editor.textEditDialogType]?.[activeLanguage]);
    }
  }, [activeLanguage]);

  return (
    <Dialog open={open} maxWidth='md' fullWidth>
      <DialogTitle sx={{ display: 'flex', flexDirection: 'row', alignItems: 'center' }}>
        <Typography>{item.id}: {editor.textEditDialogType}</Typography>
        <Box flexGrow={1} />
        <Box sx={{ display: 'flex', width: 0.35, justifyContent: 'space-between' }}>
          <DialogLanguageMenu activeLanguage={activeLanguage} setActiveLanguage={setActiveLanguage} />
          <Button color='inherit' endIcon={<Visibility />} onClick={() => setPreview(!preview)}><FormattedMessage id='preview' /></Button>
          <DialogHelpButton helpUrl='https://www.markdownguide.org/basic-syntax/' />
        </Box>
      </DialogTitle>
      <DialogContent>
        {preview ?
          <Box sx={{ border: 1, borderRadius: 0.5, borderColor: 'text.secondary' }}><Markdown>{localizedText}</Markdown></Box> :
          <TextareaAutosize style={{ width: '100%' }} minRows={10} value={localizedText} onChange={(e) => setLocalizedText(e.target.value)} />
        }
      </DialogContent>
      <DialogActionButtons handleClose={handleClose} handleClick={handleClick} />
    </Dialog>
  );
};

export default TextEditDialog;
