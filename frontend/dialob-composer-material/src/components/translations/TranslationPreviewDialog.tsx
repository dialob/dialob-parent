import React from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper
} from '@mui/material';
import { Close, Check } from '@mui/icons-material';
import { FormattedMessage } from 'react-intl';
import { TranslationResult } from '../../backend/types';
import { getLanguageName } from '../../utils/TranslationUtils';

interface TranslationPreviewDialogProps {
  open: boolean;
  translations: Map<string, { translations: TranslationResult[], sourceLanguage: string, sourceTexts: Map<string, string> }>;
  onCancel: () => void;
  onSave: () => void;
}

interface GroupedTranslation {
  entryId: string;
  sourceText: string;
  translationsByLanguage: Map<string, string>;
}

const TranslationPreviewDialog: React.FC<TranslationPreviewDialogProps> = ({
  open,
  translations,
  onCancel,
  onSave
}) => {
  // Group translations by entry ID
  const groupedTranslations = React.useMemo(() => {
    const grouped = new Map<string, GroupedTranslation>();
    const firstEntry = translations.values().next().value;
    const sourceTexts = firstEntry?.sourceTexts || new Map<string, string>();
    
    translations.forEach(({ translations: results }, targetLanguage) => {
      results.forEach(result => {
        if (!grouped.has(result.id)) {
          grouped.set(result.id, {
            entryId: result.id,
            sourceText: sourceTexts.get(result.id) || '',
            translationsByLanguage: new Map()
          });
        }
        grouped.get(result.id)!.translationsByLanguage.set(targetLanguage, result.translatedText);
      });
    });
    
    return Array.from(grouped.values());
  }, [translations]);

  const targetLanguages = React.useMemo(() => {
    return Array.from(translations.keys());
  }, [translations]);

  const sourceLanguage = React.useMemo(() => {
    const first = translations.values().next().value;
    return first?.sourceLanguage || '';
  }, [translations]);

  const totalTranslations = Array.from(translations.values()).reduce(
    (sum, { translations }) => sum + translations.length,
    0
  );

  return (
    <Dialog open={open} onClose={onCancel} maxWidth="xl" fullWidth>
      <DialogTitle sx={{ fontWeight: 'bold' }}>
        <FormattedMessage id='dialogs.translations.preview.title' />
        <Typography variant='body2' color='text.secondary' sx={{ mt: 1 }}>
          <FormattedMessage 
            id='dialogs.translations.preview.subtitle' 
            values={{ 
              count: totalTranslations,
              language: getLanguageName(sourceLanguage)
            }}
          />
        </Typography>
      </DialogTitle>
      <DialogContent dividers sx={{ p: 2 }}>
        <TableContainer component={Paper} elevation={0} sx={{ border: 1, borderColor: 'divider' }}>
          <Table size='small'>
            <TableHead>
              <TableRow>
                <TableCell sx={{ fontWeight: 'bold', minWidth: 150, p: 1 }}>
                  <FormattedMessage id='dialogs.translations.preview.entryId' />
                </TableCell>
                <TableCell sx={{ fontWeight: 'bold', minWidth: 200, p: 1 }}>
                  <FormattedMessage 
                    id='dialogs.translations.preview.source' 
                    values={{ language: getLanguageName(sourceLanguage) }}
                  />
                </TableCell>
                {targetLanguages.map(lang => (
                  <TableCell key={lang} sx={{ fontWeight: 'bold', minWidth: 200, p: 1 }}>
                    {getLanguageName(lang)}
                  </TableCell>
                ))}
              </TableRow>
            </TableHead>
            <TableBody>
              {groupedTranslations.map((grouped, idx) => (
                <TableRow key={idx} hover>
                  <TableCell sx={{ color: 'text.secondary', p: 1 }}>
                    {grouped.entryId}
                  </TableCell>
                  <TableCell sx={{ wordBreak: 'break-word', p: 1 }}>
                    {grouped.sourceText}
                  </TableCell>
                  {targetLanguages.map(lang => (
                    <TableCell key={lang} sx={{ wordBreak: 'break-word', fontWeight: 500, p: 1 }}>
                      {grouped.translationsByLanguage.get(lang) || '-'}
                    </TableCell>
                  ))}
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </DialogContent>
      <DialogActions>
        <Button onClick={onCancel} endIcon={<Close />} color='inherit'>
          <FormattedMessage id='buttons.cancel' />
        </Button>
        <Button onClick={onSave} endIcon={<Check />} variant='contained' color='primary'>
          <FormattedMessage id='buttons.save' />
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default TranslationPreviewDialog;
