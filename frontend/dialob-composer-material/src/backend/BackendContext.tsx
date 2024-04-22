import { createContext, useContext, useEffect, useRef, useState } from "react";
import { ComposerState } from "../dialob";
import { BackendService, SaveResult, TransportConfig } from "./BackendService";

export interface DialobComposerConfig {
  transport: TransportConfig;
}

interface BackendState {
  formId: string;
  loaded: boolean;
  form: ComposerState | null;
  saveForm(form: ComposerState): Promise<SaveResult>;
}

const INITIAL_BACKEND: BackendState = {
  formId: "",
  loaded: false,
  form: null,
  saveForm: (form: ComposerState): Promise<SaveResult> => {
    return Promise.resolve({success: true});
  }
};

export const BackendContext = createContext<BackendState>(INITIAL_BACKEND);

export interface BackendProviderProps {
  children: React.ReactNode;
  formId: string;
  config: TransportConfig;
}

export const BackendProvider: React.FC<BackendProviderProps> = ({children, formId, config}) => {
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
    <BackendContext.Provider value={{form: formData, loaded, formId, saveForm: backendService.current.saveForm.bind(backendService.current)}}>
      {children}
    </BackendContext.Provider>
  );
}

export const useBackend = () => {
  const backendContext = useContext(BackendContext);
  // ....
  return {
    form: backendContext.form,
    saveForm: backendContext.saveForm
  }
}
