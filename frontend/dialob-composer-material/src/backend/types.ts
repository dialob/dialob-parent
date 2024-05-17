import { ComposerState } from "../dialob";
import { EditorError } from "../editor";


export interface SaveResult {
  ok: boolean;
  id: string;
  rev: string;
  errors: EditorError[];
}

export interface DuplicateResult {
  ok: boolean;
  id: string;
  rev: string;
  form: ComposerState;
}

export interface CreateTagResult {
  ok: boolean;
}

export interface ApiResponse {
  success: boolean;
  result?: SaveResult | DuplicateResult | CreateTagResult;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  apiError?: any;
}

export interface CreateTagRequest {
  name: string;
  description: string;
  formName: string;
  formId?: string;
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
  saveForm(form: ComposerState, dryRun?: boolean): Promise<ApiResponse>;
  duplicateItem(form: ComposerState, itemId: string): Promise<ApiResponse>;
  createTag(request: CreateTagRequest): Promise<ApiResponse>;
}
