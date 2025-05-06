import type { DialobAdminConfig } from '../types';
import { fetchAuth, getHeaders } from '../util';

export const getAdminFormConfigurationList = async (config: DialobAdminConfig) => {
  const baseUrl = `${config.dialobApiUrl}/api/forms`;
  const { tenantId } = config;
  const url = tenantId ? `${baseUrl}?tenantId=${tenantId}` : baseUrl;
  const response = await fetchAuth(url, {
    method: 'GET'
  }, config);
  return response;
}

export const getAdminFormConfigurationTags = async (config: DialobAdminConfig, formId: string) => {
  const baseUrl = `${config.dialobApiUrl}/api/forms/${formId}/tags`;
  const { tenantId } = config;
  const url = tenantId ? `${baseUrl}?tenantId=${tenantId}` : baseUrl;
  const response = await fetchAuth(url, {
    method: 'GET'
  }, config);
  return response;
}

export const getAdminFormAllTags = async (config: DialobAdminConfig) => {
  const baseUrl = `${config.dialobApiUrl}/api/tags`;
  const { tenantId } = config;
  const url = tenantId ? `${baseUrl}?tenantId=${tenantId}` : baseUrl;
  const response = await fetchAuth(url, {
    method: 'GET'
  }, config);
  return response;
}

export const getAdminFormConfiguration = async (formId: string, config: DialobAdminConfig) => {
  const baseUrl = `${config.dialobApiUrl}/api/forms/${formId}`;
  const { tenantId } = config;
  const url = tenantId ? `${baseUrl}?tenantId=${tenantId}` : baseUrl;
  const response = await fetchAuth(url, {
    method: 'GET'
  }, config);
  return response;
}

export const addAdminFormConfiguration = async (form: any, config: DialobAdminConfig) => {
  const baseUrl = `${config.dialobApiUrl}/api/forms`;
  const { tenantId } = config;
  const url = tenantId ? `${baseUrl}?tenantId=${tenantId}` : baseUrl;
  const response = await fetchAuth(url, {
    method: 'POST',
    body: JSON.stringify(form)
  }, config);
  return response;
}

export const addAdminFormConfigurationFromCsv = async (csvData: string, config: DialobAdminConfig) => {
  const baseUrl = `${config.dialobApiUrl}/api/forms`;
  const { tenantId } = config;
  const url = tenantId ? `${baseUrl}?tenantId=${tenantId}` : baseUrl;

  const defaultHeaders = getHeaders(config);

  const response = await fetchAuth(url, {
    method: 'POST',
    headers: {
      ...defaultHeaders,
      'Content-Type': 'text/csv',
    },
    body: csvData,
  }, config);
  return response;
};

export const editAdminFormConfiguration = async (form: any, config: DialobAdminConfig) => {
  const baseUrl = `${config.dialobApiUrl}/api/forms/${form.name}?force=true`;
  const { tenantId } = config;
  const url = tenantId ? `${baseUrl}?tenantId=${tenantId}` : baseUrl;
  const response = await fetchAuth(url, {
    method: 'PUT',
    body: JSON.stringify(form)
  }, config);
  return response;
}

export const deleteAdminFormConfiguration = async (formId: string, config: DialobAdminConfig) => {
  const baseUrl = `${config.dialobApiUrl}/api/forms/${formId}`;
  const { tenantId } = config;
  const url = tenantId ? `${baseUrl}?tenantId=${tenantId}` : baseUrl;
  const response = await fetchAuth(url, {
    method: 'DELETE'
  }, config);
  return response;
}
