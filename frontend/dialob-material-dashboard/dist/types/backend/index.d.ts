import { DialobAdminConfig } from "..";
export declare const getHeaders: (config: DialobAdminConfig) => any;
export declare const fetchAuth: (input: string, init: any, config: DialobAdminConfig) => Promise<Response>;
export declare const getAdminFormConfigurationList: (config: DialobAdminConfig) => Promise<Response>;
export declare const getAdminFormConfigurationTags: (config: DialobAdminConfig, formId: string) => Promise<Response>;
export declare const getAdminFormConfiguration: (formId: string, config: DialobAdminConfig) => Promise<Response>;
export declare const addAdminFormConfiguration: (form: any, config: DialobAdminConfig) => Promise<Response>;
export declare const editAdminFormConfiguration: (form: any, config: DialobAdminConfig) => Promise<Response>;
export declare const deleteAdminFormConfiguration: (formId: string, config: DialobAdminConfig) => Promise<Response>;
