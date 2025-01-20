import React from 'react';
import { DialobAdmin, DialobAdminConfig } from '@dialob/dashboard-material';
import { AppConfig } from '../types';
import { useTenantContext } from '../context/useTenantContext';
import { Box } from '@mui/material';
import { useIntl } from 'react-intl';

const TenantDashboard: React.FC<{ appConfig: AppConfig }> = ({ appConfig }) => {
  const { selectedTenant } = useTenantContext();
  const intl = useIntl();

  if (!selectedTenant) {
    return;
  }

  const config: DialobAdminConfig = {
    csrf: {
      key: appConfig?.csrf?.headerName,
      value: appConfig?.csrf?.token,
    },
    dialobApiUrl: appConfig.url,
    setLoginRequired: () => console.log('Login required'),
    setTechnicalError: () => console.log('Technical error occurred'),
    language: intl.locale,
    tenantId: selectedTenant.id
  };

  return (
    <Box p={2}>
      <DialobAdmin config={config} />
    </Box>
  );
};

export default TenantDashboard;