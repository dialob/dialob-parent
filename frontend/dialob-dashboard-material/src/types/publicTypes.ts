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
  fetchAuth?: (
    input: string,
    init: RequestInit,
    config: DialobAdminConfig
  ) => Promise<Response>;
}

export interface DialobAdminViewProps {
  config: DialobAdminConfig;
  showNotification?: (message: string, severity: 'success' | 'error') => void;
}