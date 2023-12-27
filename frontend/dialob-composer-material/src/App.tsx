import { ComposerProvider, EditorProvider } from './dialob'
import multiPageForm from './dialob/test/multiPageForm.json';
import ComposerLayoutView from './views/layout/ComposerLayoutView';
import { IntlProvider } from 'react-intl';
import messages from './intl';

function App() {
  return (
    <ComposerProvider formData={multiPageForm}>
      <EditorProvider>
        <IntlProvider locale='en' messages={messages['en']}>
          <ComposerLayoutView />
        </IntlProvider>
      </EditorProvider>
    </ComposerProvider>
  )
}

export default App;
