import { PropsWithChildren, createContext, useEffect, useRef, useState } from "react";
import { ComposerState, ComposerTag, INIT_STATE } from "../types";
import { BackendService } from "./BackendService";
import { ApiResponse, BackendState, CreateTagRequest, DialobComposerConfig, PreviewSessionContext } from "./types";

/* eslint-disable @typescript-eslint/no-unused-vars */
const INITIAL_BACKEND: BackendState = {
  formId: "",
  loaded: false,
  form: null,
  config: { transport: { apiUrl: "", previewUrl: "" }, backendVersion: "0.0.0", closeHandler: () => { } },
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

export const BackendProvider: React.FC<PropsWithChildren<BackendProviderProps>> = ({ children, formId, config }) => {
  const [formData, setFormData] = useState<ComposerState | null>(null);
  const [loaded, setLoaded] = useState(false);
  const backendService = useRef(new BackendService(config));

  useEffect(() => {
    console.log("Loading form ", formId);
    backendService.current.loadForm(formId)
      .then(frm => {
        setFormData(frm);
        setLoaded(true);
      })
      .catch(err => console.log("ERR", err));
  }, [formId]);

  return (
    <BackendContext.Provider value={{
      formId,
      loaded,
      form: formData,
      config,
      loadForm: backendService.current.loadForm.bind(backendService.current),
      saveForm: backendService.current.saveForm.bind(backendService.current),
      createForm: backendService.current.createForm.bind(backendService.current),
      duplicateItem: backendService.current.duplicateItem.bind(backendService.current),
      createTag: backendService.current.createTag.bind(backendService.current),
      getTags: backendService.current.getTags.bind(backendService.current),
      changeItemId: backendService.current.changeItemId.bind(backendService.current),
      createPreviewSession: backendService.current.createPreviewSession.bind(backendService.current),
    }}>
      {children}
    </BackendContext.Provider>
  );
}
