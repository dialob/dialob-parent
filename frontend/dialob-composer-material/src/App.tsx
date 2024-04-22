import { ComposerProvider, ComposerState } from './dialob'
import { EditorProvider } from './editor';
import { IntlProvider } from 'react-intl';
import messages from './intl';
import ComposerLayoutView from './views/ComposerLayoutView';
import { Dispatch, useCallback, useContext } from 'react';
import { BackendContext } from './backend/BackendContext';
import { CircularProgress, Grid } from '@mui/material';
import { Middleware } from './dialob/react/ComposerContext';
import { ComposerAction } from './dialob/actions';

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
  const backendContext = useContext(BackendContext);

  /*
  async function saveForm(form: ComposerState): Promise<string> {
    console.log("Update trigger");
    try {
      const saveResult = await backendContext.saveForm(form);
      console.log("save result", saveResult);
      return Promise.resolve(saveResult)
    } catch (error) {
      console.error("Save error", error);
    }
  }
  */

  async function saveFormMiddleware(action: ComposerAction | undefined, state: ComposerState, dispatch: Dispatch<ComposerAction>) {
    if (action !== undefined && action.type !== 'setRevision') {
      console.log('OLD REV', state._rev);
      console.log('Action', action);
      backendContext.saveForm(state)
        .then(saveResult => {
          if (saveResult.success && saveResult.result)  {
            // backendContext.setErrors();          
            console.log('NEW REV', saveResult.result.rev);
            dispatch({ type: 'setRevision', revision: saveResult.result?.rev });
          }  
        });
    }
  }


  if (backendContext.form === null || !backendContext.loaded) {
    return (
      <ProgressSplash />
    );
  }

  const preMiddleware: Middleware[] = [];

  const postMiddleware: Middleware[] = [
   saveFormMiddleware // Remove this to disable saving
  ];

  return (
    <ComposerProvider formData={backendContext.form} preMiddleware={preMiddleware} postMiddleware={postMiddleware}>
      <EditorProvider>
        <IntlProvider locale='en' messages={messages['en']}>
          <ComposerLayoutView />
        </IntlProvider>
      </EditorProvider>
    </ComposerProvider>
  )

  /*
  return (
    <ComposerProvider formData={backendContext.form} callbacks={{onSave: saveForm}}>
      <EditorProvider>
        <IntlProvider locale='en' messages={messages['en']}>
          <ComposerLayoutView />
        </IntlProvider>
      </EditorProvider>
    </ComposerProvider>
  )
  */
}

export default App;
