import React from 'react';
import { ComposerProvider, ComposerState } from './dialob'
import { useEditor } from './editor';
import { IntlProvider } from 'react-intl';
import messages from './intl';
import ComposerLayoutView from './views/ComposerLayoutView';
import { Dispatch } from 'react';
import { CircularProgress, Grid } from '@mui/material';
import { Middleware } from './dialob/react/ComposerContext';
import { ComposerAction } from './dialob/actions';
import { useBackend } from './backend/useBackend';

const ProgressSplash: React.FC = () => {
  return (
    <Grid
      container
      spacing={0}
      direction="column"
      alignItems="center"
      justifyContent="center"
      sx={{ minHeight: '100vh' }}
    >
      <Grid item xs={3}>
        <CircularProgress size={100} thickness={5} />
      </Grid>
    </Grid>
  )
}

function App() {
  const { form, loaded, saveForm } = useBackend();
  const { setErrors } = useEditor();

  async function saveFormMiddleware(action: ComposerAction | undefined, state: ComposerState, dispatch: Dispatch<ComposerAction>) {
    if (action !== undefined && action.type !== 'setRevision') {
      saveForm(state)
        .then(saveResponse => {
          if (saveResponse.success && saveResponse.result) {
            setErrors(saveResponse.result.errors);
            dispatch({ type: 'setRevision', revision: saveResponse.result?.rev });
          }
        });
    }
  }

  if (form === null || !loaded) {
    return (
      <ProgressSplash />
    );
  }

  const preMiddleware: Middleware[] = [];

  const postMiddleware: Middleware[] = [
    saveFormMiddleware,
  ];

  return (
    <ComposerProvider formData={form} preMiddleware={preMiddleware} postMiddleware={postMiddleware}>
      <IntlProvider locale='en' messages={messages['en']}>
        <ComposerLayoutView />
      </IntlProvider>
    </ComposerProvider>
  )
}

export default App;
