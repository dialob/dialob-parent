import React from 'react';
import { DialobAdminProps } from '@dialob/dashboard-material';
import { AppConfig } from '../types';
import { useTenantContext } from '../context/useTenantContext';
import { Box } from '@mui/material';
import { useIntl } from 'react-intl';
import { DialobAdmin } from './DialobAdmin';

const TenantDashboard: React.FC<{ appConfig: AppConfig }> = ({ appConfig }) => {
  const { selectedTenant } = useTenantContext();
  const intl = useIntl();

  if (!selectedTenant) {
    return;
  }

  const config: DialobAdminProps = {
    csrf: {
      key: appConfig?.csrf?.headerName,
      value: appConfig?.csrf?.token,
    },
    // workaround to clean the "/api" from the end of the URL
    dialobApiUrl: appConfig.url.endsWith('/api') ? appConfig.url.substring(0, appConfig.url.length - 4) : appConfig.url,
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
