import { createContext, useEffect, useRef, useState } from "react";
import { ComposerState, ComposerTag, INIT_STATE } from "../dialob";
import { BackendService } from "./BackendService";
import { ApiResponse, BackendState, CreateTagRequest, TransportConfig } from "./types";

/* eslint-disable @typescript-eslint/no-unused-vars */
const INITIAL_BACKEND: BackendState = {
  formId: "",
  loaded: false,
  form: null,
  loadForm: (_formId: string, _tagName?: string): Promise<ComposerState> => {
    return Promise.resolve(INIT_STATE);
  },
  saveForm: (_form: ComposerState, _dryRun?: boolean): Promise<ApiResponse> => {
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
  }
};

export const BackendContext = createContext<BackendState>(INITIAL_BACKEND);

export interface BackendProviderProps {
  children: React.ReactNode;
  formId: string;
  config: TransportConfig;
}

export const BackendProvider: React.FC<BackendProviderProps> = ({ children, formId, config }) => {
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
      form: formData,
      loaded,
      formId,
      loadForm: backendService.current.loadForm.bind(backendService.current),
      saveForm: backendService.current.saveForm.bind(backendService.current),
      duplicateItem: backendService.current.duplicateItem.bind(backendService.current),
      createTag: backendService.current.createTag.bind(backendService.current),
      getTags: backendService.current.getTags.bind(backendService.current),
    }}>
      {children}
    </BackendContext.Provider>
  );
}
