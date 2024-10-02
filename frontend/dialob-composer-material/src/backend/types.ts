import { ItemConfig, ItemTypeConfig } from "../defaults/types";
import { ComposerState, ComposerTag } from "../dialob";
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

export interface ChangeIdResult {
  ok: boolean;
  id: string;
  rev: string;
  form: ComposerState;
  errors: EditorError[];
}

export interface CreateTagResult {
  ok: boolean;
}

export interface CreateSessionResult {
  _id: string;
  _rev: string;
}

export interface ApiResponse {
  success: boolean;
  result?: SaveResult | DuplicateResult | CreateTagResult | ChangeIdResult | CreateSessionResult;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  apiError?: any;
}

export interface CreateTagRequest {
  name: string;
  description: string;
  formName: string;
  formId?: string;
}

export type PreviewSessionContext = {
  id: string;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  value: any;
}[];

export interface PreviewSessionData {
  metadata: {
    formId: string
    formRev: string;
    language: string;
  };
  context?: PreviewSessionContext;
}

export interface TransportConfig {
  csrf?: {
    headerName: string;
    token: string;
  }
  apiUrl: string;
  previewUrl: string;
  tenantId?: string;
  credentialMode?: RequestCredentials;
}

export interface DialobComposerConfig {
  transport: TransportConfig;
  documentationUrl?: string;
  itemEditors?: ItemConfig;
  itemTypes?: ItemTypeConfig;
  closeHandler: () => void;
}

export interface AppConfig {
  formId: string;
  csrfHeader: string;
  csrf: string;
  backend_api_url: string;
  filling_app_url: string;
  adminAppUrl: string;
  tenantId: string;
  credentialMode: RequestCredentials;
}

export interface BuildInfo {
  build: {
    artifact: string;
    name: string;
    version: string;
    group: string;
    time: string;
  }
}

export interface BackendState {
  formId: string;
  loaded: boolean;
  form: ComposerState | null;
  config: DialobComposerConfig;
  loadForm(formId: string, tagName?: string): Promise<ComposerState>;
  saveForm(form: ComposerState, dryRun?: boolean): Promise<ApiResponse>;
  duplicateItem(form: ComposerState, itemId: string): Promise<ApiResponse>;
  createTag(request: CreateTagRequest): Promise<ApiResponse>;
  getTags(formName: string): Promise<ComposerTag[]>;
  changeItemId(form: ComposerState, oldId: string, newId: string): Promise<ApiResponse>;
  createPreviewSession(formId: string, language: string, context?: PreviewSessionContext): Promise<ApiResponse>;
  getBuildInfo(): Promise<BuildInfo>;
}
