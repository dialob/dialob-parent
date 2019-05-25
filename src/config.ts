export interface TransportConfig {
  mode?: 'rest';
  credentials?: RequestInit['credentials'];
  headers?: {
    [name: string]: string;
  };
}

export interface SessionConfig {
  endpoint: string;
  transport?: TransportConfig;
}
