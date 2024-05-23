import { useContext } from "react";
import { BackendContext } from "./BackendContext";

export const useBackend = () => {
  const backendContext = useContext(BackendContext);
  return {
    formId: backendContext.formId,
    loaded: backendContext.loaded,
    form: backendContext.form,
    config: backendContext.config,
    loadForm: backendContext.loadForm,
    saveForm: backendContext.saveForm,
    duplicateItem: backendContext.duplicateItem,
    createTag: backendContext.createTag,
    getTags: backendContext.getTags,
    changeItemId: backendContext.changeItemId,
    createPreviewSession: backendContext.createPreviewSession,
    getBuildInfo: backendContext.getBuildInfo,
  }
}
