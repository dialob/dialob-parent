import { PropsWithChildren, useEffect, useRef, useState } from "react";
import { ComposerState } from "../types";
import { BackendService } from "./BackendService";
import { BackendContext, BackendProviderProps } from "./BackendContext";

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
