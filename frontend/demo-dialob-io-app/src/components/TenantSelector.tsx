import React, { useState } from 'react';
import { MenuItem, Menu, Button, Box } from '@mui/material';
import { FormattedMessage } from 'react-intl';
import { Tenant } from '../types/index';
import { useTenantContext } from '../context/useTenantContext';
import ArrowDropDownIcon from '@mui/icons-material/ArrowDropDown';

const TenantSelector: React.FC = () => {
  const { tenants, selectedTenant, selectTenant } = useTenantContext();
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);

  const handleOpen = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const handleSelect = (tenant: Tenant) => {
    selectTenant(tenant);
    handleClose();
  };

  return (
    <Box sx={{ mt: 2, ml: 2 }}>
      <Button
        id="tenant-menu-button"
        variant="text"
        onClick={handleOpen}
        endIcon={<ArrowDropDownIcon fontSize='small' />}
      >
        {selectedTenant
          ? (selectedTenant.name || selectedTenant.description || <FormattedMessage id='placeholders.tenants.unnamed' />)
          : <FormattedMessage id='placeholders.tenants' />}
      </Button>
      <Menu
        anchorEl={anchorEl}
        open={Boolean(anchorEl)}
        onClose={handleClose}
        MenuListProps={{
          'aria-labelledby': 'tenant-menu-button',
        }}
      >
        {tenants.map((tenant) => (
          <MenuItem
            key={tenant.id}
            value={tenant.id}
            onClick={() => handleSelect(tenant)}
            selected={selectedTenant?.id === tenant.id}
          >
            {tenant.name || tenant.description || <FormattedMessage id='placeholders.tenants.unnamed' />}
          </MenuItem>
        ))}
      </Menu>
    </Box>
  );
};

export default TenantSelector;