import React, { useState } from 'react';
import { Theme, AppBar, Toolbar, Button, Menu, MenuItem, Container, Grid } from '@material-ui/core';
import { makeStyles } from '@material-ui/styles';
import logoSvg from '../images/logo192.png';
import { FormattedMessage, useIntl } from 'react-intl';
import ArrowDropDownIcon from '@material-ui/icons/ArrowDropDown';

const UI_LANGUAGES = ['en', 'fi', 'sv', 'et'];

const useStyles = makeStyles((theme: Theme) => (
  {

    toolbarPadding: {
      padding: '30px 20px 30px 30px',
    },
    toolbarTitle: { 
      flexGrow: 1,
    },
    toolbarImage: {
      verticalAlign: 'center',
      margin: '2px',
    },
    languageLabel: {
      display: 'none',
      [theme.breakpoints.up('md')]: { display: 'block' },
    },
    languageList: {
      color: theme.palette.secondary.main
    },
    arrowIcon: {
      fontSize: '2.5em',
    }
  }
));

interface AppHeaderProps {
  setLocale: (locale: string) => void
}

export const AppHeader: React.FC<AppHeaderProps> = ({setLocale}) => {
  const classes = useStyles();
  const intl = useIntl();
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);

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

  const selectLang = (
    <Button aria-controls='language-menu' aria-haspopup='true' onClick={handleLanguageMenuOpen} color='secondary'>
      <span className={classes.languageLabel}><FormattedMessage id={`locale.${intl.locale}`} /></span>
      <ArrowDropDownIcon className={classes.arrowIcon}/>
    </Button>
  );
  const menuLang = (
    <Menu id='language-menu' anchorEl={anchorEl} keepMounted open={Boolean(anchorEl)} onClose={handleLanguageMenuClose}>
      { UI_LANGUAGES.map(lang => 
          <MenuItem key={lang} selected={lang === intl.locale} onClick={() => handleLanguageSelect(lang)}>
            <span className={classes.languageList}><FormattedMessage id={`locale.${lang}`} /></span>
          </MenuItem>) }
    </Menu>);
  
  const logo = (<div className={classes.toolbarTitle}><img src={logoSvg} width={50} height={50} className={classes.toolbarImage} alt=''/></div>);
  return (
    <Container>
      <Grid item xs={12}>
        <Grid item xs={12}>
          <AppBar position="sticky" color="inherit" elevation={1}>
            <Toolbar className={classes.toolbarPadding}>{logo}{selectLang}{menuLang}</Toolbar>
          </AppBar>
        </Grid>
      </Grid>
    </Container>
  );
}