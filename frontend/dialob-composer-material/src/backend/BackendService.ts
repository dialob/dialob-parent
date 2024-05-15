import { ComposerState } from "../dialob";
import { SaveResult, TransportConfig, DuplicateResult, ApiResponse } from "./types";

interface UrlParams {
  formTag?: string;
  itemId?: string;
  dryRun?: boolean;
}

export class BackendService {
  private config: TransportConfig;
  private isSaving: boolean;

  constructor(config: TransportConfig) {
    this.config = config;
    this.isSaving = false;
  }

  private prepareFormsUrl(url: string, urlParams?: UrlParams): string {
    const { formTag, itemId, dryRun } = urlParams || {};
    const baseUrl = new URL(this.config.apiUrl + url);
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const params: Record<string, any> = new URLSearchParams();

    if (formTag) {
      params.append('rev', formTag);
    }

    if (itemId) {
      params.append('itemId', itemId);
    }

    if (this.config.tenantId) {
      params.append('tenantId', this.config.tenantId);
    }

    if (dryRun) {
      params.append('dryRun', 'true');
    }

    baseUrl.search = params.toString();

    return baseUrl.toString();
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  private async doFetch(url: string, method: string, body?: ComposerState): Promise<any> {
    const headers: Record<string, string> = {
      'Accept': 'application/json',
      'Content-Type': 'application/json'
    };
    if (this.config.csrf) {
      headers[this.config.csrf.headerName] = this.config.csrf.token;
    }

    const options: RequestInit = {
      method,
      credentials: this.config.credentialMode,
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
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
    } catch (err: any) {
      this.isSaving = false;
      return {
        success: false,
        apiError: err
      }
    }
  }

  public async loadForm(formId: string): Promise<ComposerState> {
    return await this.doFetch(this.prepareFormsUrl(`/forms/${formId}`), 'GET');
  }

  public async duplicateItem(form: ComposerState, itemId: string): Promise<ApiResponse> {
    try {
      const res = await this.doFetch(this.prepareFormsUrl(`/forms/actions/itemCopy`, { itemId }), 'POST', form);
      return {
        result: res as DuplicateResult,
        success: true
      }
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
    } catch (err: any) {
      return {
        success: false,
        apiError: err
      }
    }
  }
}
