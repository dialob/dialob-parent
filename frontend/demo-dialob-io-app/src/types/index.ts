export interface AppConfig {
  csrf: CsrfToken;
  url: string;
  credentialMode: RequestCredentials;
}

export interface CsrfToken {
  headerName: string;
  token: string;
}

export interface Tenant {
  id: string;
  name: string;
  description?: string;
}

export interface TenantContextType {
  tenants: Tenant[];
  selectedTenant: Tenant | null;
  selectTenant: (tenant: Tenant) => void;
  isLoading: boolean;
  error: string | null;
}