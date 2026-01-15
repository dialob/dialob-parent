import React from 'react';
import { Alert, Box, Button, Checkbox, Dialog, DialogActions, DialogContent, DialogTitle, Typography } from '@mui/material';
import { FormattedMessage } from 'react-intl';
import { getLanguageName } from '../../utils/TranslationUtils';

interface AddLanguageConfirmDialogProps {
  open: boolean;
  language: string | undefined;
  sourceLanguage: string;
  shouldTranslate: boolean;
  onShouldTranslateChange: (value: boolean) => void;
  onConfirm: () => void;
  onCancel: () => void;
  showTranslateOption: boolean;
}

const AddLanguageConfirmDialog: React.FC<AddLanguageConfirmDialogProps> = ({
  open,
  language,
  sourceLanguage,
  shouldTranslate,
  onShouldTranslateChange,
  onConfirm,
  onCancel,
  showTranslateOption
}) => {
  return (
    <Dialog open={open} onClose={onCancel}>
      <DialogTitle>
        <FormattedMessage id='dialogs.translations.languages.add.confirm.title' />
      </DialogTitle>
      <DialogContent>
        <Typography sx={{ mb: 2 }}>
          <FormattedMessage 
            id='dialogs.translations.languages.add.confirm.message' 
            values={{ language: language ? getLanguageName(language) : '' }}
          />
        </Typography>
        {showTranslateOption && (
          <>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
              <Checkbox
                checked={shouldTranslate}
                onChange={(e) => onShouldTranslateChange(e.target.checked)}
              />
              <Typography>
                <FormattedMessage 
                  id='dialogs.translations.languages.add.translate.option' 
                  values={{ source: getLanguageName(sourceLanguage) }}
                />
              </Typography>
            </Box>
            {shouldTranslate && (
              <Alert severity="info" sx={{ mt: 2 }}>
                <FormattedMessage 
                  id='dialogs.translations.languages.add.translate.info' 
                />
              </Alert>
            )}
          </>
        )}
      </DialogContent>
      <DialogActions>
        <Button onClick={onCancel}>
          <FormattedMessage id='buttons.cancel' />
        </Button>
        <Button onClick={onConfirm} variant="contained">
          <FormattedMessage id='buttons.add' />
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default AddLanguageConfirmDialog;
