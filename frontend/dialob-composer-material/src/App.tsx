import React from 'react';
import { ComposerProvider } from './dialob'
import { useEditor } from './editor';
import { IntlProvider } from 'react-intl';
import messages from './intl';
import ComposerLayoutView from './views/ComposerLayoutView';
import { Dispatch } from 'react';
import { Middleware } from './dialob/react/ComposerContext';
import { ComposerAction } from './dialob/actions';
import { useBackend } from './backend/useBackend';
import { SaveResult } from './backend/types';
import { ProgressSplash } from './utils/LoadingUtils';
import { ComposerState } from './types';

function App() {
  const { form, loaded, saveForm } = useBackend();
  const { setErrors } = useEditor();

  async function saveFormMiddleware(action: ComposerAction | undefined, state: ComposerState, dispatch: Dispatch<ComposerAction>) {
    if (action && action.type !== 'setRevision' && state._tag === undefined) {
      if (action.type === 'setForm' && !action.save) {
        return;
      }
      saveForm(state)
        .then(saveResponse => {
          if (saveResponse.success && saveResponse.result) {
            const result = saveResponse.result as SaveResult;
            const errors = result.errors?.map(e => {
              if (e.itemId && e.itemId.includes(':')) {
                const itemId = e.itemId.split(':')[0];
                return { ...e, itemId: itemId };
              }
              return e;
            }); setErrors(errors);
            dispatch({ type: 'setRevision', revision: result.rev });
          } else if (saveResponse.apiError) {
            setErrors([{ level: 'FATAL', message: saveResponse.apiError.message }])
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
