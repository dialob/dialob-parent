import { DialobAdminConfig } from "..";

export const getHeaders = (config: DialobAdminConfig) => {
  let headers: any = {
    Accept: 'application/json',
    'Content-Type': 'application/json; charset=utf-8'
  };
  if (config.csrf) {
    headers[config.csrf.key] = config.csrf.value;
  }
  return headers;
}

export const fetchAuth = (input: string, init: any, config: DialobAdminConfig) => {
  if (!init.headers) {
    init['headers'] = getHeaders(config);
  }
  return fetch(input, {
    credentials: 'same-origin',
    ...init
  })
}

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
  let url = `${config.dialobApiUrl}/api/forms/${formId}/tags`;
  const response = await fetchAuth(url, {
    method: 'GET'
  }, config);
  return response;
}

export const getAdminFormConfiguration = async (formId: string, config: DialobAdminConfig) => {
  let url = `${config.dialobApiUrl}/api/forms/${formId}`;
  const response = await fetchAuth(url, {
    method: 'GET'
  }, config);
  return response;
}

export const addAdminFormConfiguration = async (form: any, config: DialobAdminConfig) => {
  let url = `${config.dialobApiUrl}/api/forms`;
  const response = await fetchAuth(url, {
    method: 'POST',
    body: JSON.stringify(form)
  }, config);
  return response;
}

export const addAdminFormConfigurationFromCsv = async (csvData: string, config: DialobAdminConfig) => {
  let url = `${config.dialobApiUrl}/api/forms`;
  const response = await fetchAuth(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'text/csv',
    },
    body: csvData,
  }, config);
  return response;
};

export const editAdminFormConfiguration = async (form: any, config: DialobAdminConfig) => {
  let url = `${config.dialobApiUrl}/api/forms/${form.name}?force=true`;
  const response = await fetchAuth(url, {
    method: 'PUT',
    body: JSON.stringify(form)
  }, config);
  return response;
}

export const deleteAdminFormConfiguration = async (formId: string, config: DialobAdminConfig) => {
  let url = `${config.dialobApiUrl}/api/forms/${formId}`;
  const response = await fetchAuth(url, {
    method: 'DELETE'
  }, config);
  return response;
}
