import React from 'react';
import { Dialog, DialogContent, DialogTitle } from '@mui/material';
import { FormattedMessage, useIntl } from 'react-intl';
import { useComposer } from '../../dialob';
import { DialogActionButtons } from '../../dialogs/DialogComponents';

const LanguageDeleteConfirmation: React.FC<{ language?: string, onClose: () => void }> = ({ language, onClose }) => {
  const { deleteLanguage } = useComposer();
  const intl = useIntl();
  const lang = language ? intl.formatMessage({ id: `locales.${language}` }) : '';

  const handleClick = () => {
    if (language) {
      deleteLanguage(language);
    }
    onClose();
  }

  if (!language) {
    return null;
  }

  return (
    <Dialog open={true} onClose={onClose}>
      <DialogTitle>
        <FormattedMessage id='dialogs.translations.languages.delete.confirm.title' />
      </DialogTitle>
      <DialogContent>
        <FormattedMessage id='dialogs.translations.languages.delete.confirm.desc' values={{ lang }} />
      </DialogContent>
      <DialogActionButtons handleClose={onClose} handleClick={handleClick} />
    </Dialog>
  );
};

export default LanguageDeleteConfirmation;
