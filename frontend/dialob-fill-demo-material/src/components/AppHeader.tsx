import React, { useState } from 'react';
import { AppBar, Toolbar, Button, Menu, MenuItem, Container, Grid, Box, Typography } from '@mui/material';
import logoSvg from '../images/logo192.png';
import { FormattedMessage, useIntl } from 'react-intl';
import ArrowDropDownIcon from '@mui/icons-material/ArrowDropDown';
import { THEMES } from '../theme';

const UI_LANGUAGES = ['en', 'fi', 'sv', 'et', 'ms'];

interface AppHeaderProps {
  setLocale: (locale: string) => void;
  setThemeIndex: (index: number) => void;
  themeIndex: number;
}

export const AppHeader: React.FC<AppHeaderProps> = ({ setLocale, setThemeIndex, themeIndex }) => {
  const intl = useIntl();
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const [themeSelAnchorEl, setThemeSelAnchorEl] = useState<null | HTMLElement>(null);

  const handleLanguageMenuOpen = (event: React.MouseEvent<HTMLButtonElement>) => {
    setAnchorEl(event.currentTarget);
  }

  const handleLanguageMenuClose = () => {
    setAnchorEl(null);
  }

  const handleLanguageSelect = (language: string) => {
    handleLanguageMenuClose();
    setLocale(language);
  }

  const handleThemeMenuOpen = (event: React.MouseEvent<HTMLButtonElement>) => {
    setThemeSelAnchorEl(event.currentTarget);
  }

  const handleThemeMenuClose = () => {
    setThemeSelAnchorEl(null);
  }

  const handleThemeSelect = (idx: number) => {
    handleThemeMenuClose();
    setThemeIndex(idx);
  }

  const selectLang = (
    <Button aria-controls='language-menu' aria-haspopup='true' onClick={handleLanguageMenuOpen} color='secondary'>
      <Box sx={{ display: { xs: 'none', md: 'block' } }}><FormattedMessage id={`locale.${intl.locale}`} /></Box>
      <ArrowDropDownIcon fontSize='small' />
    </Button>
  );
  const menuLang = (
    <Menu id='language-menu' anchorEl={anchorEl} keepMounted open={Boolean(anchorEl)} onClose={handleLanguageMenuClose}>
      {UI_LANGUAGES.map(lang =>
        <MenuItem key={lang} selected={lang === intl.locale} onClick={() => handleLanguageSelect(lang)}>
          <FormattedMessage id={`locale.${lang}`} />
        </MenuItem>)}
    </Menu>);

  const selectTheme = (
    <Button aria-controls='theme-menu' aria-haspopup='true' onClick={handleThemeMenuOpen} color='secondary'>
      <Box><FormattedMessage id='theme' />: {THEMES[themeIndex].name}</Box>
      <ArrowDropDownIcon fontSize='small' />
    </Button>
  );

  const menuTheme = (
    <Menu id='theme-menu' anchorEl={themeSelAnchorEl} keepMounted open={Boolean(themeSelAnchorEl)} onClose={handleThemeMenuClose}>
      {THEMES.map((theme, idx) =>
        <MenuItem key={idx} selected={idx === themeIndex} onClick={() => handleThemeSelect(idx)}>
          {THEMES[idx].name}
        </MenuItem>)}
    </Menu>
  );

  const logo = (<Box><Box component='img' src={logoSvg} sx={{ width: 50, height: 50, m: '2px', verticalAlign: 'center' }} alt='Dialob' /></Box>);
  const title = (<Box sx={{ flexGrow: 1 }}><Typography variant='h6'>Dialob</Typography></Box>);
  return (
    <Container maxWidth='xl'>
      <Grid container>
        <Grid item xs={12}>
          <AppBar position="sticky" color="inherit" elevation={1}>
            <Toolbar sx={{ pt: 2, pr: 1, pb: 2, pl: 1 }} >{logo}{title}{selectTheme}{menuTheme}{selectLang}{menuLang}</Toolbar>
          </AppBar>
        </Grid>
      </Grid>
    </Container>
  );
}
