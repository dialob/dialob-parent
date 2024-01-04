import { ComposerProvider } from './dialob'
import { EditorProvider } from './editor';
import { IntlProvider } from 'react-intl';
import messages from './intl';
import multiPageForm from './dialob/test/multiPageForm.json';
import ComposerLayoutView from './views/layout/ComposerLayoutView';

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
