import { Container, Typography } from '@mui/material'
import { ComposerProvider } from './dialob'
import { DebugFormView } from './views/DebugFormView';

// TODO: Use built-in test form here
import testForm from './dialob/test/testForm.json';
import ComposerLayoutView from './views/ComposerLayoutView';
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

export default App
