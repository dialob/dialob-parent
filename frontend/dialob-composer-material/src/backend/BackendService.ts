import { ComposerState, ComposerTag } from "../types";
import {
  SaveResult, DuplicateResult, ApiResponse, CreateTagRequest, CreateTagResult, ChangeIdResult, PreviewSessionData,
  PreviewSessionContext, CreateSessionResult, DialobComposerConfig,
} from "./types";

interface UrlParams {
  formTag?: string;
  itemId?: string;
  dryRun?: boolean;
  snapshot?: boolean;
  oldId?: string;
  newId?: string;
}

/* eslint-disable @typescript-eslint/no-explicit-any */
export class BackendService {
  private config: DialobComposerConfig;
  private isSaving: boolean;

  constructor(config: DialobComposerConfig) {
    this.config = config;
    this.isSaving = false;
  }

  private prepareFormsUrl(url: string, urlParams?: UrlParams): string {
    const { formTag, itemId, dryRun, snapshot, oldId, newId } = urlParams || {};
    const baseUrl = new URL(this.config.transport.apiUrl + url);
    const params: Record<string, any> = new URLSearchParams();

    if (formTag && formTag !== 'LATEST') {
      params.append('rev', formTag);
    }
    if (itemId) {
      params.append('itemId', itemId);
    }
    if (this.config.transport.tenantId) {
      params.append('tenantId', this.config.transport.tenantId);
    }
    if (dryRun) {
      params.append('dryRun', 'true');
    }
    if (snapshot) {
      params.append('snapshot', 'true');
    }
    if (oldId) {
      params.append('oldId', oldId);
    }
    if (newId) {
      params.append('newId', newId);
    }

    baseUrl.search = params.toString();
    return baseUrl.toString();
  }

  private async doFetch(url: string, method: string, body?: any): Promise<any> {
    const headers: Record<string, string> = {
      'Accept': 'application/json',
      'Content-Type': 'application/json'
    };
    if (this.config.transport.csrf) {
      headers[this.config.transport.csrf.headerName] = this.config.transport.csrf.token;
    }

    const options: RequestInit = {
      method,
      credentials: this.config.transport.credentialMode,
      headers
    }

    if (body) {
      options.body = JSON.stringify(body);
    }

    const response = await fetch(url, options);

    if (!response.ok) {
      console.error("Backend service error", response.status);
      throw new Error(`${response.status}`);
    }

    return await response.json();
  }

  public async saveForm(form: ComposerState, dryRun?: boolean): Promise<ApiResponse> {
    if (this.isSaving) {
      console.log('DEFER SAVE');
      return {
        result: undefined,
        success: true
      }
    }

    this.isSaving = true;

    try {
      const res = await this.doFetch(this.prepareFormsUrl(`/forms/${form._id}`, { dryRun }), 'PUT', form);
      this.isSaving = false;
      return {
        result: res as SaveResult,
        success: true
      }
    } catch (err: any) {
      this.isSaving = false;
      return {
        success: false,
        apiError: err
      }
    }
  }

  public async createForm(form: ComposerState): Promise<ApiResponse> {
    if (this.isSaving) {
      console.log('DEFER SAVE');
      return {
        result: undefined,
        success: true
      }
    }

    this.isSaving = true;

    try {
      const res = await this.doFetch(this.prepareFormsUrl('/forms'), 'POST', form);
      this.isSaving = false;
      return {
        result: res as SaveResult,
        success: true
      }
    } catch (err: any) {
      this.isSaving = false;
      return {
        success: false,
        apiError: err
      }
    }
  }

  public async loadForm(formId: string, tagName?: string): Promise<ComposerState> {
    return await this.doFetch(this.prepareFormsUrl(`/forms/${formId}`, { formTag: tagName }), 'GET');
  }

  public async duplicateItem(form: ComposerState, itemId: string): Promise<ApiResponse> {
    try {
      const res = await this.doFetch(this.prepareFormsUrl(`/forms/actions/itemCopy`, { itemId }), 'POST', form);
      return {
        result: res as DuplicateResult,
        success: true
      }
    } catch (err: any) {
      return {
        success: false,
        apiError: err
      }
    }
  }

  public async createTag(request: CreateTagRequest): Promise<ApiResponse> {
    const { formName, formId } = request;
    try {
      const res = await this.doFetch(
        this.prepareFormsUrl(`/forms/${formName}/tags`, { snapshot: formId === undefined }),
        'POST',
        request
      );
      return {
        result: res as CreateTagResult,
        success: true
      }
    } catch (err: any) {
      return {
        success: false,
        apiError: err
      }
    }
  }

  public async getTags(formName: string): Promise<ComposerTag[]> {
    return await this.doFetch(this.prepareFormsUrl(`/forms/${formName}/tags`), 'GET');
  }

  public async changeItemId(form: ComposerState, oldId: string, newId: string): Promise<ApiResponse> {
    try {
      const res = await this.doFetch(this.prepareFormsUrl(`/forms/${form._id}`, { oldId, newId }), 'PUT', form);
      return {
        result: res as ChangeIdResult,
        success: true
      }
    } catch (err: any) {
      return {
        success: false,
        apiError: err
      }
    }
  }

  public async createPreviewSession(formId: string, language: string, context?: PreviewSessionContext): Promise<ApiResponse> {
    const session: PreviewSessionData = {
      metadata: {
        formId,
        formRev: 'LATEST',
        language
      }
    };
    if (context) {
      session.context = context;
    }

    try {
      const res = await this.doFetch(this.prepareFormsUrl('/questionnaires'), 'POST', session);
      return {
        result: res as CreateSessionResult,
        success: true
      }
    } catch (err: any) {
      return {
        success: false,
        apiError: err
      }
    }
  }
}
