import { ItemConfig, ItemTypeConfig } from "../defaults/types";
import { ComposerState, ComposerTag } from "../types";
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
  result?: SaveResult | DuplicateResult | CreateTagResult | ChangeIdResult | CreateSessionResult | TranslationResponse;
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
  itemEditors: ItemConfig;
  itemTypes: ItemTypeConfig;
  backendVersion: string;
  closeHandler: () => void;
  translationServiceUrl?: string;
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
  version: string;
  translationServiceUrl?: string;
}

// Translation API types
export interface TranslationEntry {
  id: string;
  text: string;
}

export interface TranslationRequest {
  sourceLanguage: string;
  targetLanguage: string;
  entries: TranslationEntry[];
}

export interface TranslationResult {
  id: string;
  translatedText: string;
}

export interface TranslationResponse {
  translations: TranslationResult[];
  targetLanguage: string;
}

export interface BackendState {
  formId: string;
  loaded: boolean;
  form: ComposerState | null;
  config: DialobComposerConfig;
  loadForm(formId: string, tagName?: string): Promise<ComposerState>;
  saveForm(form: ComposerState, dryRun?: boolean): Promise<ApiResponse>;
  createForm(form: ComposerState): Promise<ApiResponse>;
  duplicateItem(form: ComposerState, itemId: string): Promise<ApiResponse>;
  createTag(request: CreateTagRequest): Promise<ApiResponse>;
  getTags(formName: string): Promise<ComposerTag[]>;
  changeItemId(form: ComposerState, oldId: string, newId: string): Promise<ApiResponse>;
  createPreviewSession(formId: string, language: string, context?: PreviewSessionContext): Promise<ApiResponse>;
  translateEntries(request: TranslationRequest): Promise<ApiResponse>;
}
