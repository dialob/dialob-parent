import React from 'react';
import { AppBar, Box, Divider, Stack, Typography, useTheme, Button, Menu, MenuItem, styled, TextField, Popover, List } from '@mui/material';
import { ArrowDropDown, Close, Download, Search, Support, Visibility } from '@mui/icons-material';
import { FormattedMessage, useIntl } from 'react-intl';
import { isContextVariable, useComposer } from '../../dialob';
import { getStatusIcon } from '../../utils/ErrorUtils';
import { useEditor } from '../../editor';
import { SCROLLBAR_WIDTH, SCROLL_SX } from '../../theme/siteTheme';
import GlobalListsDialog from '../../dialogs/GlobalListsDialog';
import TranslationDialog from '../../dialogs/TranslationDialog';
import FormOptionsDialog from '../../dialogs/FormOptionsDialog';
import VariablesDialog from '../../dialogs/VariablesDialog';
import { matchItemByKeyword, matchVariableByKeyword } from '../../utils/SearchUtils';
import { scrollToItem } from '../../utils/ScrollUtils';

interface SearchMatch {
  type: 'item' | 'variable';
  id: string;
}

const ResponsiveButton = styled(Button)(({ theme }) => ({
  [theme.breakpoints.down('lg')]: {
    minWidth: 'unset',
    paddingLeft: theme.spacing(0.5),
    paddingRight: theme.spacing(0.5),
  },
  [theme.breakpoints.up('lg')]: {
    paddingLeft: theme.spacing(1),
    paddingRight: theme.spacing(1),
  },
}));

const HeaderButton: React.FC<{
  label: string,
  startIcon?: React.ReactElement,
  endIcon?: React.ReactElement,
  onClick?: (event: React.MouseEvent<HTMLButtonElement>) => void
}> = ({ label, startIcon, endIcon, onClick }) => {
  const intl = useIntl();
  const stringExists = !!intl.messages[label];
  return (
    <ResponsiveButton variant='text' color='inherit' startIcon={startIcon} endIcon={endIcon} onClick={onClick}>
      {stringExists ? <FormattedMessage id={label} /> : label}
    </ResponsiveButton>
  );
};

const HeaderIconButton: React.FC<{ icon: React.ReactElement, disabled?: boolean }> = ({ icon, disabled }) => {
  return (
    <ResponsiveButton variant='text' color='inherit' disabled={disabled}>
      {icon}
    </ResponsiveButton>
  );
};

const MenuBar: React.FC = () => {
  const theme = useTheme();
  const intl = useIntl();
  const { form } = useComposer();
  const { editor, setActiveFormLanguage, setActivePage, setHighlightedItem, setActiveVariableTab } = useEditor();
  const headerPaddingSx = { px: theme.spacing(1) };
  const formLanguages = form.metadata.languages || ['en'];
  const [listsDialogOpen, setListsDialogOpen] = React.useState(false);
  const [translationsDialogOpen, setTranslationsDialogOpen] = React.useState(false);
  const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
  const [optionsDialogOpen, setOptionsDialogOpen] = React.useState(false);
  const [variablesDialogOpen, setVariablesDialogOpen] = React.useState(false);
  const [searchAnchor, setSearchAnchor] = React.useState<null | HTMLElement>(null);
  const [searchKeyword, setSearchKeyword] = React.useState('');
  const [searchMatches, setSearchMatches] = React.useState<SearchMatch[]>([]);
  const languageMenuOpen = Boolean(anchorEl);

  const handleLanguageMenuOpen = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  }

  const handleLanguageSelect = (languageCode: string) => {
    setActiveFormLanguage(languageCode);
    setAnchorEl(null);
  }

  const handleMatchClick = (match: SearchMatch) => {
    setSearchAnchor(null);
    if (match.type === 'item') {
      setHighlightedItem(form.data[match.id]);
      scrollToItem(match.id, Object.values(form.data), editor.activePage, setActivePage);
    } else {
      const variable = form.variables?.find(v => v.name === match.id);
      if (variable) {
        setActiveVariableTab(isContextVariable(variable) ? 'context' : 'expression');
      }
    }
  }

  React.useEffect(() => {
    if (searchKeyword.length === 0) {
      setSearchMatches([]);
      return;
    }
    const id = setTimeout(() => {
      const matches: SearchMatch[] = [];
      for (const item of Object.values(form.data)) {
        if (matchItemByKeyword(item, form.metadata.languages, searchKeyword)) {
          matches.push({ type: 'item', id: item.id });
        }
      }
      if (form.variables) {
        for (const variable of form.variables) {
          if (matchVariableByKeyword(variable, searchKeyword)) {
            matches.push({ type: 'variable', id: variable.name });
          }
        }
      }
      setSearchMatches(matches);
    }, 500);
    return () => clearTimeout(id);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [searchKeyword]);

  return (
    <>
      <GlobalListsDialog open={listsDialogOpen} onClose={() => setListsDialogOpen(false)} />
      <TranslationDialog open={translationsDialogOpen} onClose={() => setTranslationsDialogOpen(false)} />
      <FormOptionsDialog open={optionsDialogOpen} onClose={() => setOptionsDialogOpen(false)} />
      <VariablesDialog open={variablesDialogOpen} onClose={() => setVariablesDialogOpen(false)} />
      <AppBar position="fixed" color='inherit' sx={{ zIndex: theme.zIndex.drawer + 1, marginRight: -SCROLLBAR_WIDTH }}>
        <Stack direction='row' divider={<Divider orientation='vertical' flexItem />}>
          <Box sx={{ display: 'flex', alignItems: 'center', ...headerPaddingSx }}>
            {/* eslint-disable-next-line formatjs/no-literal-string-in-jsx */}
            <Typography sx={{ fontWeight: 'bold' }}>Dialob Composer</Typography>
            <Typography sx={{ ml: 1 }}>
              {form.metadata.label}
            </Typography>
          </Box>
          <HeaderButton label='translations' onClick={() => setTranslationsDialogOpen(true)} />
          <HeaderButton label='variables' onClick={() => setVariablesDialogOpen(true)} />
          <HeaderButton label='lists' onClick={() => setListsDialogOpen(true)} />
          <HeaderButton label='options' onClick={() => setOptionsDialogOpen(true)} />
          <HeaderButton label={intl.formatMessage({ id: 'version' }) + ": " + intl.formatMessage({ id: 'version.latest' })} endIcon={<ArrowDropDown />} />
          <HeaderIconButton icon={<Support fontSize='small' />} />
          <Box flexGrow={1} />
          <Box sx={{ display: 'flex', alignItems: 'center', ...headerPaddingSx }} onClick={(e) => setSearchAnchor(e.currentTarget)}>
            <TextField
              variant='standard'
              placeholder={intl.formatMessage({ id: 'search' })}
              InputProps={{ endAdornment: <Search />, disableUnderline: true }}
              value={searchKeyword}
              onChange={(e) => setSearchKeyword(e.target.value)} />
          </Box>
          <Popover open={Boolean(searchAnchor)} anchorEl={searchAnchor} onClose={() => setSearchAnchor(null)} anchorOrigin={{
            vertical: 'bottom',
            horizontal: 'left',
          }} disableAutoFocus disableScrollLock>
            <List sx={{ maxHeight: '50vh', ...SCROLL_SX }}>
              {searchMatches.length === 0 && <MenuItem>
                <Typography color='text.hint'><FormattedMessage id='search.keyword' /></Typography>
              </MenuItem>}
              {searchMatches
                .sort((a, b) => a.type.localeCompare(b.type))
                .map((match) => (
                  <MenuItem key={match.id} sx={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-start' }} onClick={() => handleMatchClick(match)}>
                    <Typography fontWeight='bold' color={match.type === 'variable' ? 'primary' : 'inherit'}>{match.id}</Typography>
                    <Typography color='text.hint'>{match.type}</Typography>
                  </MenuItem>
                ))}
            </List>
          </Popover>
          <HeaderIconButton icon={<Download />} />
          <HeaderIconButton disabled icon={getStatusIcon(editor.errors)} />
          <HeaderButton label={'locales.' + editor.activeFormLanguage} endIcon={<ArrowDropDown />} onClick={handleLanguageMenuOpen} />
          <Menu open={languageMenuOpen} anchorEl={anchorEl} onClose={() => setAnchorEl(null)} disableScrollLock={true}>
            {formLanguages
              .filter((language) => language !== editor.activeFormLanguage)
              .map((language) => (
                <MenuItem key={language} onClick={() => handleLanguageSelect(language)}>
                  <FormattedMessage id={`locales.${language}`} />
                </MenuItem>
              ))}
          </Menu>
          <HeaderIconButton icon={<Visibility fontSize='small' />} />
          <HeaderIconButton icon={<Close />} />
        </Stack>
      </AppBar>
    </>
  );
};

export default MenuBar;
