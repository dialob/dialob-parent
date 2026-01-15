import React from 'react';
import {
  Box, Button, Divider, IconButton, List, ListItemButton, Popover, Switch, Table,
  TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Tooltip, styled
} from '@mui/material';
import { FormattedMessage } from 'react-intl';
import { Add, ContentCopy, Delete } from '@mui/icons-material';
import { useComposer } from '../../dialob';
import { useEditor } from '../../editor';
import { ISO_LANGUAGES, MOST_USED_LANGUAGES } from '../../defaults';
import LanguageDeleteConfirmation from './LanguageDeleteConfirmation';
import AddLanguageConfirmDialog from './AddLanguageConfirmDialog';
import TranslationProgressDialog from './TranslationProgressDialog';
import { getLanguageName } from '../../utils/TranslationUtils';
import { useBackend } from '../../backend/useBackend';
import { TranslationEntry, TranslationResponse } from '../../backend/types';

export const LanguagesTable = styled(Table)(({ theme }) => ({
  '& .MuiTableCell-root': {
    border: `1px solid ${theme.palette.divider}`,
    padding: theme.spacing(1),
  },
}));

const LanguageEditor: React.FC = () => {
  const { form, addLanguage, setMetadataValue, applyTranslations } = useComposer();
  const { editor, setActiveFormLanguage } = useEditor();
  const { translateEntries, config } = useBackend();
  const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
  const [copyFrom, setCopyFrom] = React.useState<string | undefined>();
  const [deleteLanguage, setDeleteLanguage] = React.useState<string | undefined>();
  const [search, setSearch] = React.useState<string>('');
  const [confirmDialogOpen, setConfirmDialogOpen] = React.useState(false);
  const [pendingLanguage, setPendingLanguage] = React.useState<string | undefined>();
  const [shouldTranslate, setShouldTranslate] = React.useState(true);
  const [isTranslating, setIsTranslating] = React.useState(false);
  const currentLanguages = form.metadata.languages || [];
  const allLanguages = [...Object.entries({ ...MOST_USED_LANGUAGES, ...ISO_LANGUAGES })];
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const newLanguages = allLanguages.filter(([code, _language]) => !currentLanguages.includes(code));

  const handleLanguageSelection = (code: string) => {
    setAnchorEl(null);
    setPendingLanguage(code);
    setConfirmDialogOpen(true);
  }

  const handleConfirmAddLanguage = async () => {
    if (!pendingLanguage) return;

    setConfirmDialogOpen(false);

    if (!shouldTranslate) {
      addLanguage(pendingLanguage, copyFrom);
      setPendingLanguage(undefined);
      setCopyFrom(undefined);
      return;
    }

    addLanguage(pendingLanguage, undefined);
    setIsTranslating(true);

    try {
      const sourceLanguage = copyFrom || editor.activeFormLanguage;
      const targetLanguage = pendingLanguage;
      const entriesToTranslate: TranslationEntry[] = [];

      // Collect all translatable content from items
      Object.values(form.data).forEach(item => {
        if (item.id === 'questionnaire') return;

        // Item labels
        if (item.label?.[sourceLanguage]) {
          entriesToTranslate.push({
            id: `i:${item.id}:l`,
            text: item.label[sourceLanguage]
          });
        }

        // Item descriptions
        if (item.description?.[sourceLanguage]) {
          entriesToTranslate.push({
            id: `i:${item.id}:d`,
            text: item.description[sourceLanguage]
          });
        }

        // Validation messages
        item.validations?.forEach((validation, idx) => {
          if (validation.message?.[sourceLanguage]) {
            entriesToTranslate.push({
              id: `i:${item.id}:v:${idx}`,
              text: validation.message[sourceLanguage]
            });
          }
        });
      });

      // Collect valueset entries
      form.valueSets?.forEach(valueSet => {
        valueSet.entries?.forEach((entry, idx) => {
          if (entry.label?.[sourceLanguage]) {
            entriesToTranslate.push({
              id: `v:${valueSet.id}:${idx}:${entry.id}`,
              text: entry.label[sourceLanguage]
            });
          }
        });
      });

      if (entriesToTranslate.length > 0) {
        const response = await translateEntries({
          sourceLanguage,
          targetLanguage,
          entries: entriesToTranslate
        });

        if (response.success && response.result) {
          const translationResponse = response.result as TranslationResponse;
          const { translations } = translationResponse;
          applyTranslations(translations, sourceLanguage, targetLanguage);
        } else if (response.apiError) {
          console.error('Translation failed:', response.apiError.message);
        }
      }
    } catch (error) {
      console.error('Translation error:', error);
    } finally {
      setIsTranslating(false);
      setPendingLanguage(undefined);
      setCopyFrom(undefined);
    }
  }

  const handleCancelAddLanguage = () => {
    setConfirmDialogOpen(false);
    setPendingLanguage(undefined);
    setShouldTranslate(true);
  }

  const handleCopyLanguage = (e: React.MouseEvent<HTMLButtonElement, MouseEvent>, code: string) => {
    setAnchorEl(e.currentTarget);
    setCopyFrom(code);
  }

  return (
    <>
      <LanguageDeleteConfirmation language={deleteLanguage} onClose={() => setDeleteLanguage(undefined)} />
      
      <Popover open={Boolean(anchorEl)} anchorEl={anchorEl} onClose={() => setAnchorEl(null)}
        sx={{ maxHeight: '70vh' }}
        anchorOrigin={{
          horizontal: 'left',
          vertical: 'bottom',
        }}
      >
        <List>
          <TextField id='search' label={<FormattedMessage id='dialogs.translations.languages.search' />}
            value={search} onChange={(e) => setSearch(e.target.value)} sx={{ m: 1, mt: 0 }} />
          <Divider />
          {newLanguages
            .filter(([code, language]) => !currentLanguages.includes(code) &&
              search === '' || language.name.toLowerCase().includes(search.toLowerCase()))
            .map(([code, language]) => (
              <ListItemButton key={code} onClick={() => handleLanguageSelection(code)}>
                {language.name}
              </ListItemButton>
            ))}
        </List>
      </Popover>

      <AddLanguageConfirmDialog
        open={confirmDialogOpen}
        language={pendingLanguage}
        sourceLanguage={copyFrom || editor.activeFormLanguage}
        shouldTranslate={shouldTranslate}
        onShouldTranslateChange={setShouldTranslate}
        onConfirm={handleConfirmAddLanguage}
        onCancel={handleCancelAddLanguage}
        showTranslateOption={!!config.translationServiceUrl}
      />

      <TranslationProgressDialog
        open={isTranslating}
        current={1}
        total={1}
        title={pendingLanguage ? `Translating to ${getLanguageName(pendingLanguage)}` : undefined}
      />
      <Box sx={{ display: 'flex', justifyContent: 'flex-end' }}>
        <Tooltip title={<FormattedMessage id='dialogs.translations.languages.add.desc' />}>
          <Button id='empty' endIcon={<Add />} onClick={(e) => setAnchorEl(e.currentTarget)}>
            <FormattedMessage id='dialogs.translations.languages.add' />
          </Button>
        </Tooltip>
      </Box>
      <TableContainer sx={{ mt: 2 }}>
        <LanguagesTable>
          <TableHead>
            <TableRow>
              <TableCell width='70%' sx={{ fontWeight: 'bold' }}><FormattedMessage id='dialogs.translations.languages.language' /></TableCell>
              <TableCell align='center' sx={{ fontWeight: 'bold' }}><FormattedMessage id='dialogs.translations.languages.copy' /></TableCell>
              <TableCell align='center' sx={{ fontWeight: 'bold' }}><FormattedMessage id='dialogs.translations.languages.delete' /></TableCell>
              <TableCell align='center' sx={{ fontWeight: 'bold' }}><FormattedMessage id='dialogs.translations.languages.active' /></TableCell>
              <Tooltip title={<FormattedMessage id='dialogs.translations.languages.default.desc' />}>
                <TableCell align='center' sx={{ fontWeight: 'bold' }}><FormattedMessage id='dialogs.translations.languages.default' /></TableCell>
              </Tooltip>
            </TableRow>
          </TableHead>
          <TableBody>
            {currentLanguages.map(lang => (
              <TableRow key={lang}>
                <TableCell width='70%'>{getLanguageName(lang)}</TableCell>
                <TableCell align='center'>
                  <IconButton id='copy' onClick={(e) => handleCopyLanguage(e, lang)}><ContentCopy /></IconButton>
                </TableCell>
                <TableCell align='center'>
                  <IconButton onClick={() => setDeleteLanguage(lang)} disabled={lang === editor.activeFormLanguage}>
                    <Delete color={lang === editor.activeFormLanguage ? 'inherit' : 'error'} />
                  </IconButton>
                </TableCell>
                <TableCell align='center'>
                  <Switch checked={editor.activeFormLanguage === lang} onChange={() => setActiveFormLanguage(lang)} />
                </TableCell>
                <TableCell align='center'>
                  <Switch checked={form.metadata.defaultActiveLanguage === lang} onChange={() => setMetadataValue('defaultActiveLanguage', lang)} />
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </LanguagesTable>
      </TableContainer>
    </>
  );
};

export { LanguageEditor };
