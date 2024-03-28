import { ComposerProvider, ComposerState } from './dialob'
import { EditorProvider } from './editor';
import { IntlProvider } from 'react-intl';
import messages from './intl';
import form from './dialob/test/multiPageForm.json';
import ComposerLayoutView from './views/ComposerLayoutView';

function App() {
  return (
    <ComposerProvider formData={form as ComposerState}>
      <EditorProvider>
        <IntlProvider locale='en' messages={messages['en']}>
          <ComposerLayoutView />
        </IntlProvider>
      </EditorProvider>
    </ComposerProvider>
  )
}

export default App;
