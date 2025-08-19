import { Metadata } from "./types";

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

export interface DialobAdminProps {
  config: DialobAdminConfig;
  showNotification?: (message: string, severity: 'success' | 'error') => void;
  onOpenForm?: (formId: string) => void;
}

export interface DialobForm {
  _id?: string;
  _rev?: string;
  name?: string;
  metadata: Metadata;
  data: Record<string, unknown>;
  variables: unknown[];
  namespaces: Record<string, unknown>;
  valueSets: unknown[];
  requiredErrorText: Record<string, string>;
}