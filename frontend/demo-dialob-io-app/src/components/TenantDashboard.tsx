import React from 'react';
import { DialobAdmin, DialobAdminConfig } from '@dialob/dashboard-material';
import { FormattedMessage } from 'react-intl';
import { AppConfig } from '../types';
import { useTenantContext } from '../context/useTenantContext';

const TenantDashboard: React.FC<{ appConfig: AppConfig }> = ({ appConfig }) => {
  const { selectedTenant } = useTenantContext();

  if (!selectedTenant) {
    return <FormattedMessage id='placeholders.tenants.unselected' />;
  }

  const config: DialobAdminConfig = {
    csrf: {
      key: appConfig?.csrf?.headerName,
      value: appConfig?.csrf?.token,
    },
    dialobApiUrl: appConfig.url,
    setLoginRequired: () => console.log('Login required'),
    setTechnicalError: () => console.log('Technical error occurred'),
    language: 'en',
    tenantId: selectedTenant.id
  };

  return (
    <DialobAdmin config={config}/>
  );
};

export default TenantDashboard;