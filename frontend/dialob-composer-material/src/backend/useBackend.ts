import { useContext } from "react";
import { BackendContext } from "./BackendContext";

export const useBackend = () => {
  const backendContext = useContext(BackendContext);
  return {
    form: backendContext.form,
    loaded: backendContext.loaded,
    saveForm: backendContext.saveForm,
    duplicateItem: backendContext.duplicateItem,
  }
}
