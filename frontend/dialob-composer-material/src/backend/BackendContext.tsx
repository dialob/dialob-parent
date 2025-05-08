import { createContext } from "react";
import { ComposerState, ComposerTag, INIT_STATE } from "../types";
import { ApiResponse, BackendState, CreateTagRequest, DialobComposerConfig, PreviewSessionContext } from "./types";

/* eslint-disable @typescript-eslint/no-unused-vars */
const INITIAL_BACKEND: BackendState = {
  formId: "",
  loaded: false,
  form: null,
  config: {
    transport: {
      apiUrl: "",
      previewUrl: ""
    },
    backendVersion: "0.0.0",
    closeHandler: () => { },
    itemEditors: {
      defaultIcon: () => null, // Provide a default functional component
      items: []
    },
    itemTypes: {
      categories: []
    }
  },
  loadForm: (_formId: string, _tagName?: string): Promise<ComposerState> => {
    return Promise.resolve(INIT_STATE);
  },
  saveForm: (_form: ComposerState, _dryRun?: boolean): Promise<ApiResponse> => {
    return Promise.resolve({ success: true });
  },
  createForm: (_form: ComposerState): Promise<ApiResponse> => {
    return Promise.resolve({ success: true });
  },
  duplicateItem: (_form: ComposerState, _itemId: string): Promise<ApiResponse> => {
    return Promise.resolve({ success: true });
  },
  createTag: (_request: CreateTagRequest): Promise<ApiResponse> => {
    return Promise.resolve({ success: true });
  },
  getTags: (_formName: string): Promise<ComposerTag[]> => {
    return Promise.resolve([]);
  },
  changeItemId: (_form: ComposerState, _oldId: string, _newId: string): Promise<ApiResponse> => {
    return Promise.resolve({ success: true });
  },
  createPreviewSession: (_formId: string, _language: string, _context?: PreviewSessionContext): Promise<ApiResponse> => {
    return Promise.resolve({ success: true });
  },
};

export const BackendContext = createContext<BackendState>(INITIAL_BACKEND);

export interface BackendProviderProps {
  formId: string;
  config: DialobComposerConfig;
}

