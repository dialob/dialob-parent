import type { DialobAdminConfig } from '../types';

export const buildFormListUrl = (config: DialobAdminConfig) =>
  config.tenantId
    ? `${config.dialobApiUrl}/api/forms?tenantId=${config.tenantId}`
    : `${config.dialobApiUrl}/api/forms`;

export const buildFormTagsUrl = (formId: string, config: DialobAdminConfig) =>
  config.tenantId
    ? `${config.dialobApiUrl}/api/forms/${formId}/tags?tenantId=${config.tenantId}`
    : `${config.dialobApiUrl}/api/forms/${formId}/tags`;

export const buildAllTagsUrl = (config: DialobAdminConfig) =>
  config.tenantId
    ? `${config.dialobApiUrl}/api/tags?tenantId=${config.tenantId}`
    : `${config.dialobApiUrl}/api/tags`;

export const buildSingleFormUrl = (formId: string, config: DialobAdminConfig) =>
  config.tenantId
    ? `${config.dialobApiUrl}/api/forms/${formId}?tenantId=${config.tenantId}`
    : `${config.dialobApiUrl}/api/forms/${formId}`;

export const buildAddFormUrl = (config: DialobAdminConfig) =>
  config.tenantId
    ? `${config.dialobApiUrl}/api/forms?tenantId=${config.tenantId}`
    : `${config.dialobApiUrl}/api/forms`;

export const buildEditFormUrl = (formName: string, config: DialobAdminConfig) =>
  config.tenantId
    ? `${config.dialobApiUrl}/api/forms/${formName}?force=true&tenantId=${config.tenantId}`
    : `${config.dialobApiUrl}/api/forms/${formName}?force=true`;

export const buildDeleteFormUrl = (formId: string, config: DialobAdminConfig) =>
  config.tenantId
    ? `${config.dialobApiUrl}/api/forms/${formId}?tenantId=${config.tenantId}`
    : `${config.dialobApiUrl}/api/forms/${formId}`;