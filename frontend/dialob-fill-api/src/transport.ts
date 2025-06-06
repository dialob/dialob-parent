import { Action } from './actions';
import { TransportConfig } from './config';
import { DialobRequestError } from './error';

export interface DialobResponse {
  rev: number;
  actions?: Action[];
};

export interface Transport {
  getFullState(sessionId: string): Promise<DialobResponse>;
  update(sessionId: string, actions: Action[], rev: number): Promise<DialobResponse>;
}

export class RESTTransport implements Transport {
  endpoint: string;
  config: TransportConfig;
  headers: { [name: string]: string };

  constructor(endpoint: string, config: TransportConfig) {
    this.endpoint = endpoint;
    this.config = config;
    this.headers = {
      'Accept': 'application/json',
      'Content-Type': 'application/json; charset=UTF-8',
      ...config.headers,
    };
  }

  async fetch(sessionId: string, data?: RequestInit['body']): Promise<DialobResponse> {
    const response = await window.fetch(`${this.endpoint}/${sessionId}`, {
      method: data ? 'POST' : 'GET',
      body: data,
      headers: this.headers,
      credentials: this.config.credentials,
    });
    if (!response.ok) {
      throw new DialobRequestError('Failure during fetch', response.status);
    }
    return response.json();
  }

  getFullState(sessionId: string): Promise<DialobResponse> {
    return this.fetch(sessionId);
  }

  update(sessionId: string, actions: Action[], rev: number): Promise<DialobResponse> {
    return this.fetch(sessionId, JSON.stringify({
      rev: rev,
      actions,
    }));
  }
}
