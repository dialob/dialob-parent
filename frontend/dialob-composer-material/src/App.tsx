import { Box } from '@mui/material'
import { ComposerProvider } from './dialob'
import DebugFormView from './views/DebugFormView';
import NavigationTreeView from './views/tree/NavigationTreeView';
import testForm from './dialob/test/testForm.json';

function App() {
  return (
    <ComposerProvider formData={testForm}>
      <Box sx={{ display: 'flex', p: 2 }}>
        <Box sx={{ mt: 2 }}>
          <NavigationTreeView />
        </Box>
        <DebugFormView />
      </Box>
    </ComposerProvider>
  )
}

export default App;
