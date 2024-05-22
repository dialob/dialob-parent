import React from 'react';
import { DialogActions, Button, Menu, MenuItem } from '@mui/material';
import { FormattedMessage } from 'react-intl';
import { ArrowDropDown, Help } from '@mui/icons-material';
import { useComposer } from '../dialob';

export const DialogActionButtons: React.FC<{ handleClose: () => void, handleClick: () => void }> = ({ handleClose, handleClick }) => {
  return (
    <DialogActions>
      <Button onClick={handleClose} variant='text' color='error'><FormattedMessage id='buttons.cancel' /></Button>
      <Button onClick={handleClick} variant='contained'><FormattedMessage id='buttons.confirm' /></Button>
    </DialogActions>
  );
};

export const DialogHelpButton: React.FC<{ helpUrl: string }> = ({ helpUrl }) => {
  return (
    <Button color='inherit' variant='contained' endIcon={<Help />} onClick={() => window.open(helpUrl, "_blank")}>
      <FormattedMessage id='buttons.help' />
    </Button>
  );
}

export const DialogLanguageMenu: React.FC<{ activeLanguage: string, setActiveLanguage: (languageCode: string) => void }> = ({ activeLanguage, setActiveLanguage }) => {
  const { form } = useComposer();
  const formLanguages = form.metadata.languages || ['en'];
  const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
  const languageMenuOpen = Boolean(anchorEl);

  const handleLanguageMenuOpen = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  }

  const handleLanguageSelect = (languageCode: string) => {
    setActiveLanguage(languageCode);
    setAnchorEl(null);
  }

  return (
    <>
      <Button endIcon={<ArrowDropDown />} onClick={handleLanguageMenuOpen} color='inherit' variant='contained'>
        <FormattedMessage id={'locales.' + activeLanguage} />
      </Button>
      <Menu open={languageMenuOpen} anchorEl={anchorEl} onClose={() => setAnchorEl(null)} disableScrollLock={true}>
        {formLanguages
          .filter((language) => language !== activeLanguage)
          .map((language) => (
            <MenuItem key={language} onClick={() => handleLanguageSelect(language)}>
              <FormattedMessage id={'locales.' + language} />
            </MenuItem>
          ))}
      </Menu>
    </>
  );
}
