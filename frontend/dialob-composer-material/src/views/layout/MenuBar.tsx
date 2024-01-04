import React from 'react';
import { AppBar, Box, Divider, InputBase, Stack, Typography, useTheme, Button, Menu, MenuItem } from '@mui/material';
import { ArrowDropDown, Check, Close, Download, Search, Support, Visibility } from '@mui/icons-material';
import { FormattedMessage, useIntl } from 'react-intl';
import { useComposer } from '../../dialob';
import { useEditor } from '../../editor';


const HeaderButton: React.FC<{
  label: string,
  startIcon?: React.ReactElement,
  endIcon?: React.ReactElement,
  onClick?: (event: React.MouseEvent<HTMLButtonElement>) => void
}> = ({ label, startIcon, endIcon, onClick }) => {
  const intl = useIntl();
  const stringExists = !!intl.messages[label];
  return (
    <Button variant='text' color='inherit' sx={{ px: 1 }} startIcon={startIcon} endIcon={endIcon} onClick={onClick}>
      {stringExists ? <FormattedMessage id={label} /> : label}
    </Button>
  );
};

const HeaderIconButton: React.FC<{ icon: React.ReactElement }> = ({ icon }) => {
  return (
    <Button variant='text' color='inherit' sx={{ px: 1 }}>
      {icon}
    </Button>
  );
};

const MenuBar: React.FC = () => {
  const theme = useTheme();
  const intl = useIntl();
  const { form } = useComposer();
  const { editor, setActiveFormLanguage } = useEditor();
  const headerPaddingSx = { px: theme.spacing(1) };
  const formLanguages = form.metadata.languages || ['en'];

  const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
  const languageMenuOpen = Boolean(anchorEl);

  const handleLanguageMenuOpen = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  }

  const handleLanguageSelect = (languageCode: string) => {
    setActiveFormLanguage(languageCode);
    setAnchorEl(null);
  }

  return (
    <AppBar position="fixed" color='inherit' sx={{ zIndex: theme.zIndex.drawer + 1 }}>
      <Stack direction='row' divider={<Divider orientation='vertical' flexItem />}>
        <Box sx={{ display: 'flex', alignItems: 'center', ...headerPaddingSx }}>
          <Typography sx={{ fontWeight: 'bold' }}>
            Dialob Composer
          </Typography>
          <Typography sx={{ ml: 1 }}>
            {form.metadata.label}
          </Typography>
        </Box>
        <HeaderButton label='translations' />
        <HeaderButton label='variables' />
        <HeaderButton label='lists' />
        <HeaderButton label='options' />
        <HeaderButton label={intl.formatMessage({ id: 'version' }) + ": " + intl.formatMessage({ id: 'version.latest' })} endIcon={<ArrowDropDown />} />
        <HeaderIconButton icon={<Support fontSize='small' />} />
        <Box flexGrow={1} />
        <Box sx={{ display: 'flex', alignItems: 'center', ...headerPaddingSx }}>
          <InputBase placeholder={intl.formatMessage({ id: 'search' })} />
          <Search />
        </Box>
        <HeaderIconButton icon={<Download />} />
        <HeaderIconButton icon={<Check color='success' />} />
        <HeaderButton label={'locales.' + editor.activeFormLanguage} endIcon={<ArrowDropDown />} onClick={handleLanguageMenuOpen} />
        <Menu open={languageMenuOpen} anchorEl={anchorEl} onClose={() => setAnchorEl(null)} disableScrollLock={true}>
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
  );
};

export default MenuBar;
