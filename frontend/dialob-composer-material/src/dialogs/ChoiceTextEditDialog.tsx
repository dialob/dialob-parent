import React from 'react';
import Markdown from 'react-markdown';
import { Dialog, DialogTitle, DialogContent, Button, Typography, Box, TextareaAutosize } from '@mui/material';
import { Visibility } from '@mui/icons-material';
import { useEditor } from '../editor';
import { FormattedMessage } from 'react-intl';
import { ValueSetEntry } from '../dialob';
import { DialogActionButtons, DialogHelpButton, DialogLanguageMenu } from './DialogComponents';
import { markdownComponents } from '../defaults/markdown';

const ChoiceTextEditDialog: React.FC<{
  open: boolean,
  valueSetEntry?: ValueSetEntry,
  onUpdate: (entry: ValueSetEntry, text: string, language: string) => void,
  onClose: () => void
}> = ({ open, valueSetEntry, onUpdate, onClose }) => {
  const { editor } = useEditor();
  const [activeLanguage, setActiveLanguage] = React.useState(editor.activeFormLanguage);
  const [preview, setPreview] = React.useState(false);
  const [localizedText, setLocalizedText] = React.useState<string>();

  const handleClick = () => {
    if (valueSetEntry && localizedText && localizedText.length > 0) {
      onUpdate(valueSetEntry, localizedText, activeLanguage);
      onClose();
    }
  }

  React.useEffect(() => {
    if (valueSetEntry && valueSetEntry.label) {
      const localizedText = valueSetEntry.label[activeLanguage];
      setLocalizedText(localizedText || '');
    } else {
      setLocalizedText('');
    }
  }, [activeLanguage, valueSetEntry]);

  if (!valueSetEntry) {
    return null;
  }

  return (
    <Dialog open={open} maxWidth='md' fullWidth>
      <DialogTitle sx={{ display: 'flex', flexDirection: 'row', alignItems: 'center' }}>
        <Typography><FormattedMessage id='dialogs.text.label.title' values={{ itemId: valueSetEntry.id }} /></Typography>
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
      <DialogActionButtons handleClose={onClose} handleClick={handleClick} />
    </Dialog>
  );
};

export default ChoiceTextEditDialog;
