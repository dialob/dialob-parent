export interface CsrfShape {
  key: string;
  value: string;
}

export interface DialobAdminConfig {
  dialobApiUrl: string;
  setLoginRequired: () => void;
  setTechnicalError: () => void;
  language: string;
  csrf?: CsrfShape;
  tenantId?: string;
}

export interface DialobAdminViewProps {
  config: DialobAdminConfig;
  showNotification?: (message: string, severity: 'success' | 'error') => void;
}

export interface DialobDashboardFetchProviderProps {
  children: React.ReactNode;
  fetch?: FetchAuthFunction;
}

export type FetchAuthFunction = typeof window.fetch;