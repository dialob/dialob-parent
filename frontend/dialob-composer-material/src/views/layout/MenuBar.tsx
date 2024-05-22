import React from 'react';
import { AppBar, Box, Divider, Stack, Typography, useTheme, Button, Menu, MenuItem, styled, TextField, Popover, List, Tooltip } from '@mui/material';
import { ArrowDropDown, Close, Download, Search, Support, Visibility } from '@mui/icons-material';
import { FormattedMessage, useIntl } from 'react-intl';
import { useComposer } from '../../dialob';
import { getStatus, getStatusIcon } from '../../utils/ErrorUtils';
import { useEditor } from '../../editor';
import { SCROLLBAR_WIDTH, SCROLL_SX } from '../../theme/siteTheme';
import GlobalListsDialog from '../../dialogs/GlobalListsDialog';
import TranslationDialog from '../../dialogs/TranslationDialog';
import FormOptionsDialog from '../../dialogs/FormOptionsDialog';
import VariablesDialog from '../../dialogs/VariablesDialog';
import PreviewDialog from '../../dialogs/PreviewDialog';
import VersioningDialog from '../../dialogs/VersioningDialog';
import CreateTagDialog from '../../dialogs/CreateTagDialog';
import { downloadForm } from '../../utils/ParseUtils';
import { matchItemByKeyword, matchVariableByKeyword } from '../../utils/SearchUtils';
import { scrollToItem } from '../../utils/ScrollUtils';
import { useBackend } from '../../backend/useBackend';
import { CreateSessionResult } from '../../backend/types';
import { isContextVariable } from '../../utils/ItemUtils';

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

const MenuBar: React.FC = () => {
  const theme = useTheme();
  const intl = useIntl();
  const { form } = useComposer();
  const { editor, setActiveFormLanguage, setActivePage, setHighlightedItem, setActiveVariableTab, setErrors } = useEditor();
  const { config, createPreviewSession } = useBackend();
  const headerPaddingSx = { px: theme.spacing(1) };
  const formLanguages = form.metadata.languages || ['en'];
  const currentTag = form._tag ?? 'LATEST';
  const status = getStatus(editor.errors);
  const [listsDialogOpen, setListsDialogOpen] = React.useState(false);
  const [translationsDialogOpen, setTranslationsDialogOpen] = React.useState(false);
  const [optionsDialogOpen, setOptionsDialogOpen] = React.useState(false);
  const [variablesDialogOpen, setVariablesDialogOpen] = React.useState(false);
  const [previewDialogOpen, setPreviewDialogOpen] = React.useState(false);
  const [versioningDialogOpen, setVersioningDialogOpen] = React.useState(false);
  const [createTagDialogOpen, setCreateTagDialogOpen] = React.useState(false);
  const [anchorElLanguage, setAnchorElLanguage] = React.useState<null | HTMLElement>(null);
  const [anchorElVersion, setAnchorElVersion] = React.useState<null | HTMLElement>(null);
  const [searchAnchor, setSearchAnchor] = React.useState<null | HTMLElement>(null);
  const [searchKeyword, setSearchKeyword] = React.useState('');
  const [searchMatches, setSearchMatches] = React.useState<SearchMatch[]>([]);

  const handleLanguageMenuOpen = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorElLanguage(event.currentTarget);
  }

  const handleLanguageSelect = (languageCode: string) => {
    setActiveFormLanguage(languageCode);
    setAnchorElLanguage(null);
  }

  const handleOpenVersioningDialog = () => {
    setAnchorElVersion(null);
    setVersioningDialogOpen(true);
  }

  const handleOpenCreateTagDialog = () => {
    setAnchorElVersion(null);
    setCreateTagDialogOpen(true);
  }

  const handleInitPreview = () => {
    const contextVariables = form.variables?.filter(isContextVariable);
    if (contextVariables && contextVariables.length > 0) {
      setPreviewDialogOpen(true);
    } else {
      createPreviewSession(form._id, editor.activeFormLanguage).then((response) => {
        const result = response.result as CreateSessionResult;
        if (response.success) {
          const win = window.open(`${config.transport.previewUrl}/${result._id}`);
          if (win) {
            win.focus();
          } else {
            setErrors([{ level: 'FATAL', message: 'FATAL_POPUP' }]);
          }
        } else if (response.apiError) {
          setErrors([{ level: 'FATAL', message: response.apiError.message }]);
        }
      });
    }
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

  const handleClose = () => {
    config.closeHandler();
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
      <PreviewDialog open={previewDialogOpen} onClose={() => setPreviewDialogOpen(false)} />
      <VersioningDialog open={versioningDialogOpen} onClose={() => setVersioningDialogOpen(false)} />
      <CreateTagDialog open={createTagDialogOpen} onClose={() => setCreateTagDialogOpen(false)} />
      <AppBar position="fixed" color='inherit' sx={{ zIndex: theme.zIndex.drawer + 1, marginRight: -SCROLLBAR_WIDTH }}>
        <Stack direction='row' divider={<Divider orientation='vertical' flexItem />}>
          <Box sx={{ display: 'flex', alignItems: 'center', ...headerPaddingSx }}>
            {/* eslint-disable-next-line formatjs/no-literal-string-in-jsx */}
            <Typography sx={{ fontWeight: 'bold' }}>Dialob Composer</Typography>
            <Typography sx={{ ml: 1 }}>
              {form.metadata.label}
            </Typography>
          </Box>
          <HeaderButton label='header.translations' onClick={() => setTranslationsDialogOpen(true)} />
          <HeaderButton label='header.variables' onClick={() => setVariablesDialogOpen(true)} />
          <HeaderButton label='header.lists' onClick={() => setListsDialogOpen(true)} />
          <HeaderButton label='header.options' onClick={() => setOptionsDialogOpen(true)} />
          <HeaderButton endIcon={<ArrowDropDown />}
            label={intl.formatMessage({ id: 'header.version' }) + ": " + currentTag}
            onClick={(e) => setAnchorElVersion(e.currentTarget)} />
          <Menu open={Boolean(anchorElVersion)} anchorEl={anchorElVersion} onClose={() => setAnchorElVersion(null)} disableScrollLock={true}>
            <MenuItem onClick={handleOpenVersioningDialog}>
              <FormattedMessage id='menus.versions.manage' />
            </MenuItem>
            <MenuItem onClick={handleOpenCreateTagDialog}>
              <FormattedMessage id='menus.versions.create' />
            </MenuItem>
          </Menu>
          <Tooltip title={<FormattedMessage id='header.help' />} placement='bottom'>
            <ResponsiveButton onClick={() => window.open('https://docs.dialob.io/', "_blank")} variant='text' color='inherit' >
              <Support fontSize='small' />
            </ResponsiveButton>
          </Tooltip>
          <Box flexGrow={1} />
          <Box sx={{ display: 'flex', alignItems: 'center', ...headerPaddingSx }} onClick={(e) => setSearchAnchor(e.currentTarget)}>
            <TextField
              variant='standard'
              placeholder={intl.formatMessage({ id: 'header.search' })}
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
                <Typography color='text.hint'><FormattedMessage id='header.search.hint' /></Typography>
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
          <Tooltip title={<FormattedMessage id='header.download' />} placement='bottom'>
            <ResponsiveButton onClick={() => downloadForm(form)} variant='text' color='inherit' >
              <Download />
            </ResponsiveButton>
          </Tooltip>
          <Tooltip title={<FormattedMessage id='header.status' values={{ status }} />} placement='bottom'>
            <ResponsiveButton variant='text' color='inherit'>
              {getStatusIcon(editor.errors)}
            </ResponsiveButton>
          </Tooltip>
          <HeaderButton label={'locales.' + editor.activeFormLanguage} endIcon={<ArrowDropDown />} onClick={handleLanguageMenuOpen} />
          <Menu open={Boolean(anchorElLanguage)} anchorEl={anchorElLanguage} onClose={() => setAnchorElLanguage(null)} disableScrollLock={true}>
            {formLanguages
              .filter((language) => language !== editor.activeFormLanguage)
              .map((language) => (
                <MenuItem key={language} onClick={() => handleLanguageSelect(language)}>
                  <FormattedMessage id={`locales.${language}`} />
                </MenuItem>
              ))}
          </Menu>
          <Tooltip title={<FormattedMessage id='header.preview' />} placement='bottom'>
            <Box sx={{ display: 'flex', alignItems: 'center' }}>
              <ResponsiveButton variant='text' color='inherit' onClick={handleInitPreview} disabled={status === 'FATAL' || status === 'ERROR'}>
                <Visibility fontSize='small' color={(status === 'FATAL' || status === 'ERROR') ? 'disabled' : 'primary'} />
              </ResponsiveButton>
            </Box>
          </Tooltip>
          <Tooltip title={<FormattedMessage id='header.close' />} placement='bottom'>
            <ResponsiveButton variant='text' color='inherit' onClick={handleClose}>
              <Close sx={{ color: 'error.dark' }} />
            </ResponsiveButton>
          </Tooltip>
        </Stack>
      </AppBar>
    </>
  );
};

export default MenuBar;
