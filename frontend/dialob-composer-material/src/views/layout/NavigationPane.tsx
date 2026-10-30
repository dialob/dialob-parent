import React from 'react';
import { Box, ToggleButton, ToggleButtonGroup } from '@mui/material';
import { FormattedMessage } from 'react-intl';
import NavigationTreeView from '../../components/tree/NavigationTreeView';
import { useComposer } from '../../dialob';
import { TreeItemDisplay, useEditor } from '../../editor';
import { MENU_HEIGHT, SCROLL_SX, SCROLLBAR_WIDTH } from '../../theme/siteTheme';

const NavigationPane: React.FC = () => {
  const { form } = useComposer();
  const { editor, setTreeItemDisplay } = useEditor();
  const hasItems = Object.values(form.data).length > 1;

  if (!hasItems) {
    return null;
  }

  const handleDisplayChange = (_event: React.MouseEvent<HTMLElement>, value: TreeItemDisplay | null) => {
    if (value) {
      setTreeItemDisplay(value);
    }
  };

  return (
    <Box sx={{
      mt: `${MENU_HEIGHT}px`,
      height: `calc(100% - ${MENU_HEIGHT}px)`,
      display: 'flex',
      flexDirection: 'column',
      minHeight: 0,
      overflow: 'hidden',
    }}>
      <Box sx={{
        flexShrink: 0,
        display: 'flex',
        pt: 1,
        pl: 1,
        pb: 0.5,
        pr: (theme) => `calc(${theme.spacing(1)} + ${SCROLLBAR_WIDTH})`,
      }}>
        <ToggleButtonGroup
          value={editor.treeItemDisplay}
          exclusive
          fullWidth
          size='small'
          onChange={handleDisplayChange}
        >
          <ToggleButton value='label'>
            <FormattedMessage id='tree.display.label' />
          </ToggleButton>
          <ToggleButton value='id'>
            <FormattedMessage id='tree.display.id' />
          </ToggleButton>
        </ToggleButtonGroup>
      </Box>
      <Box id='tree-scroll-container' sx={{ flex: 1, minHeight: 0, ...SCROLL_SX }}>
        <NavigationTreeView />
      </Box>
    </Box>
  );
};

export default NavigationPane;
