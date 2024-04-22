import { ComposerState } from "../dialob";

export interface TransportConfig {
  csrf?: {
    headerName: string;
    token: string;
  };
  apiUrl: string;
  tenantId?: string;
  credentialMode?: RequestCredentials;
}

export interface ApiSaveResult {
  ok: boolean;
  id: string;
  rev: string;
  errors: any; // TODO type
}

/* 
  "errors": [
        {
            "itemId": "group8",
            "message": "SYNTAX_ERROR",
            "level": "ERROR",
            "type": "VISIBILITY",
            "startIndex": 1,
            "endIndex": 1
        },
        {
            "itemId": "group8",
            "message": "SYNTAX_ERROR",
            "level": "ERROR",
            "type": "VISIBILITY",
            "startIndex": 2,
            "endIndex": 2
        }
    ]
*/

export interface SaveResult {
  result?: ApiSaveResult;
  success: boolean;
  apiError?: string
}

// TODO: This service needs rewrite when things start to work...

export class BackendService {
  private config: TransportConfig;
  private isSaving: boolean;

  constructor(config: TransportConfig) {
    this.config = config;
    this.isSaving = false;
  }

  private prepareFormsUrl(formId: string, formTag: string | undefined): string {
    const baseUrl = new URL(`${this.config.apiUrl}/forms/${formId}`);
    const params: Record<string, any> = new URLSearchParams();
  
    if (!!formTag) {
      params.append('rev', formTag);
    }
    
    if (!!this.config.tenantId) {
      params.append('tenantId', this.config.tenantId);
    }
  
    baseUrl.search = params.toString();
    
    return baseUrl.toString();
  }

  private async storeForm(formData: ComposerState): Promise<ApiSaveResult> {
    const headers: Record<string, string> = {
      'Accept': 'application/json',
      'Content-Type': 'application/json'
    };
    if (this.config.csrf) {
      headers[this.config.csrf.headerName] = this.config.csrf.token;
    }

    let options: RequestInit = {
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

  public async saveForm(form: ComposerState): Promise<SaveResult> {
    if (this.isSaving) {
      console.log('DEFER SAVE');
      return {
        result: undefined,
        success: true
      }
    } 

    this.isSaving  = true;

    try {
      const res = await this.storeForm(form);
      this.isSaving = false;
      return {
        result: res,
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

  public async loadForm(formId: string): Promise<ComposerState> {
    const headers: Record<string, string> = {
      'Accept': 'application/json',
      'Content-Type': 'application/json'
    };
    if (this.config.csrf) {
      headers[this.config.csrf.headerName] = this.config.csrf.token;
    }
  
    let options: RequestInit = {
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