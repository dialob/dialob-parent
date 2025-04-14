import React, { useState } from 'react';
import { AppBar, Toolbar, Box, Typography, useTheme, Theme } from '@mui/material';
import { FormattedMessage } from 'react-intl';
import TenantSelector from './TenantSelector';
import { useTenantContext } from '../context/useTenantContext';

const Header: React.FC = () => {
  const [imageError, setImageError] = useState(false);
  const theme: Theme = useTheme();
  const { error } = useTenantContext();

  const styles = {
    logo: {
      width: 180,
      height: 40,
    },
    fallbackTitle: {
      color: theme.palette.activeItem.contrastText,
    },
    toolbar: {
      display: "flex",
      alignItems: "center",
      height: "80px"
    }
  };

  return (
    <AppBar position="static" elevation={1}>
      <Toolbar sx={styles.toolbar}>
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
        {!error && <TenantSelector />}
      </Toolbar>
    </AppBar>
  );
};

export default Header;
