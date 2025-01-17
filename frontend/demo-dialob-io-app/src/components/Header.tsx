import React, { useState } from 'react';
import { AppBar, Toolbar, Box, Typography, useTheme, Theme } from '@mui/material';
import { FormattedMessage } from 'react-intl';

const Header: React.FC = () => {
  const [imageError, setImageError] = useState(false);
  const theme: Theme = useTheme();

  const styles = {
    logo: {
      width: 180,
      height: 40,
    },
    fallbackTitle: {
      color: theme.palette.activeItem.contrastText,
    },
  };

  return (
    <AppBar position="static" elevation={1}>
      <Toolbar>
        {!imageError ? (
          <Box
            component="img"
            src="https://cdn.resys.io/dialob_logo.svg"
            alt="Dialob Logo"
            sx={styles.logo}
            onError={() => setImageError(true)}
          />
        ) : (
          <Typography variant="h3" sx={styles.fallbackTitle}>
            <FormattedMessage id='placeholders.appbar.header' />
          </Typography>
        )}
      </Toolbar>
    </AppBar>
  );
};

export default Header;