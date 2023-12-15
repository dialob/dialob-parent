import { ComposerProvider } from './dialob'
import testForm from './dialob/test/testForm.json';
import ComposerLayoutView from './views/layout/ComposerLayoutView';
import { IntlProvider } from 'react-intl';
import messages from './intl';

function App() {
  return (
    <ComposerProvider formData={testForm}>
      <IntlProvider locale='en' messages={messages['en']}>
        <ComposerLayoutView />
      </IntlProvider>
    </ComposerProvider>
  )
}

export default App;
