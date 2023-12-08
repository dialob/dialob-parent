import { Container, Typography } from '@mui/material'
import { ComposerProvider } from './dialob'
import { DebugFormView } from './views/DebugFormView';

// TODO: Use built-in test form here
import testForm from './dialob/test/testForm.json';

function App() {
 
  return (
    <ComposerProvider formData={testForm}>
      <Container>
        <Typography variant='h4'>Dialob Composer</Typography>
        <DebugFormView />
      </Container>
    </ComposerProvider>
  )
}

export default App
