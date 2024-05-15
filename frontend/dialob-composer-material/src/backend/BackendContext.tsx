import { createContext, useEffect, useRef, useState } from "react";
import { ComposerState } from "../dialob";
import { BackendService } from "./BackendService";
import { ApiResponse, BackendState, TransportConfig } from "./types";


const INITIAL_BACKEND: BackendState = {
  formId: "",
  loaded: false,
  form: null,
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  saveForm: (form: ComposerState, dryRun?: boolean): Promise<ApiResponse> => {
    return Promise.resolve({ success: true });
  },
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  duplicateItem: (form: ComposerState, itemId: string): Promise<ApiResponse> => {
    return Promise.resolve({ success: true });
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
      saveForm: backendService.current.saveForm.bind(backendService.current),
      duplicateItem: backendService.current.duplicateItem.bind(backendService.current),
    }}>
      {children}
    </BackendContext.Provider>
  );
}
