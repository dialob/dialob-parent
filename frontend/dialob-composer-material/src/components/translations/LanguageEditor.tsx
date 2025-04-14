import React from 'react';
import {
  Box, Button, Table, TableContainer, TableHead, TableRow, TableCell, styled,
  Tooltip, TableBody, IconButton, Switch, Popover, List, ListItemButton, Divider, TextField
} from '@mui/material';
import { FormattedMessage } from 'react-intl';
import { Add, ContentCopy, Delete } from '@mui/icons-material';
import { useComposer } from '../../dialob';
import { useEditor } from '../../editor';
import { ISO_LANGUAGES, MOST_USED_LANGUAGES } from '../../defaults';
import LanguageDeleteConfirmation from './LanguageDeleteConfirmation';
import { getLanguageName } from '../../utils/TranslationUtils';

export const LanguagesTable = styled(Table)(({ theme }) => ({
  '& .MuiTableCell-root': {
    border: `1px solid ${theme.palette.divider}`,
    padding: theme.spacing(1),
  },
}));

const LanguageEditor: React.FC = () => {
  const { form, addLanguage } = useComposer();
  const { editor, setActiveFormLanguage } = useEditor();
  const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
  const [copyFrom, setCopyFrom] = React.useState<string | undefined>();
  const [deleteLanguage, setDeleteLanguage] = React.useState<string | undefined>();
  const [search, setSearch] = React.useState<string>('');
  const currentLanguages = form.metadata.languages || [];
  const allLanguages = [...Object.entries({ ...MOST_USED_LANGUAGES, ...ISO_LANGUAGES })];
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const newLanguages = allLanguages.filter(([code, _language]) => !currentLanguages.includes(code));

  const handleAddLanguage = (code: string) => {
    setAnchorEl(null);
    addLanguage(code, copyFrom);
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
              <ListItemButton key={code} onClick={() => handleAddLanguage(code)}>
                {language.name}
              </ListItemButton>
            ))}
        </List>
      </Popover>
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
              </TableRow>
            ))}
          </TableBody>
        </LanguagesTable>
      </TableContainer>
    </>
  );
};

export { LanguageEditor };
