import React from 'react';
import { MenuItem, Box, FormControl, Typography, Select, SelectChangeEvent, useTheme } from '@mui/material';
import { FormattedMessage } from 'react-intl';
import { useTenantContext } from '../context/useTenantContext';

const TenantSelector: React.FC = () => {
  const { tenants, selectedTenant, selectTenant } = useTenantContext();
  const theme = useTheme();

  const styles = {
    select: {
      width: "100%",
      height: "28px",
      lineHeight: "28px",
      '& .MuiSelect-select': {
        backgroundColor: theme.palette.article.contrastText,
        minWidth: "0px !important"
      },
      '&.Mui-focused .MuiSelect-select': {
        backgroundColor: theme.palette.article.contrastText,
      },
    },
    container: {
      display: 'flex',
      alignItems: 'center',
    },
    typography: {
      ml: 4,
      mr: 1,
      color: theme.palette.text.primary,
      whiteSpace: 'nowrap'
    }
  };

  const handleSelect = (event: SelectChangeEvent<string>) => {
    const selectedId = event.target.value;
    const tenant = tenants.find((tenant) => tenant.id === selectedId);
    if (tenant) {
      selectTenant(tenant);
    }
  };

  return (
    <Box sx={styles.container}>
      <Typography sx={styles.typography}>
        <FormattedMessage id="placeholders.tenants" />
      </Typography>
      <FormControl fullWidth>
        <Select
          value={selectedTenant?.id || ""}
          onChange={handleSelect}
          variant='standard'
          sx={styles.select}
          disableUnderline
        >
          {tenants.map((tenant) => (
            <MenuItem key={tenant.id} value={tenant.id}>
              {tenant.name || tenant.description || (
                <FormattedMessage id="placeholders.tenants.unnamed" />
              )}
            </MenuItem>
          ))}
        </Select>
      </FormControl>
    </Box>
  );
};

export default TenantSelector;
