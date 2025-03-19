import React from 'react';
import { Drawer, Box } from '@mui/material';
import MenuBar from './layout/MenuBar';
import NavigationPane from './layout/NavigationPane';
import EditorArea from './layout/EditorArea';
import ErrorPane from './layout/ErrorPane';
import { useEditor } from '../editor';
import ConfirmationDialog from '../dialogs/ConfirmationDialog';
import { MENU_HEIGHT, SCROLL_SX } from '../theme/siteTheme';
import ItemOptionsDialog from '../dialogs/ItemOptionsDialog';
import { useBackend } from '../backend/useBackend';
import { useComposer } from '../dialob';
import { SaveResult } from '../backend/types';
import { ProgressSplash } from '../App';

const ComposerLayoutView: React.FC = () => {
  const { form } = useComposer();
  const { editor, setErrors } = useEditor();
  const { saveForm } = useBackend();
  const hasErrors = editor.errors?.length > 0;
  const [loading, setLoading] = React.useState(true);

  React.useEffect(() => {
    saveForm(form, true)
      .then(saveResponse => {
        if (saveResponse.success && saveResponse.result) {
          const result = saveResponse.result as SaveResult;
          const errors = result.errors?.map(e => {
            if (e.itemId && e.itemId.includes(':')) {
              const itemId = e.itemId.split(':')[0];
              return { ...e, itemId: itemId };
            }
            return e;
          });
          setErrors(errors);
          setLoading(false);
        } else if (saveResponse.apiError) {
          setErrors([{ level: 'FATAL', message: saveResponse.apiError.message }])
          setLoading(false);
        }
      });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  if (loading) {
    return <ProgressSplash />;
  }

  return (
    <>
      <ConfirmationDialog />
      <ItemOptionsDialog />
      <Box display='flex'>
        <MenuBar />
        <Drawer variant="permanent">
          <Box sx={{ mt: `${MENU_HEIGHT}px`, ...SCROLL_SX }} id='tree-scroll-container'>
            <NavigationPane />
          </Box>
        </Drawer>
        <Box sx={{ mt: `${MENU_HEIGHT}px`, width: 1 }}>
          <EditorArea />
        </Box>
        {hasErrors && <Drawer variant="permanent" anchor="right">
          <Box sx={{ mt: `${MENU_HEIGHT}px`, ...SCROLL_SX }}>
            <ErrorPane />
          </Box>
        </Drawer>}
      </Box>
    </>
  );
};

export default ComposerLayoutView;
