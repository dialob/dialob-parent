import React from 'react';
import { Box } from '@mui/material';
import { DialobItem, DialobItems, useComposer } from '../dialob';
import { useEditor } from '../editor';
import Items from '../items';
import PageTabs from './PageTabs';
import { AddItemMenu } from '../items/ItemComponents';
import { ItemConfig } from '../defaults/types';
import { useBackend } from '../backend/useBackend';


const createChildren = (items: DialobItems, activePage?: DialobItem, itemConfig?: ItemConfig) => {
  if (!activePage || !activePage.items) {
    return null;
  }
  return (
    activePage.items
      .map((itemId: string) => items[itemId])
      .map((item: DialobItem) => Items.itemFactory(item, itemConfig))
  );
};

const Editor: React.FC = () => {
  const { form } = useComposer();
  const { editor } = useEditor();
  const { config } = useBackend();

  return (
    <Box id='scroll-container'>
      <PageTabs items={form.data} />
      {createChildren(form.data, editor.activePage, config.itemEditors)}
      {editor.activePage && <Box sx={{ mb: 2 }}><AddItemMenu item={editor.activePage} /></Box>}
    </Box>
  );
};

export default Editor;
