export interface CsrfShape {
  key: string;
  value: string;
}

export type FetchAuthFn = (
  input: string,
  init: RequestInit,
  config: DialobAdminConfig
) => Promise<Response>;

export interface DialobAdminConfig {
  dialobApiUrl: string;
  setLoginRequired: () => void;
  setTechnicalError: () => void;
  language: string;
  csrf?: CsrfShape;
  tenantId?: string;
  fetchAuth?: FetchAuthFn;
}

export interface DialobAdminViewProps {
  config: DialobAdminConfig;
  showNotification?: (message: string, severity: 'success' | 'error') => void;
}