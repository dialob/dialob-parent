import App from "../App";
import { BackendProvider } from "../backend/BackendContext";
import { EditorProvider } from "../editor";
import { DialobComposerConfig } from "../backend/types";

const DialobComposer: React.FC<{ config: DialobComposerConfig, formId: string }> = ({ config, formId }) => {
  return (
    <BackendProvider config={config} formId={formId}>
      <EditorProvider>
        <App />
      </EditorProvider>
    </BackendProvider>
  );
}

export default DialobComposer;
