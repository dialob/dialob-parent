import type { DialobAdminConfig } from '../types';

export const getHeaders = (config: DialobAdminConfig): Record<string, string> => {
  const headers: Record<string, string> = {
    Accept: 'application/json',
    'Content-Type': 'application/json; charset=utf-8'
  };
  if (config.csrf) {
    headers[config.csrf.key] = config.csrf.value;
  }
  return headers;
};

const defaultFetchAuth = (
  url: string,
  init: RequestInit,
  config: DialobAdminConfig
): Promise<Response> => {
  if (!init.headers) {
    init.headers = getHeaders(config);
  }
  return fetch(url, {
    credentials: 'same-origin',
    ...init
  });
};

export const fetchAuth = (
  url: string,
  init: RequestInit,
  config: DialobAdminConfig
): Promise<Response> => {
  const fetchFn = config.fetchAuth || defaultFetchAuth;
  return fetchFn(url, init, config);
};