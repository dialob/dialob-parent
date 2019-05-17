export interface TransportConfig {
  mode?: 'rest';
  credentials?: RequestInit['credentials'];
  headers?: {
    [name: string]: string;
  };
}

export interface Config {
  endpoint: string;
  transport?: TransportConfig;
}
