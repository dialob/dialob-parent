import { useContext } from "react";
import { BackendContext } from "./BackendContext";

export const useBackend = () => {
  const backendContext = useContext(BackendContext);
  return {
    form: backendContext.form,
    saveForm: backendContext.saveForm,
    loaded: backendContext.loaded,
  }
}
