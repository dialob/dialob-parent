import React, { createContext, useState, useEffect } from 'react';
import { AppConfig, Tenant, TenantContextType } from '../types';

const TenantContext = createContext<TenantContextType | undefined>(undefined);

export const TenantProvider: React.FC<{ appConfig: AppConfig; children: React.ReactNode }> = ({ appConfig, children }) => {
  const [tenants, setTenants] = useState<Tenant[]>([]);
  const [selectedTenant, setSelectedTenant] = useState<Tenant | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const baseUrl = window.location.origin;

  useEffect(() => {
    const fetchTenants = async () => {
      setIsLoading(true);
      try {
        const apiUrl = appConfig.url.includes('://') ? appConfig.url : baseUrl + appConfig.url;
        const response = await fetch(`${apiUrl}/api/tenants`, {
          method: 'GET',
          credentials: appConfig.credentialMode || 'same-origin',
          headers: appConfig.csrf ? { [appConfig.csrf.headerName]: appConfig.csrf.token } : undefined,
        });

        if (!response.ok) {
          throw new Error(`Failed to fetch tenants: ${response.status}`);
        }

        const data = await response.json();
        setTenants(data);
        setError(null);
      } catch (err) {
        setTenants([]);
        setError((err as Error).message);
      } finally {
        setIsLoading(false);
      }
    };

    fetchTenants();
  }, [appConfig.url, appConfig.credentialMode, appConfig.csrf, baseUrl]);

  const selectTenant = (tenant: Tenant) => setSelectedTenant(tenant);

  return (
    <TenantContext.Provider value={{ tenants, selectedTenant, selectTenant, isLoading, error }}>
      {children}
    </TenantContext.Provider>
  );
};

export { TenantContext };