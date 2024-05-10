import { ComposerState } from "../dialob";
import { EditorError } from "../editor";


export interface SaveResult {
  ok: boolean;
  id: string;
  rev: string;
  errors: EditorError[];
}

export interface SaveFormResponse {
  result?: SaveResult;
  success: boolean;
  apiError?: string;
}

export interface TransportConfig {
  csrf?: {
    headerName: string;
    token: string;
  }
  apiUrl: string;
  tenantId?: string;
  credentialMode?: RequestCredentials;
}

export interface DialobComposerConfig {
  transport: TransportConfig;
}

export interface AppConfig {
  formId: string;
  csrfHeader: string;
  csrf: string;
  backend_api_url: string;
  tenantId: string;
  credentialMode: RequestCredentials;
}

export interface BackendState {
  formId: string;
  loaded: boolean;
  form: ComposerState | null;
  saveForm(form: ComposerState): Promise<SaveFormResponse>;
}
