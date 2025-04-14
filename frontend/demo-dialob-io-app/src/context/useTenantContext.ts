import { useContext } from 'react';
import { TenantContextType } from '../types';
import { TenantContext } from './TenantContext';

export const useTenantContext = (): TenantContextType => {
  const context = useContext(TenantContext);
  if (!context) {
    throw new Error('useTenantContext must be used within a TenantProvider');
  }
  return context;
};
