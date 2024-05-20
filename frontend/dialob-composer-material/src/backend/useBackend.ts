import { useContext } from "react";
import { BackendContext } from "./BackendContext";

export const useBackend = () => {
  const backendContext = useContext(BackendContext);
  return {
    form: backendContext.form,
    loaded: backendContext.loaded,
    loadForm: backendContext.loadForm,
    saveForm: backendContext.saveForm,
    duplicateItem: backendContext.duplicateItem,
    createTag: backendContext.createTag,
    getTags: backendContext.getTags,
    changeItemId: backendContext.changeItemId,
  }
}
