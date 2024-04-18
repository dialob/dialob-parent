import React from 'react';
import { AppBar, Box, Divider, InputBase, Stack, Typography, useTheme, Button, Menu, MenuItem, styled } from '@mui/material';
import { ArrowDropDown, Close, Download, Search, Support, Visibility } from '@mui/icons-material';
import { FormattedMessage, useIntl } from 'react-intl';
import { useComposer } from '../../dialob';
import { getStatusIcon } from '../../utils/ErrorUtils';
import { useEditor } from '../../editor';
import { SCROLLBAR_WIDTH } from '../../theme/siteTheme';
import GlobalListsDialog from '../../dialogs/GlobalListsDialog';
import TranslationDialog from '../../dialogs/TranslationDialog';
import FormOptionsDialog from '../../dialogs/FormOptionsDialog';
import VariablesDialog from '../../dialogs/VariablesDialog';
import VersioningDialog from '../../dialogs/VersioningDialog';
import CreateTagDialog from '../../dialogs/CreateTagDialog';
import { downloadForm } from '../../utils/ParseUtils';

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

const HeaderIconButton: React.FC<{ icon: React.ReactElement, disabled?: boolean, onClick?: () => void }> = ({ icon, disabled, onClick }) => {
  return (
    <ResponsiveButton variant='text' color='inherit' disabled={disabled} onClick={onClick}>
      {icon}
    </ResponsiveButton>
  );
};

const MenuBar: React.FC = () => {
  const theme = useTheme();
  const intl = useIntl();
  const { form } = useComposer();
  const { editor, setActiveFormLanguage } = useEditor();
  const headerPaddingSx = { px: theme.spacing(1) };
  const formLanguages = form.metadata.languages || ['en'];
  const currentTag = form._tag ?? 'LATEST';
  const [listsDialogOpen, setListsDialogOpen] = React.useState(false);
  const [translationsDialogOpen, setTranslationsDialogOpen] = React.useState(false);
  const [optionsDialogOpen, setOptionsDialogOpen] = React.useState(false);
  const [variablesDialogOpen, setVariablesDialogOpen] = React.useState(false);
  const [versioningDialogOpen, setVersioningDialogOpen] = React.useState(false);
  const [createTagDialogOpen, setCreateTagDialogOpen] = React.useState(false);
  const [anchorElLanguage, setAnchorElLanguage] = React.useState<null | HTMLElement>(null);
  const [anchorElVersion, setAnchorElVersion] = React.useState<null | HTMLElement>(null);

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

  return (
    <>
      <GlobalListsDialog open={listsDialogOpen} onClose={() => setListsDialogOpen(false)} />
      <TranslationDialog open={translationsDialogOpen} onClose={() => setTranslationsDialogOpen(false)} />
      <FormOptionsDialog open={optionsDialogOpen} onClose={() => setOptionsDialogOpen(false)} />
      <VariablesDialog open={variablesDialogOpen} onClose={() => setVariablesDialogOpen(false)} />
      <VersioningDialog open={versioningDialogOpen} onClose={() => setVersioningDialogOpen(false)} />
      <CreateTagDialog open={createTagDialogOpen} onClose={() => setCreateTagDialogOpen(false)} />
      <AppBar position="fixed" color='inherit' sx={{ zIndex: theme.zIndex.drawer + 1, marginRight: -SCROLLBAR_WIDTH }}>
        <Stack direction='row' divider={<Divider orientation='vertical' flexItem />}>
          <Box sx={{ display: 'flex', alignItems: 'center', ...headerPaddingSx }}>
            <Typography sx={{ fontWeight: 'bold' }}>
              Dialob Composer
            </Typography>
            <Typography sx={{ ml: 1 }}>
              {form.metadata.label}
            </Typography>
          </Box>
          <HeaderButton label='translations' onClick={() => setTranslationsDialogOpen(true)} />
          <HeaderButton label='variables' onClick={() => setVariablesDialogOpen(true)} />
          <HeaderButton label='lists' onClick={() => setListsDialogOpen(true)} />
          <HeaderButton label='options' onClick={() => setOptionsDialogOpen(true)} />
          <HeaderButton endIcon={<ArrowDropDown />}
            label={intl.formatMessage({ id: 'version' }) + ": " + currentTag}
            onClick={(e) => setAnchorElVersion(e.currentTarget)} />
          <Menu open={Boolean(anchorElVersion)} anchorEl={anchorElVersion} onClose={() => setAnchorElVersion(null)} disableScrollLock={true}>
            <MenuItem onClick={handleOpenVersioningDialog}>
              <FormattedMessage id='menus.versions.manage' />
            </MenuItem>
            <MenuItem onClick={handleOpenCreateTagDialog}>
              <FormattedMessage id='menus.versions.create' />
            </MenuItem>
          </Menu>
          <HeaderIconButton icon={<Support fontSize='small' />} onClick={() => window.open('https://docs.dialob.io/', "_blank")} />
          <Box flexGrow={1} />
          <Box sx={{ display: 'flex', alignItems: 'center', ...headerPaddingSx }}>
            <InputBase placeholder={intl.formatMessage({ id: 'search' })} />
            <Search />
          </Box>
          <HeaderIconButton icon={<Download />} onClick={() => downloadForm(form)} />
          <HeaderIconButton disabled icon={getStatusIcon(editor.errors)} />
          <HeaderButton label={'locales.' + editor.activeFormLanguage} endIcon={<ArrowDropDown />} onClick={handleLanguageMenuOpen} />
          <Menu open={Boolean(anchorElLanguage)} anchorEl={anchorElLanguage} onClose={() => setAnchorElLanguage(null)} disableScrollLock={true}>
            {formLanguages
              .filter((language) => language !== editor.activeFormLanguage)
              .map((language) => (
                <MenuItem key={language} onClick={() => handleLanguageSelect(language)}>
                  {intl.formatMessage({ id: 'locales.' + language })}
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
