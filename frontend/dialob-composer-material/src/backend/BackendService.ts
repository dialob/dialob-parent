import { ComposerState } from "../dialob";
import { SaveResult, SaveFormResponse, TransportConfig } from "./types";


export class BackendService {
  private config: TransportConfig;
  private isSaving: boolean;

  constructor(config: TransportConfig) {
    this.config = config;
    this.isSaving = false;
  }

  private prepareFormsUrl(formId: string, formTag: string | undefined): string {
    const baseUrl = new URL(`${this.config.apiUrl}/forms/${formId}`);
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const params: Record<string, any> = new URLSearchParams();

    // eslint-disable-next-line no-extra-boolean-cast
    if (!!formTag) {
      params.append('rev', formTag);
    }

    // eslint-disable-next-line no-extra-boolean-cast
    if (!!this.config.tenantId) {
      params.append('tenantId', this.config.tenantId);
    }

    baseUrl.search = params.toString();

    return baseUrl.toString();
  }

  private async storeForm(formData: ComposerState): Promise<SaveResult> {
    const headers: Record<string, string> = {
      'Accept': 'application/json',
      'Content-Type': 'application/json'
    };
    if (this.config.csrf) {
      headers[this.config.csrf.headerName] = this.config.csrf.token;
    }

    const options: RequestInit = {
      method: 'PUT',
      credentials: this.config.credentialMode,
      headers,
      body: JSON.stringify(formData)
    }

    const response = await fetch(this.prepareFormsUrl(formData._id, undefined), options);

    if (!response.ok) {
      console.error("Form store error", response.status);
      throw new Error(`${response.status}`);
    }

    return await response.json();
  }

  public async saveForm(form: ComposerState): Promise<SaveFormResponse> {
    if (this.isSaving) {
      console.log('DEFER SAVE');
      return {
        result: undefined,
        success: true
      }
    }

    this.isSaving = true;

    try {
      const res = await this.storeForm(form);
      this.isSaving = false;
      return {
        result: res,
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
    const headers: Record<string, string> = {
      'Accept': 'application/json',
      'Content-Type': 'application/json'
    };
    if (this.config.csrf) {
      headers[this.config.csrf.headerName] = this.config.csrf.token;
    }

    const options: RequestInit = {
      method: 'GET',
      credentials: this.config.credentialMode,
      headers
    }

    const response = await fetch(this.prepareFormsUrl(formId, undefined), options);

    if (!response.ok) {
      console.error("Form fetch error", response.status);
      throw new Error(`${response.status}`);
    }

    return await response.json();
  }
}
